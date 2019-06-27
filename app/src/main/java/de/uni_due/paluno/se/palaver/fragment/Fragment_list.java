package de.uni_due.paluno.se.palaver.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

import de.uni_due.paluno.se.palaver.Datenbank.Constant;
import de.uni_due.paluno.se.palaver.Datenbank.DBManager;
import de.uni_due.paluno.se.palaver.Datenbank.MysqliteHelper;
import de.uni_due.paluno.se.palaver.VolleyClass;
import de.uni_due.paluno.se.palaver.Adapter.UserAdapter;
import de.uni_due.paluno.se.palaver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment_list extends Fragment {

    private UserAdapter user;
    private List<String> friend_list;

    public MysqliteHelper helper;

    public RecyclerView mCollectRecyclerView;

    SharedPreferences speicher_fragment;

    SharedPreferences.Editor speicher_editor;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mCollectRecyclerView = view.findViewById(R.id.collect_recyclerView);
        mCollectRecyclerView.setHasFixedSize(true);
        mCollectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        speicher_fragment = getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        speicher_editor = speicher_fragment.edit();

        friend_list = new ArrayList<>();
        String user = speicher_fragment.getString("username", "");

        Constant.setUserName(user);

        helper = DBManager.getInstance(this.getContext());

        if(have_db())
        {
            if(have_date())
            {
                getFriendslist_fromDB();
            }
            else
            {
                volley_getFriendslist();
            }

        }
        if(!have_db())
        {
            createDB();

            volley_getFriendslist();
        }


        return view;
    }

    public void createDB(){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql= "create table "+Constant.getUserName()+"_friendlist(_id Integer primary key,name varchar(40))";
        db.execSQL(sql);


    }

    public void createMessage_DB(){
        SQLiteDatabase db = helper.getWritableDatabase();
        for(String name : friend_list)
        {
            String sql= "create table "+Constant.getUserName()+"_"+name+"(_id Integer primary key autoincrement,Sender varchar(20),Recipient varchar(20),Mimetype varchar(20),Data text)";
            db.execSQL(sql);
            requestMessage_DB(name);
        }

    }

    public boolean have_date(){
        Cursor cursor ;
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor=db.query(Constant.getUserName()+"_friendlist",null,null,null,null,null,null);

        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public int have_dateint(){
        Cursor cursor ;
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor=db.query(Constant.getUserName()+"_friendlist",null,null,null,null,null,null);

        if(cursor.getCount()>0)
            return cursor.getCount();
        else
            return 0;
    }

    public boolean have_db(){
        boolean test = false ;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;
        String sql = "select name from sqlite_master where type='table'";
        cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext())
        {
            String name = cursor.getString(0);
            if(name.equals(Constant.getUserName()+"_friendlist"))
            {
                test = true;
                break;
            }
        }
        //Log.i("tag",test + " test in fragment 1!!");
        return test;
    }

    public void addUser(){

        user= new UserAdapter(getContext(),friend_list);
        mCollectRecyclerView.setAdapter(user);
    }

    public void volley_getFriendslist()
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
                                ContentValues values = new ContentValues();
                                for (int i=0; i<data.length(); i++){
                                    values.put("_id",i);
                                    values.put("name",data.getString(i));
                                    long result = db.insert(Constant.getUserName()+"_friendlist",null,values);
                                    if(result>0)
                                    {
                                       // Toast.makeText(getActivity(),"Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                       // Toast.makeText(getActivity(),"failed"+info,Toast.LENGTH_SHORT).show();
                                    }
                                }
                                getFriendslist_fromDB();
                                createMessage_DB();
                               // createMessage_DB();
                                //Toast.makeText(getActivity(),"Info: "+info,Toast.LENGTH_SHORT).show();
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
        VolleyClass.getHttpQueues().add(jsonArrayReq);
    }


    public void getFriendslist_fromDB()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.query(Constant.getUserName()+"_friendlist",null,null,null,null,null,null);

        while(cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            friend_list.add(name);
        }
        addUser();

    }


    public void requestMessage_DB(final String recipient)
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
        VolleyClass.getHttpQueues().add(jsonArrayReq);

    }


    @Override
    public void onStop() {
        super.onStop();
        VolleyClass.getHttpQueues().cancelAll("getfriendlist_Request");
        VolleyClass.getHttpQueues().cancelAll("getMessagelist_Request");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {   // not displayed onPause();
            Log.i("tag","-----OnHidden-------");

        }else{  // displayed onResume();

            if(!(friend_list.size()==have_dateint()))
            {
                SQLiteDatabase db = helper.getWritableDatabase();
                String sql = "select * from "+Constant.getUserName()+"_friendlist";
                Cursor cursor = db.rawQuery(sql, null);

                while(cursor.moveToNext())
                {
                    if(cursor.getInt(cursor.getColumnIndex("_id"))==(have_dateint()-1))
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
