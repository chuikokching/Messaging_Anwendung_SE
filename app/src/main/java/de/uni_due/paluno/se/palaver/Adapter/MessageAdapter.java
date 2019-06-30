package de.uni_due.paluno.se.palaver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private OnMapListener mOnMapListener;


    public MessageAdapter()
    {

    }

    public MessageAdapter(List<Message> userMessage_list,OnMapListener onMapListener)
    {
        this.userMessage_list=userMessage_list;
        this.mOnMapListener=onMapListener;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView leftMessageText,rightMessageText;
        public ImageView leftImageView,rightImageView;
        OnMapListener onMapListener;


        public MessageViewHolder(@NonNull View itemView,OnMapListener onMapListener)
        {
            super(itemView);

            this.onMapListener = onMapListener;
            leftMessageText = (TextView)itemView.findViewById(R.id.receiver);
            rightMessageText = (TextView)itemView.findViewById(R.id.sender);
            leftImageView = (ImageView) itemView.findViewById(R.id.mapbox_left);
            rightImageView = (ImageView) itemView.findViewById(R.id.mapbox_right);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onMapListener.OnMapClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_message,viewGroup,false);
        return new MessageViewHolder(view,mOnMapListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {

        Message message = userMessage_list.get(i);
        String sender = message.getSender();
        String receiver =message.getRecipient();
        String type = message.getMimetype();
        String data = message.getData();
       // Log.i("tag","-----------------------On MessageAdapter-------------------------" + sender +" " + type + data);


        if(type.equals("text/plain"))
        {
            messageViewHolder.leftMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.rightImageView.setVisibility(View.INVISIBLE);
            messageViewHolder.leftImageView.setVisibility(View.INVISIBLE);
            if(Constant.getUserName().equals(sender))
            {
               // Log.i("tag","-----------------------On MessageAdapter-------------inside------------");
                messageViewHolder.rightMessageText.setBackgroundResource(R.drawable.chat_right);
                messageViewHolder.rightMessageText.setText(sender + ":"+data);
            }
            else {
                messageViewHolder.rightMessageText.setVisibility(View.INVISIBLE);
                messageViewHolder.leftMessageText.setVisibility(View.VISIBLE);
                //Log.i("tag","-----------------------On MessageAdapter-------------outside------------");
                messageViewHolder.leftMessageText.setBackgroundResource(R.drawable.chat_left);
                messageViewHolder.leftMessageText.setText(sender+":"+data);
            }
        }

        if(type.equals("location"))
        {
            messageViewHolder.leftMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.rightMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.leftImageView.setVisibility(View.INVISIBLE);
            if(Constant.getUserName().equals(sender))
            {
                // Log.i("tag","-----------------------On MessageAdapter-------------inside------------");
                messageViewHolder.rightImageView.setVisibility(View.VISIBLE);

            }
            else {
                messageViewHolder.rightImageView.setVisibility(View.INVISIBLE);
                messageViewHolder.leftImageView.setVisibility(View.VISIBLE);
                //Log.i("tag","-----------------------On MessageAdapter-------------outside------------");
            }
        }


    }

    @Override
    public int getItemCount() {

        return userMessage_list.size();
    }

    public interface OnMapListener{
        void OnMapClick(int position);
    }

}
