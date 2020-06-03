package de.uni_due.paluno.se.palaver.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import de.uni_due.paluno.se.palaver.Chat_Activity;
import de.uni_due.paluno.se.palaver.adapter.FriendListAdapter;
import de.uni_due.paluno.se.palaver.datenbank.SQliteManager;
import de.uni_due.paluno.se.palaver.datenbank.SQlite_Operation_Manager;
import de.uni_due.paluno.se.palaver.datenbank.SQlite_Version_Manager;
import de.uni_due.paluno.se.palaver.Volley_Connect;
import de.uni_due.paluno.se.palaver.R;
import de.uni_due.paluno.se.palaver.room.Friend;
import de.uni_due.paluno.se.palaver.room.PalaverDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class fragment_friendlist extends Fragment implements FriendListAdapter.OnFriendListener {

    private FriendListAdapter list;
    private ArrayList<String> friend_list;

    public SQliteManager helper;

    public RecyclerView friendlist_RecyclerView;

    SharedPreferences loginUser_SP;

    SharedPreferences.Editor loginUser_SP_Editor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        friendlist_RecyclerView = view.findViewById(R.id.collect_recyclerView);
        friendlist_RecyclerView.setHasFixedSize(true);
        friendlist_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendlist_RecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        loginUser_SP = getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);

        friend_list = new ArrayList<>();
        String user = loginUser_SP.getString("username", "");

        SQlite_Version_Manager.setTable_name(user);

        helper= SQlite_Operation_Manager.newInstance(this.getContext());

        /**
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
        }*/
        if(exist_data())
        {
            get_Friendslist_from_DataBase();
        }
        else
        {
            get_Friendslist_from_volley();
        }
        return view;
    }

    /**
    public void create_DataBase(){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql= "create table "+SQlite_Version_Manager.getTable_name()+"_friendlist(_id Integer primary key,name varchar(40))";
        db.execSQL(sql);
        System.out.println(" db has been created!!!! ");
    }*/

    public boolean exist_data(){
        /**
        Cursor cursor ;
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor=db.query(SQlite_Version_Manager.getTable_name()+"_friendlist",null,null,null,null,null,null);

        if(cursor.getCount()>0)
            return true;
        else
            return false;*/
        List<Friend> friendList = PalaverDatabase.getInstance(getContext()).getFriendDao().getFriendList();
        if(friendList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * check how many friends already in DB
     * @return number of friends that user have after addition.
     */
    public int numberOfFriendsInDB(){
        /**
        Cursor cursor ;
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor=db.query(SQlite_Version_Manager.getTable_name()+"_friendlist",null,null,null,null,null,null);

        if(cursor.getCount()>0)
            return cursor.getCount();
        else
            return 0;*/

        List<Friend> friendList = PalaverDatabase.getInstance(getContext()).getFriendDao().getFriendList();
        return (friendList.size() > 0) ? friendList.size() : 0;
    }

    /**
     * check database exists or not.
     * @return
     */
    public boolean exist_database(){
        boolean temp = false ;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;
        String sql = "select name from sqlite_master where type='table'";
        cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext())
        {
            String name = cursor.getString(0);
            if(name.equals(SQlite_Version_Manager.getTable_name()+"_friendlist"))
            {
                temp = true;
                break;
            }
        }
        return temp;
    }

    public void addUser(){
        list= new FriendListAdapter(getContext(),friend_list, this);
        friendlist_RecyclerView.setAdapter(list);
    }

    public void get_Friendslist_from_volley()
    {
        //final SQLiteDatabase db = helper.getWritableDatabase();
        String user = loginUser_SP.getString("username", "");
        String pass = loginUser_SP.getString("password", "");

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
                                        //values.put("_id",i);
                                        //values.put("name",data.getString(i));
                                        Friend friend = new Friend(data.getString(i));
                                        PalaverDatabase.getInstance(getContext()).getFriendDao().addFriend(friend);
                                        //long result = db.insert(SQlite_Version_Manager.getTable_name()+"_friendlist",null,values);
                                        //if(result>0)
                                        //{
                                            Toast.makeText(getActivity(),"Update DB Successfully",Toast.LENGTH_SHORT).show();
                                        //}
                                        //else {
                                          //  Toast.makeText(getActivity(),"failed: "+info,Toast.LENGTH_SHORT).show();
                                        //}
                                    }
                                    get_Friendslist_from_DataBase();
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
        /**
        SQLiteDatabase db = helper.getWritableDatabase();
        //query all the data of the entire table to cursor
        Cursor cursor = db.query(SQlite_Version_Manager.getTable_name()+"_friendlist",null,null,null,null,null,null);
        while(cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            friend_list.add(name);
        }
         */
        List<Friend> friendList = PalaverDatabase.getInstance(getContext()).getFriendDao().getFriendList();
        for (Friend friend: friendList) {
            friend_list.add(friend.getNickName());
        }
        addUser();
    }



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

            if(!(friend_list.size()==numberOfFriendsInDB()))
            {
                SQLiteDatabase db = helper.getWritableDatabase();
                String sql = "select * from "+SQlite_Version_Manager.getTable_name()+"_friendlist";
                Cursor cursor = db.rawQuery(sql, null);// //query all the data of the entire table to cursor

                while(cursor.moveToNext())
                {
                    if(cursor.getInt(cursor.getColumnIndex("_id"))==(numberOfFriendsInDB()-1))
                    {
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        friend_list.add(name);
                    }
                }
                addUser();
            }
       }
    }

    @Override
    public void onFriendClick(int position) {
        friend_list.get(position);
        Intent chatIntent = new Intent(getContext(), Chat_Activity.class);
        startActivity(chatIntent);
    }
}
