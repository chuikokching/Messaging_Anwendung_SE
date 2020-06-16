package de.uni_due.paluno.se.palaver.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.uni_due.paluno.se.palaver.R;

import de.uni_due.paluno.se.palaver.Volley_Connect;
import de.uni_due.paluno.se.palaver.room.Chat;
import de.uni_due.paluno.se.palaver.room.Friend;
import de.uni_due.paluno.se.palaver.room.PalaverDatabase;
import de.uni_due.paluno.se.palaver.room.SendTypeEnum;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPreferences loginUser_SP = this.getSharedPreferences("loginUser", Context.MODE_PRIVATE);

        boolean isLogin = loginUser_SP.getBoolean("login", false);
        if(isLogin) {
            String senderName = remoteMessage.getData().get("sender");
            String preview = remoteMessage.getData().get("preview");

            // add the new sender to DB
            List<Friend> friendList = PalaverDatabase.getInstance(this).getFriendDao().getFriendList();

            List<String> friendNameList = new ArrayList<>();
            for (Friend friend : friendList) {
                friendNameList.add(friend.getNickname());
            }

            if(!friendNameList.contains(senderName)) {
                Friend sender = new Friend(senderName);
                PalaverDatabase.getInstance(this).getFriendDao().addFriend(sender);

                // send broadcast to update friend list fragment
                Intent new_friend_intent = new Intent();
                new_friend_intent.setAction("de.uni_due.paluno.se.palaver.broadcast_NEW_FRIEND");
                sendBroadcast(new_friend_intent);
            }

            // add the new chat to DB
            List<Chat> chatLists = PalaverDatabase.getInstance(getApplicationContext()).getChatDao().getChatListByName(senderName);
            if(chatLists.size() == 0) {
                this.getChatListFromServer(senderName);
            } else {
                this.updateChatDB(senderName);
            }

            createNotificationChannel();

            // define the notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, "id")
                            .setContentTitle(senderName)
                            .setContentText(preview)
                            .setSmallIcon(R.drawable.palaver_pic)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Channel";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Get all Chats from Server
     * @param friendName The name of friend
     */
    public void getChatListFromServer(final String friendName) {
        SharedPreferences loginUser_SP = this.getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        final String username = loginUser_SP.getString("username", "");
        final String password = loginUser_SP.getString("password", "");
        String getUrl="http://palaver.se.paluno.uni-due.de/api/message/get";
        HashMap<String,String> getMap = new HashMap<>();
        getMap.put("Username", username);
        getMap.put("Password", password);
        getMap.put("Recipient", friendName);
        JSONObject getJSONObject = new JSONObject(getMap);
        JsonObjectRequest getJSONArrayRequest = new JsonObjectRequest(Request.Method.POST, getUrl, getJSONObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("MsgType") == 1) {
                        JSONArray messages = response.getJSONArray("Data");
                        for (int i = 0 ; i < messages.length() ; i++) {
                            JSONObject message = (JSONObject) messages.get(i);
                            String sender = message.getString("Sender");
                            String recipient = message.getString("Recipient");
                            String mimeType = message.getString("Mimetype");
                            String data = message.getString("Data");
                            String dateTime = message.getString("DateTime");
                            if(sender.equals(username)) {
                                Chat newChat = new Chat(recipient, mimeType, data, dateTime, SendTypeEnum.TYPE_SEND.getSendType());
                                PalaverDatabase.getInstance(getApplicationContext()).getChatDao().addChat(newChat);
                            } else {
                                Chat newChat = new Chat(sender, mimeType, data, dateTime, SendTypeEnum.TYPE_RECEIVE.getSendType());
                                PalaverDatabase.getInstance(getApplicationContext()).getChatDao().addChat(newChat);
                            }

                            // send broadcast
                            Intent chat_intent = new Intent();
                            chat_intent.setAction("de.uni_due.paluno.se.palaver.broadcast_NEW_MESSAGE");
                            sendBroadcast(chat_intent);
                        }
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
        Volley_Connect.getVolleyQueues().add(getJSONArrayRequest);
    }

    /**
     * Get new Chats from Server
     * @param friendName The name of friend
     */
    public void updateChatDB(final String friendName) {
        SharedPreferences loginUser_SP = this.getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        final String username = loginUser_SP.getString("username", "");
        final String password = loginUser_SP.getString("password", "");

        List<Chat> chatListOfSender = PalaverDatabase.getInstance(getApplicationContext()).getChatDao().getChatListByName(friendName);

        String getMessageUrl = "";
        HashMap<String,String> getMessageMap = new HashMap<>();

        getMessageUrl = "http://palaver.se.paluno.uni-due.de/api/message/getoffset";
        String offset = chatListOfSender.get(chatListOfSender.size() - 1).getDateTime();
        getMessageMap.put("Username", username);
        getMessageMap.put("Password", password);
        getMessageMap.put("Recipient", friendName);
        getMessageMap.put("Offset", offset);

        JSONObject getMessageJSON = new JSONObject(getMessageMap);
        JsonObjectRequest getMessageJSONRequest = new JsonObjectRequest(Request.Method.POST, getMessageUrl, getMessageJSON, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("MsgType") == 1) {
                        JSONArray messages = response.getJSONArray("Data");

                        List<Chat> chatLists = PalaverDatabase.getInstance(getApplicationContext()).getChatDao().getChatListByName(friendName);
                        Chat lastChat = chatLists.get(chatLists.size() - 1);
                        JSONObject firstMessageFromServer = (JSONObject)messages.get(0);
                        int i = 0;
                        String dateTimeOfLastChat = lastChat.getDateTime().substring(0, lastChat.getDateTime().indexOf("."));
                        String dateTimeOfMessage = firstMessageFromServer.getString("DateTime")
                                .substring(0, firstMessageFromServer.getString("DateTime").indexOf("."));
                        if(lastChat.getData().equals(firstMessageFromServer.getString("Data")) &&
                                dateTimeOfLastChat.equals(dateTimeOfMessage)) {
                            i = 1;
                        }

                        for ( ; i < messages.length() ; i++) {
                            JSONObject message = (JSONObject) messages.get(i);
                            String sender = message.getString("Sender");
                            String recipient = message.getString("Recipient");
                            String mimeType = message.getString("Mimetype");
                            String data = message.getString("Data");
                            String dateTime = message.getString("DateTime");

                            // add to DB
                            if(sender.equals(username)) {
                                Chat newChat = new Chat(recipient, mimeType, data, dateTime, SendTypeEnum.TYPE_SEND.getSendType());
                                PalaverDatabase.getInstance(getApplicationContext()).getChatDao().addChat(newChat);
                            } else {
                                Chat newChat = new Chat(sender, mimeType, data, dateTime, SendTypeEnum.TYPE_RECEIVE.getSendType());
                                PalaverDatabase.getInstance(getApplicationContext()).getChatDao().addChat(newChat);
                            }

                            // send broadcast
                            Intent chat_intent = new Intent();
                            chat_intent.setAction("de.uni_due.paluno.se.palaver.broadcast_NEW_MESSAGE");
                            sendBroadcast(chat_intent);
                            sendBroadcast(chat_intent);
                        }
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
