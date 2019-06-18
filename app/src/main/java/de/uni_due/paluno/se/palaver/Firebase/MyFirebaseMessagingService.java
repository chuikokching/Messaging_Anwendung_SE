package de.uni_due.paluno.se.palaver.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import de.uni_due.paluno.se.palaver.MainActivity;

import de.uni_due.paluno.se.palaver.R;

import static android.support.v4.content.ContextCompat.getSystemService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage message)
    {
        //broadcast
        super.onMessageReceived(message);

        //String title = message.getNotification().getTitle();
        String sender = message.getData().get("sender");
        String preview = message.getData().get("preview");
        // Log.i("tag","test = title :"+sender);
        //Log.i("tag","test = text :"+preview);


        createNotificationChannel();


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(R.drawable.star)
                .setContentTitle("Text from palaver")
                .setContentText(sender+" : "+preview)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,builder.build());


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

        //sendBroadcast(i);

}
