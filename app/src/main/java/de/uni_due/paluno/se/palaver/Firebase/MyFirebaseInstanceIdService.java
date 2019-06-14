package de.uni_due.paluno.se.palaver.Firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import de.uni_due.paluno.se.palaver.R;

public class MyFirebaseInstanceIdService {
    private static final String REG_TOKEN="REG_TOKEN";
    private static final String TAG = "MainActivity";

//    public void onTokenRefresh(){
//        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.d(REG_TOKEN,token);
//    }
    public void get(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();

                // Log and toast
                //String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, token);
                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
