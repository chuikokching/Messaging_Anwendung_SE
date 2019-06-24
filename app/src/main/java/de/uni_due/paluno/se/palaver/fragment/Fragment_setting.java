package de.uni_due.paluno.se.palaver.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import de.uni_due.paluno.se.palaver.Datenbank.Constant;
import de.uni_due.paluno.se.palaver.Datenbank.DBManager;
import de.uni_due.paluno.se.palaver.Datenbank.MysqliteHelper;
import de.uni_due.paluno.se.palaver.MainActivity;

import de.uni_due.paluno.se.palaver.VolleyClass;
import de.uni_due.paluno.se.palaver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class Fragment_setting extends Fragment {

    SharedPreferences speicher_fragment;

    SharedPreferences.Editor speicher_editor;

    public MysqliteHelper helper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final EditText nickname = (EditText) getActivity().findViewById(R.id.nickname_friend_add);
        Button btn_logout= (Button) getActivity().findViewById(R.id.button_logout);
        Button btn_addfriends= (Button) getActivity().findViewById(R.id.button_friend_add);

        speicher_fragment = getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_editor = speicher_fragment.edit();

        String user = speicher_fragment.getString("username", "");

        Constant.setUserName(user);

        helper = DBManager.getInstance(this.getContext());

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent test2 =new Intent(getActivity(),MainActivity.class);
                startActivity(test2);
                getActivity().finish();
            }
        });

        btn_addfriends.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                volley_add(v,nickname);
                nickname.setText("");
            }
        });

    }

    public int have_date(){
        Cursor cursor ;
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor=db.query(Constant.getUserName()+"_friendlist",null,null,null,null,null,null);

        if(cursor.getCount()>0)
            return cursor.getCount();
        else
            return 0;
    }

    public void volley_add(View v,EditText nickname)
    {
        final SQLiteDatabase db = helper.getWritableDatabase();
        speicher_fragment= getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_editor=speicher_fragment.edit();

        String user = speicher_fragment.getString("username", "");
        String pass = speicher_fragment.getString("password", "");
        //Toast.makeText(getActivity(), "successfully", Toast.LENGTH_LONG).show();

        final String friend=nickname.getText().toString();
        String url="http://palaver.se.paluno.uni-due.de/api/friends/add";

        HashMap<String,String> map=new HashMap<>();
        map.put("Username",user);
        map.put("Password",pass);
        map.put("Friend",friend);

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

                            if(number.equals("1")) {
                                ContentValues values = new ContentValues();
                                values.put("_id",have_date());
                                values.put("name",friend);
                                long result = db.insert(Constant.getUserName()+"_friendlist",null,values);
                                if(result>0)
                                {
                                    Toast.makeText(getActivity(),"Update DB Successfully",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getActivity(),"failed"+info,Toast.LENGTH_SHORT).show();
                                }
                            }

                            else
                            {
                                Toast.makeText(getActivity(),"Info: "+info,Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        System.out.println("Output from Error: "+ error.toString());
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();

                    }
                });
        jsonArrayReq.setTag("Add_Request");
        VolleyClass.getHttpQueues().add(jsonArrayReq);

    }


    @Override
    public void onStop() {
        super.onStop();
        VolleyClass.getHttpQueues().cancelAll("Add_Request");
    }
}
