package de.uni_due.paluno.se.palaver.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.uni_due.paluno.se.palaver.R;

public class Friendlist_adapter extends RecyclerView.Adapter<Friendlist_adapter.ViewHolder>{
    @NonNull

    private Context context;
    private List<String> adapter_friend_list;

    public Friendlist_adapter(Context context, List<String> friendlist)
    {
        this.context=context;
        this.adapter_friend_list=friendlist;
    }
    @Override
    public Friendlist_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_interface_item,viewGroup,false);
        return new Friendlist_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Friendlist_adapter.ViewHolder viewHolder, int i) {
        viewHolder.username.setText(adapter_friend_list.get(i));
        viewHolder.profile_image.setImageResource(R.drawable.ic_face_black_24dp);

    }

    @Override
    public int getItemCount() {
        return adapter_friend_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.name_of_user);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
