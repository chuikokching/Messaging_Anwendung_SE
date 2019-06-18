package de.uni_due.paluno.se.palaver.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment_list extends Fragment {

    private UserAdapter user;
    private List<String> friend_list;

    private MysqliteHelper helper;

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

        String user = speicher_fragment.getString("username", "");

        Constant.setUserName(user);


        helper = DBManager.getInstance(this.getContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        db.close();

        friend_list= new ArrayList<>();
        //volley_getFriendslist();
        addUser();

        return view;
    }

    public void addUser(){
        friend_list.add("test123126");
        friend_list.add("chuikokching");
        friend_list.add("jeff");
        friend_list.add("guenes");
        user= new UserAdapter(getContext(),friend_list);
        mCollectRecyclerView.setAdapter(user);

    }

    public void volley_getFriendslist()
    {

        String user = speicher_fragment.getString("username", "");
        String pass = speicher_fragment.getString("password", "");
        String data="";

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
                        //Log.i("Output from server: ", response.toString());
                        //System.out.println("Output from server: "+ response.toString());

                        try {
                            String number= response.getString("MsgType");
                            String info = response.getString("Info");
                            String data = response.getString("Data");

                            if(number.equals("1")) {

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
        jsonArrayReq.setTag("getfriendlist_Request");
        VolleyClass.getHttpQueues().add(jsonArrayReq);

    }

    @Override
    public void onStop() {
        super.onStop();
        VolleyClass.getHttpQueues().cancelAll("getfriendlist_Request");
    }
}
