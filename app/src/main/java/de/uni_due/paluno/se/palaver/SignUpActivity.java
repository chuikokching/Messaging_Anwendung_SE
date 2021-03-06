package de.uni_due.paluno.se.palaver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    Button sign_up_button;
    Button back_button;

    final String REGISTER_REQUEST_TAG = "Register_Request";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sign_up_button = findViewById(R.id.sign_up_register);
        back_button = findViewById(R.id.back_to_main);

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volley_SignUp(v);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent test = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(test);
                finish();
            }
        });
    }

    /**
     * Sign Up
     * @param v View
     */
    public void volley_SignUp(View v)
    {
        String signUp_url="http://palaver.se.paluno.uni-due.de/api/user/register";
        EditText Text_username=findViewById(R.id.username_signup);
        EditText Text_password_1=findViewById(R.id.password_signup1);
        EditText Text_password_2=findViewById(R.id.password_signup2);

        if(!Text_password_1.getText().toString().equals(Text_password_2.getText().toString())){
            Toast.makeText(getApplicationContext(),"The two password fields don't match!",Toast.LENGTH_SHORT).show();
        }

        if(Text_password_1.getText().toString().isEmpty()||Text_password_2.getText().toString().isEmpty()||Text_username.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please input your account information!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,String> signUpMap=new HashMap<>();
            signUpMap.put("Username",Text_username.getText().toString());
            signUpMap.put("Password",Text_password_1.getText().toString());

            JSONObject jsonObject=new JSONObject(signUpMap);
            JsonObjectRequest jsonArrayReq=new JsonObjectRequest(
                    Request.Method.POST,
                    signUp_url,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String number = response.getString("MsgType");
                                String message = response.getString("Info");

                                if (Integer.parseInt(number) == 1) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            Intent main_intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(main_intent);
                                            SignUpActivity.this.finish();
                                        }
                                    }, 1000);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
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
                            System.out.println("Output from Error: " + error.toString());
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
            jsonArrayReq.setTag(REGISTER_REQUEST_TAG);
            Volley_Connect.getVolleyQueues().add(jsonArrayReq);
        }
    }
}