package de.uni_due.paluno.se.palaver.Notificationhelper;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import de.uni_due.paluno.se.palaver.MainActivity;
import de.uni_due.paluno.se.palaver.R;

public class NotificationHelper {

    public static void displayNotification(Context context,String title,String text){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.star)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1,builder.build());


    }
}
