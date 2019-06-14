package de.uni_due.paluno.se.palaver.Firebase;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override public void onMessageReceived(RemoteMessage message){
        //broadcast


        String from = message.getFrom();

        Map data = message.getData();

        Intent i = new Intent("de.uni_due.paluno.se.palaver.message." + data.get("whatever-key"));

        sendBroadcast(i); }
}
