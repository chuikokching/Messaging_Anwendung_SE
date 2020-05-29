package de.uni_due.paluno.se.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Handler change=new Handler();

    private static final String TAG = "MainActivity";

    SharedPreferences speicher;
    SharedPreferences.Editor speicher_Editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speicher= getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_Editor=speicher.edit();
    }

    public void onClickSignUp_Activity(View v){
        Intent test1 =new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(test1);
        MainActivity.this.finish();
    }

    protected void onStop() {
        super.onStop();
        Volley_Connect.getVolleyQueues().cancelAll("Login_Request");
        Volley_Connect.getVolleyQueues().cancelAll("PushToken_Request");
    }

    public void onClickLog_in(View v)
    {
        String login_url="http://palaver.se.paluno.uni-due.de/api/user/validate";
        EditText text1=findViewById(R.id.username_signin);
        EditText text2=findViewById(R.id.password_signin);

        final String name=text1.getText().toString();
        final String pass=text2.getText().toString();

        HashMap<String,String> map=new HashMap<>();
        map.put("Username",name);
        map.put("Password",pass);

        JSONObject jsonObject=new JSONObject(map);
        JsonObjectRequest jsonArrayReq=new JsonObjectRequest(
                Request.Method.POST,
                login_url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String number= response.getString("MsgType");
                            String message = response.getString("Info");
                            if(number.equals("1"))
                            {
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                autoLogin(name,pass);
                                getToken();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
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
                        System.out.println("Output from Error: "+ error.toString());
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        jsonArrayReq.setTag("Login_Request");
        Volley_Connect.getVolleyQueues().add(jsonArrayReq);
    }


    public void getToken(){

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                // new Instance ID token
                String token = task.getResult().getToken();
                // Log and toast
                //String msg = R.string.msg_token_fmt+token;
                Log.d(TAG, token);
                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                //pushToken(token);
                String url="http://palaver.se.paluno.uni-due.de/api/user/pushtoken";
                final String name=speicher.getString("username",null);
                String pass=speicher.getString("password",null);

                HashMap<String,String> map=new HashMap<>();
                map.put("Username",name);
                map.put("Password",pass);
                map.put("PushToken",token);

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
                                    //String info = response.getString("Info");
                                    if(number.equals("1"))
                                    {
                                        //Toast.makeText(getApplicationContext(),info,Toast.LENGTH_SHORT).show();
                                        change.postDelayed(new Runnable() {
                                            public void run() {
                                                Intent test2 =new Intent(MainActivity.this, User_Interface_Activity.class);
                                                startActivity(test2);
                                                MainActivity.this.finish();
                                            }}, 3000); }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"PushToken isn't successful!",Toast.LENGTH_SHORT).show();
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
                                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                                System.out.println("Output from Error: "+ error.toString());
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                            }
                        });
                jsonArrayReq.setTag("Pushtoken_Request");
                Volley_Connect.getVolleyQueues().add(jsonArrayReq);
            }
        });
    }

    private void autoLogin(String name,String pass){

        speicher_Editor.putBoolean("login",true);
        speicher_Editor.putString("username", name);
        speicher_Editor.putString("password", pass);

        speicher_Editor.commit();

    }
}