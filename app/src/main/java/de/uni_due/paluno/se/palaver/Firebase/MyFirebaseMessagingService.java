package de.uni_due.paluno.se.palaver.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.uni_due.paluno.se.palaver.Datenbank.Constant;
import de.uni_due.paluno.se.palaver.Datenbank.DBManager;
import de.uni_due.paluno.se.palaver.Datenbank.MysqliteHelper;
import de.uni_due.paluno.se.palaver.MainActivity;

import de.uni_due.paluno.se.palaver.R;
import de.uni_due.paluno.se.palaver.VolleyClass;

import static android.support.v4.content.ContextCompat.getSystemService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String action = "jason.broadcast.action";
    SharedPreferences speicher_fragment;

    public MysqliteHelper helper;

    SharedPreferences.Editor speicher_editor;

    @Override
    public void onMessageReceived(RemoteMessage message)
    {
        //broadcast
        super.onMessageReceived(message);

        helper = DBManager.getInstance(this);

        speicher_fragment = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_editor = speicher_fragment.edit();

        String sender = message.getData().get("sender");
        String preview = message.getData().get("preview");

        String user = speicher_fragment.getString("username", "");
        if(!user.equals(sender))
        {
            createNotificationChannel();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "id")
                    .setSmallIcon(R.drawable.star)
                    .setContentTitle("Text from palaver")
                    .setContentText(sender+" : "+preview)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1,builder.build());

            //requestMessage_DB(sender);

            Intent intent = new Intent(action);
            intent.putExtra("sender",sender);
            Log.i("tag","broadcast sent!!!!!!");
            sendBroadcast(intent);

        }

    }

        private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Channel";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("id", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
    public void requestMessage_DB(final String sender)
    {
        String user = speicher_fragment.getString("username", "");
        String pass = speicher_fragment.getString("password", "");
        final SQLiteDatabase db = helper.getWritableDatabase();

        String url="http://palaver.se.paluno.uni-due.de/api/message/get";

        HashMap<String,String> map=new HashMap<>();
        map.put("Username",user);
        map.put("Password",pass);
        map.put("Recipient",sender);

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
                            JSONArray data = response.getJSONArray("Data");
                            ContentValues values = new ContentValues();
                            if(number.equals("1")) {
                                JSONObject temp = null;
                                    if(data.length() != 0)
                                    {
                                        temp = data.getJSONObject(data.length()-1);
                                        values.put("Sender",temp.get("Sender").toString());
                                        values.put("Recipient",temp.get("Recipient").toString());
                                        values.put("Mimetype",temp.get("Mimetype").toString());
                                        values.put("Data",temp.get("Data").toString());

                                        Log.i("tag",data.length() +" "+temp.get("Sender").toString()+" "+temp.get("Recipient")+" "+ temp.get("Data").toString());
                                        long result = db.insert(Constant.getUserName()+"_"+sender,null,values);
                                        if(result>0)
                                        {
                                            Log.i("tag","--------------Successfully in DB----------");
                                            //Toast.makeText(getActivity(),"Successfully",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Log.i("tag","--------------failed----------");
                                            //Toast.makeText(getActivity(),"failed"+info,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                //Toast.makeText(getActivity(),"Info: "+info,Toast.LENGTH_SHORT).show();
                            }

                            else
                            {
                                Log.i("tag","--------------failed----------");
                                //Toast.makeText(getActivity(),"Info: "+info,Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("tag","--------------failed----------");
                           // Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        System.out.println("Output from Error: "+ error.toString());
                        Log.i("tag","--------------failed----------");
                        //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();

                    }
                });

        jsonArrayReq.setTag("getMessagelist_Request");
        VolleyClass.getHttpQueues().add(jsonArrayReq);
    }

}
