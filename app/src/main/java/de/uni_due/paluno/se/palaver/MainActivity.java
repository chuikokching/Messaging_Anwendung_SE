package de.uni_due.paluno.se.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    Button login_button;
    Button register_button;

    EditText username_text;
    EditText password_text;

    SharedPreferences loginUser_SP;
    SharedPreferences.Editor loginUser_SP_Editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_button = findViewById(R.id.login);
        register_button = findViewById(R.id.register);

        username_text = findViewById(R.id.username_signin);
        password_text = findViewById(R.id.password_signin);

        loginUser_SP = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        loginUser_SP_Editor = loginUser_SP.edit();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLog_in(v);
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(getApplication(), SignUpActivity.class);
                startActivity(signUpIntent);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Volley_Connect.getVolleyQueues().cancelAll("Login_Request");
        Volley_Connect.getVolleyQueues().cancelAll("PushToken_Request");
    }

    /**
     * Log in
     * @param v View
     */
    private void onClickLog_in(View v)
    {
        final String login_url="http://palaver.se.paluno.uni-due.de/api/user/validate";
        final String name=username_text.getText().toString();
        final String pass=password_text.getText().toString();

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
                            String msgType = response.getString("MsgType");
                            String message = response.getString("Info");
                            if (Integer.parseInt(msgType) == 1) {
                                Toast.makeText(MainActivity.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                save_SP(name, pass);
                                //getAndSendToken();
                            } else {
                                Toast.makeText(MainActivity.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        jsonArrayReq.setTag("Login_Request");
        Volley_Connect.getVolleyQueues().add(jsonArrayReq);
    }

    /**
     * save the information of user in Handy
     * @param name The username
     * @param pass The password
     */
    private void save_SP(String name,String pass){
        loginUser_SP_Editor.putBoolean("login",true);
        loginUser_SP_Editor.putString("username", name);
        loginUser_SP_Editor.putString("password", pass);
        loginUser_SP_Editor.commit();
    }

    /**
     * get and send Token
     */
    private void getAndSendToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                // new Instance ID token
                String token = task.getResult().getToken();

                final String pushTokenUrl = "http://palaver.se.paluno.uni-due.de/api/user/pushtoken";
                final String name = loginUser_SP.getString("username", "");
                final String pass = loginUser_SP.getString("password", "");

                HashMap<String, String> map = new HashMap<>();
                map.put("Username", name);
                map.put("Password", pass);
                map.put("PushToken", token);

                JSONObject jsonObject = new JSONObject(map);
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
                                        Toast.makeText(MainActivity.this.getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                                        Intent userInterfaceActivityIntent = new Intent(MainActivity.this, User_Interface_Activity.class);
                                        MainActivity.this.startActivity(userInterfaceActivityIntent);
                                        MainActivity.this.finish();
                                    } else {
                                        Toast.makeText(MainActivity.this.getApplicationContext(), "PushToken isn't successful!", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                jsonArrayReq.setTag("Pushtoken_Request");
                Volley_Connect.getVolleyQueues().add(jsonArrayReq);
            }
        });
    }
}