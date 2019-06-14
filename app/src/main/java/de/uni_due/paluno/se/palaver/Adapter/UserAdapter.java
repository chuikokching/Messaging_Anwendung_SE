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
import de.uni_due.paluno.se.palaver.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mcontext;
    private List<String> ulist;

    public UserAdapter(Context m,List<String> muser)
    {
        this.mcontext=m;
        this.ulist=muser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.user_item,viewGroup,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String t= ulist.get(i);
        viewHolder.username.setText(t);
        viewHolder.pro_image.setImageResource(R.drawable.star);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, ChatActivity.class);
                intent.putExtra("username",t);
                mcontext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ulist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView pro_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            username = itemView.findViewById(R.id.name_of_user);
            pro_image = itemView.findViewById(R.id.profile_image);
        }


    }
}
