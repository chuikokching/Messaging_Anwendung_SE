package de.uni_due.paluno.se.palaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.uni_due.paluno.se.palaver.Adapter.Message;
import de.uni_due.paluno.se.palaver.Adapter.MessageAdapter;



public class ChatActivity extends AppCompatActivity {

    TextView username1;

    Intent intent;

    SharedPreferences speicher_fragment;

    SharedPreferences.Editor speicher_editor;

    EditText send_text;
    ImageButton send_btn;

    RecyclerView userMessage_list;

    List<Message> Message_list= new ArrayList<>();

    LinearLayoutManager linearLayoutManager;
    MessageAdapter messageAdapter;

    @Override
    protected void onStop() {
        super.onStop();
        VolleyClass.getHttpQueues().cancelAll("Send_Request");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        speicher_fragment = this.getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_editor = speicher_fragment.edit();

        Toolbar toolbar1= findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //messageAdapter = new MessageAdapter();

        userMessage_list = findViewById(R.id.recycleview_chat);
        userMessage_list.setHasFixedSize(true);

        LinearLayoutManager  lm=new LinearLayoutManager(getApplicationContext());
        lm.setStackFromEnd(true);
        userMessage_list.setLayoutManager(lm);


        send_btn = findViewById(R.id.btn_send);

        username1 = findViewById(R.id.username_chat);

        intent = getIntent();

        final String recipient=intent.getStringExtra("username");

        username1.setText(recipient);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    volley_send(v,recipient);
                    send_text.setText("");
            }
        });

    }




    public void volley_send(View v,String recipient){
        String url="http://palaver.se.paluno.uni-due.de/api/message/send";
        send_text = findViewById(R.id.text_send);


        if(send_text.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Inputs can't be empty",Toast.LENGTH_SHORT).show();
        }
        else{

            String user = speicher_fragment.getString("username", "");
            String pass = speicher_fragment.getString("password", "");


            HashMap<String,String> map=new HashMap<>();
            map.put("Username",user);
            map.put("Password",pass);
            map.put("Recipient",recipient);
            map.put("Mimetype","text/plain");
            map.put("Data",send_text.getText().toString());

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
                                System.out.println(number +  "  " + info + " test result ");
                                if(number.equals("1")) {

                                    Toast.makeText(getApplicationContext(),info,Toast.LENGTH_SHORT).show();

                                }

                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Error: "+info,Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                        }
                    });
            jsonArrayReq.setTag("Send_Request");
            VolleyClass.getHttpQueues().add(jsonArrayReq);
        }
    }
}
