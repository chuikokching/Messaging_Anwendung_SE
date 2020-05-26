package de.uni_due.paluno.se.palaver;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    Handler back=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void onClickBack(View v){
        Intent test= new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(test);
        SignUpActivity.this.finish();
    }

    public void onClickRegister(View v)
    {
        volley_SignUp(v);
    }

    public void volley_SignUp(View v)
    {
        String signup_url="http://palaver.se.paluno.uni-due.de/api/user/register";
        EditText Text_username1=findViewById(R.id.username_signup);
        EditText Text_password2=findViewById(R.id.password_signup1);
        EditText Text_password3=findViewById(R.id.password_signup2);

        //System.out.println(" gogogoo !!!");
        if(!Text_password2.getText().toString().equals(Text_password3.getText().toString())){
            Toast.makeText(getApplicationContext(),"Error: check password again!!!",Toast.LENGTH_SHORT).show();
        }


        if(Text_password2.getText().toString().isEmpty()||Text_password3.getText().toString().isEmpty()||Text_username1.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please input your account information!!!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,String> map=new HashMap<>();
            map.put("Username",Text_username1.getText().toString());
            map.put("Password",Text_password2.getText().toString());

            System.out.println(Text_username1.getText().toString()+" "+Text_password2.getText().toString()+" "+ Text_password3.getText().toString());
            JSONObject jsonObject=new JSONObject(map);
            JsonObjectRequest jsonArrayReq=new JsonObjectRequest(
                    Request.Method.POST,
                    signup_url,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Output from server: ", response.toString());
                            System.out.println("Output from server: "+ response.toString());

                            try {
                                String number= response.getString("MsgType");
                                String message = response.getString("Info");

                                if(number.equals("1")) {
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                    back.postDelayed(new Runnable() {
                                        public void run() {
                                            Intent test2 =new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(test2);
                                            SignUpActivity.this.finish();
                                        }}, 3000);
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Error: "+message,Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Output from Error: "+ error.toString());
                            Toast.makeText(SignUpActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
            jsonArrayReq.setTag("Register_Request");
            Volley_Connect.getVolleyQueues().add(jsonArrayReq);
        }

    }
}