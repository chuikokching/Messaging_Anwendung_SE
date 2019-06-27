package de.uni_due.paluno.se.palaver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


public class ChatActivity extends AppCompatActivity {

    TextView username1;

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
    protected void onStop() {
        super.onStop();
        VolleyClass.getHttpQueues().cancelAll("Send_Request");
    }

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

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("tag",intent.getExtras().getString("data"));
        }
    };


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

                                    Log.i("tag"," send successfully!!! "+ data );
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
                                        Toast.makeText(getApplicationContext(),"Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"failed"+info,Toast.LENGTH_SHORT).show();
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

    protected void onStart()
    {
        super.onStart();
        Log.i("tag","-----------------------onSTart-------------------------");
    }

    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
