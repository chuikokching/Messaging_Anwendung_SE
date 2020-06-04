package de.uni_due.paluno.se.palaver.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class Fragment_friendlist extends Fragment implements FriendListAdapter.OnFriendListener {

    private FriendListAdapter friendListAdapter;
    private List<Friend> friendListsInAdapter;

    public RecyclerView friendlist_RecyclerView;

    final String GET_FRIEND_LIST_REQUEST_TAG = "getfriendlist_Request";

    SharedPreferences loginUser_SP;

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

        friendListsInAdapter = new ArrayList<>();

        if(exist_data())
        {
            get_Friendslist_from_DataBase();
        }
        else
        {
            get_Friendslist_from_volley();
        }

        // set broadcast to update the list of friend
        IntentFilter filter = new IntentFilter("de.uni_due.paluno.se.palaver.broadcast_NEW_FRIEND");
        BroadcastReceiver newFriendBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("de.uni_due.paluno.se.palaver.broadcast_NEW_FRIEND")) {
                    addUser();
                }
            }
        };
        getActivity().registerReceiver(newFriendBroadcastReceiver,filter);

        return view;
    }

    /**
     * Check if the list of friends already exists
     * @return true The list of friends exists
     * @return false The list of friends doesn't exist
     */
    public boolean exist_data(){
        List<Friend> friendList = PalaverDatabase.getInstance(getContext()).getFriendDao().getFriendList();
        if(friendList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add the list of friends to adapter
     */
    public void addUser(){
        friendListsInAdapter = PalaverDatabase.getInstance(getContext()).getFriendDao().getFriendList();
        if(friendListAdapter == null) {
            friendListAdapter= new FriendListAdapter(getContext(),friendListsInAdapter, this);
            friendlist_RecyclerView.setAdapter(friendListAdapter);
        } else {
            friendListAdapter.setAdapter_friend_list(friendListsInAdapter);
            friendListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * get the list of friends from server
     */
    public void get_Friendslist_from_volley()
    {
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
                                    for (int i=0; i<data.length(); i++){
                                        Friend friend = new Friend(data.getString(i));
                                        PalaverDatabase.getInstance(getContext()).getFriendDao().addFriend(friend);
                                        Toast.makeText(getActivity(),"Update DB Successfully",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        jsonArrayReq.setTag(GET_FRIEND_LIST_REQUEST_TAG);
        Volley_Connect.getVolleyQueues().add(jsonArrayReq);
    }

    /**
     * get the list of friends from Database
     */
    public void get_Friendslist_from_DataBase()
    {
        List<Friend> friendList = PalaverDatabase.getInstance(getContext()).getFriendDao().getFriendList();
        for (Friend friend : friendList) {
            friendListsInAdapter.add(friend);
        }
        addUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        Volley_Connect.getVolleyQueues().cancelAll(GET_FRIEND_LIST_REQUEST_TAG);
    }

    /**
     *  Refresh fragment, after friend's addition.
     * @param hidden not displayed onPause(), else displayed onResume();
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        List<Friend> friendListInDB = PalaverDatabase.getInstance(getContext()).getFriendDao().getFriendList();

        if(!(friendListsInAdapter.size()==friendListInDB.size()))
        {
            for (Friend friend : friendListInDB) {
                if(friendListsInAdapter.contains(friend.getNickname()) == false) {
                    friendListsInAdapter.add(friend);
                }
            }
            addUser();
        }
    }

    @Override
    public void onFriendClick(int position) {
        friendListsInAdapter.get(position);
        String friendName = friendListsInAdapter.get(position).getNickname();
        Intent chatIntent = new Intent(getContext(), Chat_Activity.class);
        chatIntent.putExtra("friendName", friendName);
        startActivity(chatIntent);
    }
}
