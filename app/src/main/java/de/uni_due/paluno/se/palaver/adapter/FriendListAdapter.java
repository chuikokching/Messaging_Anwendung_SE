package de.uni_due.paluno.se.palaver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.uni_due.paluno.se.palaver.R;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{
    @NonNull

    private Context context;
    private List<String> adapter_friend_list;
    private OnFriendListener mOnFriendListener;

    public FriendListAdapter(Context context, List<String> friendList, OnFriendListener onFriendListener)
    {
        this.context=context;
        this.adapter_friend_list=friendList;
        this.mOnFriendListener = onFriendListener;
    }
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_interface_item,viewGroup,false);
        return new FriendListAdapter.ViewHolder(view, mOnFriendListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder viewHolder, int i) {
        viewHolder.username.setText(adapter_friend_list.get(i));
        viewHolder.profile_image.setImageResource(R.drawable.ic_face_black_24dp);
    }

    @Override
    public int getItemCount() {
        return adapter_friend_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView username;
        public ImageView profile_image;
        OnFriendListener onFriendListener;

        public ViewHolder(@NonNull View itemView, OnFriendListener onFriendListener) {
            super(itemView);

            username = itemView.findViewById(R.id.name_of_user);
            profile_image = itemView.findViewById(R.id.profile_image);
            this.onFriendListener = onFriendListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onFriendListener.onFriendClick(getAdapterPosition());
        }
    }

    public interface OnFriendListener {
        void onFriendClick(int position);
    }
}
