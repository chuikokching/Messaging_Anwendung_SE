package de.uni_due.paluno.se.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import de.uni_due.paluno.se.palaver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    SharedPreferences speicher;
    SharedPreferences.Editor speicher_Editor;

    EditText Text1;
    EditText Text2;
    Boolean loginsave;

    RelativeLayout interface1;
    RelativeLayout interface2;
    Handler handler=new Handler();
    Runnable run1 = new Runnable() {
        @Override
        public void run() {



            interface1.setVisibility(View.VISIBLE);
            interface2.setVisibility(View.VISIBLE);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Log.i(msg,"onCreate");

        speicher= getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_Editor=speicher.edit();
        //System.out.println("executed " + speicher.getString("username","") + "  " + speicher.getString("password","") );

        Text1=(EditText) findViewById(R.id.user_main);
        Text2=(EditText) findViewById(R.id.password);

        if(speicher.getBoolean("login",false))
        {
            Text1.setText(speicher.getString("username",null));
            Text2.setText(speicher.getString("password",null));
        }

        interface1=findViewById(R.id.interface1);
        interface2=findViewById(R.id.interface2);

        handler.postDelayed(run1,2000);

    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyClass.getHttpQueues().cancelAll("Login_Request");
        VolleyClass.getHttpQueues().cancelAll("PushToken_Request");
    }



    public void onClickSignUp(View v){
        Intent test1 =new Intent(MainActivity.this,SignupActivity.class);
        startActivity(test1);
        MainActivity.this.finish();
    }



    public void onClickConnect(View v){
                    String url="http://palaver.se.paluno.uni-due.de/api/user/validate";

                    final String name=Text1.getText().toString();
                    final String pass=Text2.getText().toString();

                    HashMap<String,String> map=new HashMap<>();
                    map.put("Username",name);
                    map.put("Password",pass);

                    //System.out.println(Text1.getText().toString()+" "+Text2.getText().toString()+" ");

                    JSONObject jsonObject=new JSONObject(map);
                    JsonObjectRequest jsonArrayReq=new JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //Log.i("Output from server: ", response.toString());
                                    //System.out.println("Output from server: "+ response.toString());

                                    try {
                                            String number= response.getString("MsgType");
                                            String info = response.getString("Info");
                                            if(number.equals("1"))
                                            {

                                                Toast.makeText(getApplicationContext(),info,Toast.LENGTH_SHORT).show();

                                                autoLogin(name,pass);

                                                getToken();

                                            }
                                            else
                                            {

                                                Toast.makeText(getApplicationContext(),info,Toast.LENGTH_SHORT).show();
                                            }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(),
                                                "Error: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //VolleyLog.d(TAG, "Error: " + error.getMessage());
                                    System.out.println("Output from Error: "+ error.toString());
                                    Toast.makeText(MainActivity.this,
                                            error.toString(), Toast.LENGTH_LONG).show();

                                }
                            });
                    jsonArrayReq.setTag("Login_Request");
                    VolleyClass.getHttpQueues().add(jsonArrayReq);
    }
    

    private void autoLogin(String name,String pass){

        speicher_Editor.putBoolean("login",true);
        speicher_Editor.putString("username", name);
        speicher_Editor.putString("password", pass);
        speicher_Editor.commit();

    }

    public void getToken(){

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                // let new Instance ID token
                String token = task.getResult().getToken();

                // Log and toast
                String msg = R.string.msg_token_fmt+token;

                Log.d(TAG, token);

                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                System.out.println(msg + " test in console!!!!!!!!!!");

                pushToken(token);
            }
        });
    }

    public void pushToken(String token){
        String url="http://palaver.se.paluno.uni-due.de/api/user/pushtoken";
        String name=speicher.getString("username",null);
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
                            String info = response.getString("Info");
                            if(number.equals("1"))
                            {

                                //Toast.makeText(getApplicationContext(),info,Toast.LENGTH_SHORT).show();

                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        Intent test2 =new Intent(MainActivity.this, UserInterfaceActivity.class);
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
        VolleyClass.getHttpQueues().add(jsonArrayReq);

    }

    /*protected void onStart(){
        super.onStart();
        Log.i(msg,"onStart");
    }

    protected void onResume(){
        super.onResume();
        Log.i(msg,"onResume");
    }

    protected void onPause(){
        super.onPause();
        Log.i(msg,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(msg,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(msg,"onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(msg,"onRestart");
    }*/



}
