package de.uni_due.paluno.se.palaver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import de.uni_due.paluno.se.palaver.Adapter.Message;
import de.uni_due.paluno.se.palaver.Adapter.MessageAdapter;
import de.uni_due.paluno.se.palaver.Datenbank.Constant;
import de.uni_due.paluno.se.palaver.Datenbank.DBManager;
import de.uni_due.paluno.se.palaver.Datenbank.MysqliteHelper;
import de.uni_due.paluno.se.palaver.Firebase.MyFirebaseMessagingService;
import de.uni_due.paluno.se.palaver.Location.LocationUtils;


public class ChatActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    TextView username1;

    Bitmap bitmap;

    Intent intent;

    SharedPreferences speicher_fragment;

    SharedPreferences.Editor speicher_editor;

    EditText send_text;
    ImageButton send_btn;

    RecyclerView userMessage_list;

    List<Message> Message_list= new ArrayList<>();

    LinearLayoutManager linearLayoutManager;
    MessageAdapter messageAdapter;

    public MysqliteHelper helper;

    public String recipient="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        speicher_fragment = this.getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_editor = speicher_fragment.edit();

        Toolbar toolbar1= findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        helper = DBManager.getInstance(this);

        userMessage_list = (RecyclerView) findViewById(R.id.recycleview_chat);
        userMessage_list.setHasFixedSize(true);

        linearLayoutManager =new LinearLayoutManager(this);
        userMessage_list.setLayoutManager(linearLayoutManager);

        IntentFilter filter = new IntentFilter(MyFirebaseMessagingService.action);
        registerReceiver(broadcastReceiver, filter);

        send_btn = findViewById(R.id.btn_send);

        username1 = findViewById(R.id.username_chat);

        intent = getIntent();

        recipient=intent.getStringExtra("username");

        username1.setText(recipient);

        getMessage_fromDB();

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    volley_send(v,recipient);
                    send_text.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==438&& RESULT_OK == resultCode )
        {
            Uri uri = data.getData();
            Log.i("tag",uri + " test !!!");
            Cursor cursor = getContentResolver().query(uri, null, null, null,null);
            if (cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sender = intent.getExtras().getString("sender");
            requestMessage_DB(sender);
        }
    };

    public void send_locationMessage(String lat,String lng)
    {
        String url="http://palaver.se.paluno.uni-due.de/api/message/send";
        send_text = findViewById(R.id.text_send);

        String coordinate = lat + ","+ lng;
        final SQLiteDatabase db = helper.getWritableDatabase();
        final String user = speicher_fragment.getString("username", "");
        final String pass = speicher_fragment.getString("password", "");

            HashMap<String,String> map=new HashMap<>();
            map.put("Username",user);
            map.put("Password",pass);
            map.put("Recipient",recipient);
            map.put("Mimetype","location");
            map.put("Data",coordinate);

            JSONObject jsonObject=new JSONObject(map);
            JsonObjectRequest jsonArrayReq=new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String number= response.getString("MsgType");
                                String info = response.getString("Info");
                                if(number.equals("1")) {
                                    ContentValues values = new ContentValues();
                                    values.put("Sender",user);
                                    values.put("Recipient",recipient);
                                    values.put("Mimetype","location");
                                    values.put("Data",coordinate);
                                    long result = db.insert(Constant.getUserName()+"_"+recipient,null,values);
                                    if(result>0)
                                    {
                                        //Toast.makeText(getApplicationContext(),"Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        // Toast.makeText(getApplicationContext(),"failed"+info,Toast.LENGTH_SHORT).show();
                                    }
                                    Message message = new Message(user,recipient,coordinate,"location");
                                    Message_list.add(message);
                                    addMessage();
                                    fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                                    Log.i("tag","services terminate.");
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Error: "+info,Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //VolleyLog.d(TAG, "Error: " + error.getMessage());
                            System.out.println("Output from Error: "+ error.toString());
                            Toast.makeText(ChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                        }
                    });
            jsonArrayReq.setTag("SendLocation_Request");
            VolleyClass.getHttpQueues().add(jsonArrayReq);

    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            String lat = locationResult.getLastLocation().getLatitude()+"";
            String lng = locationResult.getLastLocation().getLongitude()+"";

            Log.i("tag","lat: "+locationResult.getLastLocation().getLatitude());
            Log.i("tag","lng: "+locationResult.getLastLocation().getLongitude());
            send_locationMessage(lat,lng);
        };
    };

    public void requestLocationUpdate()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PermissionChecker.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PermissionChecker.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();

            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(15000);
            locationRequest.setInterval(20000);


            fusedLocationProviderClient.requestLocationUpdates(locationRequest,mLocationCallback,getMainLooper());
        }
        else
        {
            callPermission();
        }
    }


    public void callPermission()
    {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        String rationale = "Please provide location permission so that you can get User location";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("location permission")
                .setSettingsDialogTitle("Warning");
        Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                requestLocationUpdate();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
                callPermission();
            }
        });
    }

    public void OnClick_location(View v)
    {
       //Intent test3 =new Intent(this, MapViewActivity.class);
        //startActivity(test3);
       // this.finish();
       callPermission();
//        String lat = "",lng="";
//        Location location = LocationUtils.getInstance(this).showLocation();
//        if (location!=null){
//            lat = location.getLatitude()+"";
//            lng = location.getLongitude()+"";
//            String address = "lat："+location.getLatitude()+"lng："+location.getLongitude();
//            Log.i("tag",address);
//        }
//        send_locationMessage(lat,lng);
//        LocationUtils.getInstance(this).removeLocationUpdatesListener();
    }

    public void OnClick_image(View v)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 438);
        //decode to bitmap

        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test);

        //convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        //base64 encode
        byte[] encode = Base64.encode(bytes,Base64.DEFAULT);
        String encodeString = new String(encode);
        Log.i("tag"," chat image : " +encodeString);

    }

    public void getMessage_fromDB()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.query(Constant.getUserName()+"_"+recipient,null,null,null,null,null,null);

        while(cursor.moveToNext())
        {
            String sender = cursor.getString(cursor.getColumnIndex("Sender"));
            String receiver = cursor.getString(cursor.getColumnIndex("Recipient"));
            String type = cursor.getString(cursor.getColumnIndex("Mimetype"));
            String data = cursor.getString(cursor.getColumnIndex("Data"));
            Message message = new Message(sender,receiver,data,type);
            Message_list.add(message);
        }
        addMessage();
    }


    public void addMessage()
    {
        messageAdapter = new MessageAdapter(Message_list);
        userMessage_list.setAdapter(messageAdapter);

    }


    public void volley_send(View v, final String recipient){
        String url="http://palaver.se.paluno.uni-due.de/api/message/send";
        send_text = findViewById(R.id.text_send);
        final String data = send_text.getText().toString();
        if(data.equals("")){
            Toast.makeText(getApplicationContext(),"Inputs can't be empty",Toast.LENGTH_SHORT).show();
        }
        else{
            final SQLiteDatabase db = helper.getWritableDatabase();
            final String user = speicher_fragment.getString("username", "");
            final String pass = speicher_fragment.getString("password", "");

            HashMap<String,String> map=new HashMap<>();
            map.put("Username",user);
            map.put("Password",pass);
            map.put("Recipient",recipient);
            map.put("Mimetype","text/plain");
            map.put("Data",data);

            JSONObject jsonObject=new JSONObject(map);
            JsonObjectRequest jsonArrayReq=new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String number= response.getString("MsgType");
                                String info = response.getString("Info");
                                if(number.equals("1")) {
                                    ContentValues values = new ContentValues();
                                    values.put("Sender",user);
                                    values.put("Recipient",recipient);
                                    values.put("Mimetype","text/plain");
                                    values.put("Data",data);
                                    //Toast.makeText(getApplicationContext(),info,Toast.LENGTH_SHORT).show();
                                    //requestMessage_DB(recipient);
                                    long result = db.insert(Constant.getUserName()+"_"+recipient,null,values);
                                    if(result>0)
                                    {
                                        //Toast.makeText(getApplicationContext(),"Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                       // Toast.makeText(getApplicationContext(),"failed"+info,Toast.LENGTH_SHORT).show();
                                    }
                                    Message message = new Message(user,recipient,data,"text/plain");
                                   // Log.i("tag"," send successfully!!! test after "+ message.getData());
                                    Message_list.add(message);
                                    addMessage();

                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Error: "+info,Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //VolleyLog.d(TAG, "Error: " + error.getMessage());
                            System.out.println("Output from Error: "+ error.toString());
                            Toast.makeText(ChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                        }
                    });
            jsonArrayReq.setTag("Send_Request");
            VolleyClass.getHttpQueues().add(jsonArrayReq);
        }
    }


    public void requestMessage_DB(final String sender) {
        String user = speicher_fragment.getString("username", "");
        String pass = speicher_fragment.getString("password", "");
        final SQLiteDatabase db = helper.getWritableDatabase();

        String url = "http://palaver.se.paluno.uni-due.de/api/message/get";

        HashMap<String, String> map = new HashMap<>();
        map.put("Username", user);
        map.put("Password", pass);
        map.put("Recipient", sender);

        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonArrayReq = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String number = response.getString("MsgType");
                            String info = response.getString("Info");
                            JSONArray data = response.getJSONArray("Data");
                            ContentValues values = new ContentValues();
                            if (number.equals("1")) {
                                JSONObject temp = null;
                                if (data.length() != 0) {
                                    temp = data.getJSONObject(data.length() - 1);
                                    values.put("Sender", temp.get("Sender").toString());
                                    values.put("Recipient", temp.get("Recipient").toString());
                                    values.put("Mimetype", temp.get("Mimetype").toString());
                                    values.put("Data", temp.get("Data").toString());

                                    //Log.i("tag", data.length() + " and listsize "+ Message_list.size() + " " + temp.get("Sender").toString() + " " + temp.get("Recipient").toString() + " " + temp.get("Data").toString());
                                    long result = db.insert(Constant.getUserName() + "_" + sender, null, values);
                                    if (result > 0) {
                                        //Log.i("tag", "--------------Successfully in DB----------");
                                        //Toast.makeText(getApplicationContext(),"Successfully in DB while inserting newest message",Toast.LENGTH_SHORT).show();
                                    } else {
                                        //Log.i("tag", "--------------failed----------");
                                        //Toast.makeText(getApplicationContext(),"failed"+info,Toast.LENGTH_SHORT).show();
                                    }

                                    Message message = new Message(temp.get("Sender").toString(),temp.get("Recipient").toString(),temp.get("Data").toString(),temp.get("Mimetype").toString());
                                    Message_list.add(message);
                                    addMessage();

                                }
                                Toast.makeText(getApplicationContext(),"Info: "+info,Toast.LENGTH_SHORT).show();
                            } else {
                                //Log.i("tag", "--------------failed----------");
                                Toast.makeText(getApplicationContext(),"Info: "+info,Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                           // Log.i("tag", "--------------failed----------");
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        System.out.println("Output from Error: " + error.toString());
                        //Log.i("tag", "--------------failed----------");
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

                    }
                });

        jsonArrayReq.setTag("getMessagelist_Request");
        VolleyClass.getHttpQueues().add(jsonArrayReq);
    }

    protected void onStart()
    {
        super.onStart();
        Log.i("tag","-----------------------onSTart-------------------------");
    }

    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
    @Override
    protected void onStop() {
        super.onStop();
        VolleyClass.getHttpQueues().cancelAll("Send_Request");
        VolleyClass.getHttpQueues().cancelAll("SendLocation_Request");
    }
}
