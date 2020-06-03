package de.uni_due.paluno.se.palaver.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import java.util.List;
import java.util.UUID;

import de.uni_due.paluno.se.palaver.R;
import de.uni_due.paluno.se.palaver.Volley_Connect;
import de.uni_due.paluno.se.palaver.room.Chat;
import de.uni_due.paluno.se.palaver.room.Friend;
import de.uni_due.paluno.se.palaver.room.PalaverDatabase;
import de.uni_due.paluno.se.palaver.room.SendTypeEnum;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final String PUSHTOKEN_REQUEST_TAG = "PushToken_Request";
    SharedPreferences loginUser_SP;

    @Override
    public void onNewToken(@NonNull String s) {
        final String pushTokenUrl = "http://palaver.se.paluno.uni-due.de/api/user/pushtoken";
        final String name = loginUser_SP.getString("username", "");
        final String pass = loginUser_SP.getString("password", "");

        HashMap<String, String> newTokenMap = new HashMap<>();
        newTokenMap.put("Username", name);
        newTokenMap.put("Password", pass);
        newTokenMap.put("PushToken", s);

        JSONObject jsonObject = new JSONObject(newTokenMap);
        JsonObjectRequest jsonArrayReq = new JsonObjectRequest(
                Request.Method.POST,
                pushTokenUrl,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String msgType = response.getString("MsgType");
                            String info = response.getString("Info");
                            if (Integer.parseInt(msgType) == 1) {
                                Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "PushToken isn't successful!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        jsonArrayReq.setTag(PUSHTOKEN_REQUEST_TAG);
        Volley_Connect.getVolleyQueues().add(jsonArrayReq);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        int id = UUID.randomUUID().hashCode();
        super.onMessageReceived(remoteMessage);
        SharedPreferences loginUser_SP = this.getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        boolean isLogin = loginUser_SP.getBoolean("login", false);
        if(isLogin == true) {
            String senderName = remoteMessage.getData().get("sender");
            String preview = remoteMessage.getData().get("preview");

            Friend sender = new Friend(senderName);

            // add the new sender to DB
            List<Friend> friendList = PalaverDatabase.getInstance(this).getFriendDao().getFriendList();
            if(friendList.contains(sender) == false) {
                PalaverDatabase.getInstance(this).getFriendDao().addFriend(sender);
            }

            // get the message from sender and save to DB
            getMessageFromServer(senderName);

            initChannels(this);

            // define the notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, "default")
                            .setContentTitle(senderName)
                            .setContentText(preview)
                            .setAutoCancel(true);
            NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
            mNotificationManager.notify(id, mBuilder.build());

            // send broadcast to update friend list fragment
            Intent new_friend_intent = new Intent();
            new_friend_intent.setAction("new_friend");
            sendBroadcast(new_friend_intent);
        }
    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel");
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * get message from server
     * @param senderName The name of the sender
     */
    private void getMessageFromServer(final String senderName) {
        final String username = loginUser_SP.getString("username", "");
        final String password = loginUser_SP.getString("password", "");

        List<Chat> chatListOfSender = PalaverDatabase.getInstance(this).getChatDao().getChatListByName(senderName);

        String getMessageUrl = "";
        HashMap<String,String> getMessageMap = new HashMap<>();

        // if chatListOfSender is empty, use get API, otherwise use getOffset API
        if(chatListOfSender.size() == 0) {
            getMessageUrl = "http://palaver.se.paluno.uni-due.de/api/message/get";
            getMessageMap.put("Username", username);
            getMessageMap.put("Password", password);
            getMessageMap.put("Recipient", senderName);
        } else {
            getMessageUrl = "http://palaver.se.paluno.uni-due.de/api/message/getoffset";
            String offset = chatListOfSender.get(chatListOfSender.size() - 1).getDateTime();
            getMessageMap.put("Username", username);
            getMessageMap.put("Password", password);
            getMessageMap.put("Recipient", senderName);
            getMessageMap.put("Offset", offset);
        }

        JSONObject getMessageJSON = new JSONObject(getMessageMap);
        JsonObjectRequest getMessageJSONRequest = new JsonObjectRequest(Request.Method.POST, getMessageUrl, getMessageJSON, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("MsgType") == 1) {
                        JSONArray messages = response.getJSONArray("Data");
                        for (int i = 0 ; i < messages.length() ; i++) {
                            JSONObject message = (JSONObject) messages.get(i);
                            String sender = message.getString("Sender");
                            String recipient = message.getString("Recipient");
                            String mimeType = message.getString("Mimetype");
                            String data = message.getString("Data");
                            String dateTime = message.getString("DateTime");

                            // add to DB
                            if(sender.equals(username) == true) {
                                Chat newChat = new Chat(recipient, mimeType, data, dateTime, SendTypeEnum.TYPE_SEND.getSendType());
                                PalaverDatabase.getInstance(MyFirebaseMessagingService.this).getChatDao().addChat(newChat);
                            } else if(recipient.equals(username) == true){
                                Chat newChat = new Chat(recipient, mimeType, data, dateTime, SendTypeEnum.TYPE_RECEIVE.getSendType());
                                PalaverDatabase.getInstance(MyFirebaseMessagingService.this).getChatDao().addChat(newChat);
                            }
                        }

                        // send broadcast
                        Intent chat_intent = new Intent();
                        chat_intent.setAction("new_message");
                        sendBroadcast(chat_intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley_Connect.getVolleyQueues().add(getMessageJSONRequest);
    }
}
