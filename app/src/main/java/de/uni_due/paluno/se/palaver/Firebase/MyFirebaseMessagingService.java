package de.uni_due.paluno.se.palaver.Firebase;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import de.uni_due.paluno.se.palaver.MainActivity;
import de.uni_due.paluno.se.palaver.Notificationhelper.NotificationHelper;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage message)
    {
        //broadcast
        super.onMessageReceived(message);


        if(message.getNotification()!=null)
        {
            String title = message.getNotification().getTitle();
            String text = message.getNotification().getBody();
            Log.i("tag","test = title :"+title);
            Log.i("tag","test = text :"+text);


            NotificationHelper.displayNotification(getApplicationContext(),"Text from palaver",message.getData().toString());
        }

        //sendBroadcast(i);
    }
}
