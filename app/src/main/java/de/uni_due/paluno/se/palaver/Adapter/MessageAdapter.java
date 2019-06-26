package de.uni_due.paluno.se.palaver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.uni_due.paluno.se.palaver.ChatActivity;
import de.uni_due.paluno.se.palaver.Datenbank.Constant;
import de.uni_due.paluno.se.palaver.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public List<Message> userMessage_list;



    public MessageAdapter(List<Message> userMessage_list)
    {
        this.userMessage_list=userMessage_list;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView leftMessageText,rightMessageText;


        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            leftMessageText = (TextView)itemView.findViewById(R.id.receiver);
            rightMessageText = (TextView)itemView.findViewById(R.id.sender);
        }

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_item,viewGroup,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {

        Message message = userMessage_list.get(i);
        String sender = message.getSender();
        String receiver =message.getRecipient();
        String type = message.getMimetype();
        String data = message.getData();

        if(type.equals("text/plain"))
        {

            if(Constant.getUserName().equals(sender))
            {
                messageViewHolder.leftMessageText.setVisibility(View.INVISIBLE);
                messageViewHolder.rightMessageText.setBackgroundResource(R.drawable.chat_right);
                messageViewHolder.rightMessageText.setText(data);
            }
            else {
                messageViewHolder.rightMessageText.setVisibility(View.INVISIBLE);
                messageViewHolder.leftMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.leftMessageText.setBackgroundResource(R.drawable.chat_left);
                messageViewHolder.leftMessageText.setText(data);
            }
        }

    }

    @Override
    public int getItemCount() {

        return userMessage_list.size();
    }


}
