package de.uni_due.paluno.se.palaver.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import de.uni_due.paluno.se.palaver.datenbank.SQliteManager;
import de.uni_due.paluno.se.palaver.datenbank.SQlite_Operation_Manager;
import de.uni_due.paluno.se.palaver.datenbank.SQlite_Version_Manager;
import de.uni_due.paluno.se.palaver.MainActivity;

import de.uni_due.paluno.se.palaver.Volley_Connect;
import de.uni_due.paluno.se.palaver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class fragment_setting extends Fragment {
    SharedPreferences speicher_fragment;
    SharedPreferences.Editor speicher_editor;
    public SQliteManager helper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final EditText nickname = getActivity().findViewById(R.id.nickname_friend_add);
        Button button_signout= getActivity().findViewById(R.id.button_sign_out);
        Button button_addfriends= getActivity().findViewById(R.id.button_friend_add);

        speicher_fragment = getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_editor = speicher_fragment.edit();

        String user = speicher_fragment.getString("username", "");

        SQlite_Version_Manager.setTable_name(user);

        helper = SQlite_Operation_Manager.newInstance(this.getContext());

        button_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speicher_editor.clear().commit();
                Intent test2 = new Intent(fragment_setting.this.getActivity(), MainActivity.class);
                fragment_setting.this.startActivity(test2);
                fragment_setting.this.getActivity().finish();
            }
        });

        button_addfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_setting.this.volley_add_friend(v, nickname);
                nickname.setText("");
            }
        });

    }

    /**
     * @param v View
     * @param nickname Name of friend
     */
    public void volley_add_friend(View v,EditText nickname)
    {
        final SQLiteDatabase db = helper.getWritableDatabase();
        speicher_fragment= getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_editor=speicher_fragment.edit();

        String user = speicher_fragment.getString("username", "");
        String pass = speicher_fragment.getString("password", "");

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
                            String message= response.getString("Info");

                            if(number.equals("1")) {
                               ContentValues values = new ContentValues();
                               values.put("_id",number_of_rows());
                               values.put("name",friend);
                               long result = db.insert(SQlite_Version_Manager.getTable_name()+"_friendlist",null,values);
                               if(result>0)
                               {
                                   Toast.makeText(getActivity(),"Update DB Successfully and "+message ,Toast.LENGTH_SHORT).show();
                               }
                                else {
                                        Toast.makeText(getActivity(),"failed: "+message,Toast.LENGTH_SHORT).show();
                               }
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Info: "+message,Toast.LENGTH_SHORT).show();
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
        Volley_Connect.getVolleyQueues().add(jsonArrayReq);

    }

    /**
     * calculate rows in a table
     * @return number of rows
     */
    public int number_of_rows(){
        Cursor cursor ;
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor=db.query(SQlite_Version_Manager.getTable_name()+"_friendlist",null,null,null,null,null,null);

        if(cursor.getCount()>0)
            return cursor.getCount();
        else
            return 0;
    }

    @Override
    public void onStop() {
        super.onStop();
        Volley_Connect.getVolleyQueues().cancelAll("Add_Request");
    }
}
