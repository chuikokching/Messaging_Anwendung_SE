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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import de.uni_due.paluno.se.palaver.R;
import de.uni_due.paluno.se.palaver.Volley_Connect;
import de.uni_due.paluno.se.palaver.adapter.ChatAdapter;
import de.uni_due.paluno.se.palaver.room.Chat;
import de.uni_due.paluno.se.palaver.room.MimeTypeEnum;
import de.uni_due.paluno.se.palaver.room.PalaverDatabase;
import de.uni_due.paluno.se.palaver.room.SendTypeEnum;

public class Fragment_chat extends Fragment {
    private TextView friendNameTextView;
    private String friendName;
    private ChatAdapter chatAdapter;
    private List<Chat> chatListsInDB;
    private RecyclerView chatRecyclerView;
    private EditText chatInputText;
    private Button chatSendButton;
    private SharedPreferences loginUser_SP;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendName = getArguments().getString("name_of_friend");
    }

    @Override
    public void onResume() {
        super.onResume();
        getChatListFromServer();
        addChat();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View chatView = inflater.inflate(R.layout.fragment_chat, container, false);
        chatRecyclerView = chatView.findViewById(R.id.chats_recyclerview);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loginUser_SP = getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);

        friendNameTextView = chatView.findViewById(R.id.friendName);
        friendNameTextView.setText(friendName);

        chatInputText = chatView.findViewById(R.id.chat_input_text);
        chatSendButton = chatView.findViewById(R.id.chat_Send_Button);

        getChatListFromServer();

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String data = chatInputText.getText().toString();
                if(data.equals("") == true) {
                    Toast.makeText(getContext(), "Error: The input is empty", Toast.LENGTH_LONG).show();
                } else {
                    final String username = loginUser_SP.getString("username", "");
                    final String password = loginUser_SP.getString("password", "");
                    String sendUrl="http://palaver.se.paluno.uni-due.de/api/message/send";
                    HashMap<String,String> sendMap = new HashMap<>();
                    sendMap.put("Username", username);
                    sendMap.put("Password", password);
                    sendMap.put("Recipient", friendName);
                    sendMap.put("Mimetype", MimeTypeEnum.TEXT_PLAIN.getMimeType());
                    sendMap.put("Data", data);
                    JSONObject sendJSONObject = new JSONObject(sendMap);
                    JsonObjectRequest sendJSONArrayRequest = new JsonObjectRequest(Request.Method.POST, sendUrl, sendJSONObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("MsgType") == 1) {
                                    String chatType = SendTypeEnum.TYPE_SEND.getSendType();
                                    String dateTime = response.getJSONObject("Data").getString("DateTime");
                                    Chat newChat = new Chat(friendName, MimeTypeEnum.TEXT_PLAIN.getMimeType(), data, dateTime, chatType);
                                    PalaverDatabase.getInstance(getContext()).getChatDao().addChat(newChat);
                                    chatInputText.setText("");
                                    addChat();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    Volley_Connect.getVolleyQueues().add(sendJSONArrayRequest);
                }
            }
        });

        // set broadcast to update the list of chat
        IntentFilter filter = new IntentFilter("de.uni_due.paluno.se.palaver.broadcast_NEW_MESSAGE");
        BroadcastReceiver chatBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("de.uni_due.paluno.se.palaver.broadcast_NEW_MESSAGE")) {
                    updateChatDB();
                }
            }
        };
        getActivity().registerReceiver(chatBroadcastReceiver,filter);

        return chatView;
    }

    /**
     * Add the list of chats to adapter
     */
    public void addChat(){
        chatListsInDB = PalaverDatabase.getInstance(getContext()).getChatDao().getChatListByName(friendName);
        if(chatAdapter == null) {
            chatAdapter = new ChatAdapter(getContext(), chatListsInDB);
            chatRecyclerView.setAdapter(chatAdapter);
        } else {
            chatAdapter.setAdapter_chat_list(chatListsInDB);
            chatAdapter.notifyDataSetChanged();
        }

        chatRecyclerView.scrollToPosition(chatListsInDB.size() - 1);
    }

    /**
     * get all chat lists from the server
     */
    public void getChatListFromServer() {
        final String username = loginUser_SP.getString("username", "");
        final String password = loginUser_SP.getString("password", "");
        String getUrl="http://palaver.se.paluno.uni-due.de/api/message/get";
        HashMap<String,String> getMap = new HashMap<>();
        getMap.put("Username", username);
        getMap.put("Password", password);
        getMap.put("Recipient", friendName);
        JSONObject getJSONObject = new JSONObject(getMap);
        JsonObjectRequest getJSONArrayRequest = new JsonObjectRequest(Request.Method.POST, getUrl, getJSONObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("MsgType") == 1) {
                        JSONArray messages = response.getJSONArray("Data");
                        if(PalaverDatabase.getInstance(getContext()).getChatDao().getChatListByName(friendName).size() != 0) {
                            PalaverDatabase.getInstance(getContext()).getChatDao().deleteChatsAboutOneFriend(friendName);
                        } else {
                            for (int i = 0 ; i < messages.length() ; i++) {
                                JSONObject message = (JSONObject) messages.get(i);
                                String sender = message.getString("Sender");
                                String recipient = message.getString("Recipient");
                                String mimeType = message.getString("Mimetype");
                                String data = message.getString("Data");
                                String dateTime = message.getString("DateTime");
                                if(sender.equals(username)) {
                                    Chat newChat = new Chat(recipient, mimeType, data, dateTime, SendTypeEnum.TYPE_SEND.getSendType());
                                    PalaverDatabase.getInstance(getContext()).getChatDao().addChat(newChat);
                                } else {
                                    Chat newChat = new Chat(sender, mimeType, data, dateTime, SendTypeEnum.TYPE_RECEIVE.getSendType());
                                    PalaverDatabase.getInstance(getContext()).getChatDao().addChat(newChat);
                                }
                                //Toast.makeText(getActivity(),"Update Chat Successfully",Toast.LENGTH_SHORT).show();
                                addChat();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley_Connect.getVolleyQueues().add(getJSONArrayRequest);
    }

    public void updateChatDB() {
        final String username = loginUser_SP.getString("username", "");
        final String password = loginUser_SP.getString("password", "");

        List<Chat> chatListOfSender = PalaverDatabase.getInstance(getContext()).getChatDao().getChatListByName(friendName);

        String getMessageUrl = "";
        HashMap<String,String> getMessageMap = new HashMap<>();

        getMessageUrl = "http://palaver.se.paluno.uni-due.de/api/message/getoffset";
        String offset = chatListOfSender.get(chatListOfSender.size() - 1).getDateTime();
        getMessageMap.put("Username", username);
        getMessageMap.put("Password", password);
        getMessageMap.put("Recipient", friendName);
        getMessageMap.put("Offset", offset);

        JSONObject getMessageJSON = new JSONObject(getMessageMap);
        JsonObjectRequest getMessageJSONRequest = new JsonObjectRequest(Request.Method.POST, getMessageUrl, getMessageJSON, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("MsgType") == 1) {
                        JSONArray messages = response.getJSONArray("Data");

                        List<Chat> chatLists = PalaverDatabase.getInstance(getContext()).getChatDao().getChatListByName(friendName);
                        Chat lastChat = chatLists.get(chatLists.size() - 1);
                        JSONObject firstMessageFromServer = (JSONObject)messages.get(0);
                        int i = 0;
                        String dateTimeOfLastChat = lastChat.getDateTime().substring(0, lastChat.getDateTime().indexOf("."));
                        String dateTimeOfMessage = firstMessageFromServer.getString("DateTime")
                                .substring(0, firstMessageFromServer.getString("DateTime").indexOf("."));
                        if(lastChat.getData().equals(firstMessageFromServer.getString("Data")) &&
                                dateTimeOfLastChat.equals(dateTimeOfMessage)) {
                            i = 1;
                        }

                        for ( ; i < messages.length() ; i++) {
                            JSONObject message = (JSONObject) messages.get(i);
                            String sender = message.getString("Sender");
                            String recipient = message.getString("Recipient");
                            String mimeType = message.getString("Mimetype");
                            String data = message.getString("Data");
                            String dateTime = message.getString("DateTime");

                            // add to DB
                            if(sender.equals(username)) {
                                Chat newChat = new Chat(recipient, mimeType, data, dateTime, SendTypeEnum.TYPE_SEND.getSendType());
                                PalaverDatabase.getInstance(getContext()).getChatDao().addChat(newChat);
                            } else {
                                Chat newChat = new Chat(sender, mimeType, data, dateTime, SendTypeEnum.TYPE_RECEIVE.getSendType());
                                PalaverDatabase.getInstance(getContext()).getChatDao().addChat(newChat);
                            }
                            addChat();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley_Connect.getVolleyQueues().add(getMessageJSONRequest);
    }
}
