package de.uni_due.paluno.se.palaver.firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.uni_due.paluno.se.palaver.MainActivity;
import de.uni_due.paluno.se.palaver.User_Interface_Activity;
import de.uni_due.paluno.se.palaver.Volley_Connect;

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
}
