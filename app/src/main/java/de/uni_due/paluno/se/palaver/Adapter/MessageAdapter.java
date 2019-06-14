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
import de.uni_due.paluno.se.palaver.Modell.Chat;
import de.uni_due.paluno.se.palaver.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context mcontext;
    private List<Chat> ulist;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;


    public MessageAdapter(Context m, List<Chat> muser)
    {
        this.mcontext=m;
        this.ulist=muser;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {

            Chat chat = ulist.get(i);
            viewHolder.text.setText(chat.getMessage());
            viewHolder.pro_image.setImageResource(R.drawable.star);


    }

    @Override
    public int getItemCount() {
        return ulist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView text;
        public ImageView pro_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            text = itemView.findViewById(R.id.message_show);
            pro_image = itemView.findViewById(R.id.left_image);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
