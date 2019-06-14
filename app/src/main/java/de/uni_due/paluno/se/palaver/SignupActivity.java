package de.uni_due.paluno.se.palaver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

public class SignupActivity extends AppCompatActivity {

    Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyClass.getHttpQueues().cancelAll("Register_Request");
    }


    public void onClickRegister(View v)
    {
        volley_Post(v);
    }

    public void volley_Post(View v){
        String url="http://palaver.se.paluno.uni-due.de/api/user/register";
        EditText Text1=findViewById(R.id.user);
        EditText Text2=findViewById(R.id.password1);
        EditText Text3=findViewById(R.id.password2);

        if(!Text2.getText().toString().equals(Text3.getText().toString())){
            Toast.makeText(getApplicationContext(),"Error:passwords are different.",Toast.LENGTH_SHORT).show();
        }

        if(Text2.getText().toString().isEmpty()||Text3.getText().toString().isEmpty()||Text1.getText().toString().isEmpty()){

            Toast.makeText(getApplicationContext(),"Inputs can't be empty",Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,String> map=new HashMap<>();
            map.put("Username",Text1.getText().toString());
            map.put("Password",Text2.getText().toString());

            //System.out.println(Text1.getText().toString()+" "+Text2.getText().toString()+" "+ Text3.getText().toString());
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
                                System.out.println(number +  "  " + info + " test ");
                                if(number.equals("1")) {

                                    Toast.makeText(getApplicationContext(),info,Toast.LENGTH_SHORT).show();

                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            Intent test2 =new Intent(SignupActivity.this, MainActivity.class);
                                            startActivity(test2);
                                            SignupActivity.this.finish();
                                        }}, 3000);
                                }

                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Error:"+info,Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SignupActivity.this,
                                    error.toString(), Toast.LENGTH_LONG).show();

                        }
                    });
           jsonArrayReq.setTag("Register_Request");
            VolleyClass.getHttpQueues().add(jsonArrayReq);
        }

    }

    public void onClickBack(View v){
        Intent test= new Intent(SignupActivity.this,MainActivity.class);
        startActivity(test);
        SignupActivity.this.finish();
    }

}
