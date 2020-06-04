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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;


import de.uni_due.paluno.se.palaver.R;

import de.uni_due.paluno.se.palaver.room.Friend;
import de.uni_due.paluno.se.palaver.room.PalaverDatabase;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences loginUser_SP = this.getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        boolean isLogin = loginUser_SP.getBoolean("login", false);
        if(isLogin) {
            String senderName = remoteMessage.getData().get("sender");
            String preview = remoteMessage.getData().get("preview");

            Friend sender = new Friend(senderName);

            // add the new sender to DB
            List<Friend> friendList = PalaverDatabase.getInstance(this).getFriendDao().getFriendList();
            if(!friendList.contains(sender)) {
                PalaverDatabase.getInstance(this).getFriendDao().addFriend(sender);

                // send broadcast to update friend list fragment
                Intent new_friend_intent = new Intent();
                new_friend_intent.setAction("de.uni_due.paluno.se.palaver.broadcast_NEW_FRIEND");
                sendBroadcast(new_friend_intent);
            }

            createNotificationChannel();

            // define the notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, "id")
                            .setContentTitle(senderName)
                            .setContentText(preview)
                            .setSmallIcon(R.drawable.logo)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());

            // send broadcast
            Intent chat_intent = new Intent();
            chat_intent.setAction("de.uni_due.paluno.se.palaver.broadcast_NEW_MESSAGE");
            sendBroadcast(chat_intent);
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
}
