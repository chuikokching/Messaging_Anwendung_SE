package de.uni_due.paluno.se.palaver.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import de.uni_due.paluno.se.palaver.Adapter.Friendlist_adapter;
import de.uni_due.paluno.se.palaver.Datenbank.SQliteManager;
import de.uni_due.paluno.se.palaver.Datenbank.SQlite_Operation_Manager;
import de.uni_due.paluno.se.palaver.Datenbank.SQlite_Version_Manager;
import de.uni_due.paluno.se.palaver.Volley_Connect;
import de.uni_due.paluno.se.palaver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class fragment_friendlist extends Fragment {

    private Friendlist_adapter list;
    private List<String> friend_list;

    public SQliteManager helper;

    public RecyclerView friendlist_RecyclerView;

    SharedPreferences speicher_fragment;

    SharedPreferences.Editor speicher_editor;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        friendlist_RecyclerView = view.findViewById(R.id.collect_recyclerView);
        friendlist_RecyclerView.setHasFixedSize(true);
        friendlist_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        speicher_fragment = getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_editor = speicher_fragment.edit();

        friend_list = new ArrayList<>();
        String user = speicher_fragment.getString("username", "");

        SQlite_Version_Manager.setTable_name(user);

         helper= SQlite_Operation_Manager.newInstance(this.getContext());

        if(exist_database())
        {
            if(exist_data())
            {
                get_Friendslist_from_DataBase();
            }
            else
            {
                get_Friendslist_from_volley();
            }
        }
        if(!exist_database())
        {
            create_DataBase();
            get_Friendslist_from_volley();
        }
        return view;
    }

    public void create_DataBase(){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql= "create table "+SQlite_Version_Manager.getTable_name()+"_friendlist(_id Integer primary key,name varchar(40))";
        db.execSQL(sql);
        System.out.println(" db has been created!!!! ");
    }

   /* public void createMessage_DB(){
        SQLiteDatabase db = helper.getWritableDatabase();
        for(String name : friend_list)
        {
            String sql= "create table "+Constant.getUserName()+"_"+name+"(_id Integer primary key autoincrement,Sender varchar(20),Recipient varchar(20),Mimetype varchar(20),Data text)";
            db.execSQL(sql);
            requestMessage_DB(name);
        }
    }*/

    public boolean exist_data(){
        Cursor cursor ;
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor=db.query(SQlite_Version_Manager.getTable_name()+"_friendlist",null,null,null,null,null,null);

        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public int exist_new_friends(){
        Cursor cursor ;
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor=db.query(SQlite_Version_Manager.getTable_name()+"_friendlist",null,null,null,null,null,null);

        if(cursor.getCount()>0)
            return cursor.getCount();
        else
            return 0;
    }

    public boolean exist_database(){
        boolean test = false ;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;
        String sql = "select name from sqlite_master where type='table'";
        cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext())
        {
            String name = cursor.getString(0);
            if(name.equals(SQlite_Version_Manager.getTable_name()+"_friendlist"))
            {
                test = true;
                break;
            }
        }
        return test;
    }

    public void addUser(){

        list= new Friendlist_adapter(getContext(),friend_list);
        friendlist_RecyclerView.setAdapter(list);
    }

    public void get_Friendslist_from_volley()
    {
        final SQLiteDatabase db = helper.getWritableDatabase();
        String user = speicher_fragment.getString("username", "");
        String pass = speicher_fragment.getString("password", "");

        String url="http://palaver.se.paluno.uni-due.de/api/friends/get";

        HashMap<String,String> map=new HashMap<>();
        map.put("Username",user);
        map.put("Password",pass);

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
                            JSONArray data = response.getJSONArray("Data");
                            if(number.equals("1")) {
                                if(data.length()!=0)
                                {
                                    ContentValues values = new ContentValues();
                                    for (int i=0; i<data.length(); i++){
                                        values.put("_id",i);
                                        values.put("name",data.getString(i));
                                        long result = db.insert(SQlite_Version_Manager.getTable_name()+"_friendlist",null,values);
                                        if(result>0)
                                        {
                                            Toast.makeText(getActivity(),"Successfully",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(getActivity(),"failed"+info,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    get_Friendslist_from_DataBase();
                                    //createMessage_DB();
                                    // createMessage_DB();
                                    //Toast.makeText(getActivity(),"Info: "+info,Toast.LENGTH_SHORT).show();
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
        jsonArrayReq.setTag("getfriendlist_Request");
        Volley_Connect.getVolleyQueues().add(jsonArrayReq);
    }


    public void get_Friendslist_from_DataBase()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        //query all the data of the entire table to cursor
        Cursor cursor = db.query(SQlite_Version_Manager.getTable_name()+"_friendlist",null,null,null,null,null,null);
        while(cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            friend_list.add(name);
        }
        addUser();
    }


    /*public void requestMessage_DB(final String recipient)
    {
        String user = speicher_fragment.getString("username", "");
        String pass = speicher_fragment.getString("password", "");
        final SQLiteDatabase db = helper.getWritableDatabase();

        String url="http://palaver.se.paluno.uni-due.de/api/message/get";

        HashMap<String,String> map=new HashMap<>();
        map.put("Username",user);
        map.put("Password",pass);

        map.put("Recipient",recipient);
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
                            JSONArray data = response.getJSONArray("Data");
                            ContentValues values = new ContentValues();
                            if(number.equals("1")) {
                                JSONObject temp = null;
                                for(int i=0;i<data.length();i++)
                                {
                                    if(data.length() == 0)
                                    {
                                        break;
                                    }
                                    else
                                    {
                                        temp = data.getJSONObject(i);
                                        values.put("Sender",temp.get("Sender").toString());
                                        values.put("Recipient",temp.get("Recipient").toString());
                                        values.put("Mimetype",temp.get("Mimetype").toString());
                                        values.put("Data",temp.get("Data").toString());

                                        //Log.i("tag",data.length() +" "+temp.get("Sender").toString()+" "+temp.get("Recipient")+" "+ temp.get("Data").toString());
                                        long result = db.insert(Constant.getUserName()+"_"+recipient,null,values);
                                        if(result>0)
                                        {
                                            Toast.makeText(getActivity(),"Successfully",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(getActivity(),"failed"+info,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                Toast.makeText(getActivity(),"Info: "+info,Toast.LENGTH_SHORT).show();
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
        jsonArrayReq.setTag("getMessagelist_Request");
        Volley_Connect.getHttpQueues().add(jsonArrayReq);

    }*/


    @Override
    public void onStop() {
        super.onStop();
        Volley_Connect.getVolleyQueues().cancelAll("getfriendlist_Request");
        //Volley_Connect.getVolleyQueues().cancelAll("getMessagelist_Request");
    }
    /**
     *  Refresh fragment, after friend's addition.
     * @param hidden not displayed onPause(), else displayed onResume();
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Log.i("tag","-----OnHidden-------");

            if(!(friend_list.size()==exist_new_friends()))
            {
                SQLiteDatabase db = helper.getWritableDatabase();
                String sql = "select * from "+SQlite_Version_Manager.getTable_name()+"_friendlist";
                Cursor cursor = db.rawQuery(sql, null);// //query all the data of the entire table to cursor

                while(cursor.moveToNext())
                {
                    if(cursor.getInt(cursor.getColumnIndex("_id"))==(exist_new_friends()-1))
                    {
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        friend_list.add(name);
                    }
                }
                addUser();
            }
       }
    }

}
