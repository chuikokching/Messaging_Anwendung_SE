package de.uni_due.paluno.se.palaver.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.uni_due.paluno.se.palaver.R;
import de.uni_due.paluno.se.palaver.room.Chat;
import de.uni_due.paluno.se.palaver.room.MimeTypeEnum;
import de.uni_due.paluno.se.palaver.room.SendTypeEnum;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private List<Chat> adapter_chat_list;
    private OnMapListener mOnMapListener;

    public ChatAdapter(Context context, List<Chat> chatList,OnMapListener onMapListener) {
        this.context = context;
        this.adapter_chat_list = chatList;
        this.mOnMapListener=onMapListener;
    }

    public void setAdapter_chat_list(List<Chat> adapter_chat_list) {
        this.adapter_chat_list = adapter_chat_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(view,mOnMapListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = adapter_chat_list.get(position);
        if(chat.getSendType().equals(SendTypeEnum.TYPE_RECEIVE.getSendType())) {
            if(chat.getMimeType().equals(MimeTypeEnum.TEXT_PLAIN.getMimeType())) {
                holder.textLayoutLeft.setVisibility(View.VISIBLE);
                holder.textLayoutRight.setVisibility(View.GONE);
                holder.locationLayoutLeft.setVisibility(View.GONE);
                holder.locationLayoutRight.setVisibility(View.GONE);
                holder.picLayoutLeft.setVisibility(View.GONE);
                holder.picLayoutRight.setVisibility(View.GONE);
                holder.textTextViewLeft.setText(chat.getData());
            } else if(chat.getMimeType().equals(MimeTypeEnum.TEXT_COOR.getMimeType())) {
                holder.textLayoutLeft.setVisibility(View.GONE);
                holder.textLayoutRight.setVisibility(View.GONE);
                holder.locationLayoutLeft.setVisibility(View.VISIBLE);
                holder.locationLayoutRight.setVisibility(View.GONE);
                holder.picLayoutLeft.setVisibility(View.GONE);
                holder.picLayoutRight.setVisibility(View.GONE);
                //holder.locationImageViewLeft.setText(chat.getData());
            } else if(chat.getMimeType().equals(MimeTypeEnum.IMAGE_PIC.getMimeType())) {
                holder.textLayoutLeft.setVisibility(View.GONE);
                holder.textLayoutRight.setVisibility(View.GONE);
                holder.locationLayoutLeft.setVisibility(View.GONE);
                holder.locationLayoutRight.setVisibility(View.GONE);
                holder.picLayoutLeft.setVisibility(View.VISIBLE);
                holder.picLayoutRight.setVisibility(View.GONE);
                byte[] decodedString = Base64.decode(chat.getData(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.picImageViewLeft.setImageBitmap(decodedByte);
                //holder.picImageViewLeft);
            }
        } else if(chat.getSendType().equals(SendTypeEnum.TYPE_SEND.getSendType())) {
            if(chat.getMimeType().equals(MimeTypeEnum.TEXT_PLAIN.getMimeType())) {
                holder.textLayoutLeft.setVisibility(View.GONE);
                holder.textLayoutRight.setVisibility(View.VISIBLE);
                holder.locationLayoutLeft.setVisibility(View.GONE);
                holder.locationLayoutRight.setVisibility(View.GONE);
                holder.picLayoutLeft.setVisibility(View.GONE);
                holder.picLayoutRight.setVisibility(View.GONE);
                holder.textTextViewRight.setText(chat.getData());
            } else if(chat.getMimeType().equals(MimeTypeEnum.TEXT_COOR.getMimeType())) {
                holder.textLayoutLeft.setVisibility(View.GONE);
                holder.textLayoutRight.setVisibility(View.GONE);
                holder.locationLayoutLeft.setVisibility(View.GONE);
                holder.locationLayoutRight.setVisibility(View.VISIBLE);
                holder.picLayoutLeft.setVisibility(View.GONE);
                holder.picLayoutRight.setVisibility(View.GONE);
                //holder.locationImageViewRight.setText(chat.getData());
            } else if(chat.getMimeType().equals(MimeTypeEnum.IMAGE_PIC.getMimeType()) == true) {
                holder.textLayoutLeft.setVisibility(View.GONE);
                holder.textLayoutRight.setVisibility(View.GONE);
                holder.locationLayoutLeft.setVisibility(View.GONE);
                holder.locationLayoutRight.setVisibility(View.GONE);
                holder.picLayoutLeft.setVisibility(View.GONE);
                holder.picLayoutRight.setVisibility(View.VISIBLE);
                byte[] decodedString = Base64.decode(chat.getData(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.picImageViewRight.setImageBitmap(decodedByte);
            }
        }
    }

    public interface OnMapListener{
        void OnMapClick(int position);
    }

    @Override
    public int getItemCount() {
        return adapter_chat_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private LinearLayout textLayoutLeft;
        private LinearLayout textLayoutRight;

        private LinearLayout locationLayoutLeft;
        private LinearLayout locationLayoutRight;

        private LinearLayout picLayoutLeft;
        private LinearLayout picLayoutRight;

        private TextView textTextViewLeft;
        private TextView textTextViewRight;

        private ImageView locationImageViewLeft;
        private ImageView locationImageViewRight;

        private ImageView picImageViewLeft;
        private ImageView picImageViewRight;
        OnMapListener onMapListener;

        public ViewHolder(@NonNull View itemView,OnMapListener onMapListener) {
            super(itemView);
            this.onMapListener = onMapListener;
            textLayoutLeft = itemView.findViewById(R.id.text_layout_left);
            textLayoutRight = itemView.findViewById(R.id.text_layout_right);

            locationLayoutLeft = itemView.findViewById(R.id.location_layout_left);
            locationLayoutRight = itemView.findViewById(R.id.location_layout_right);

            picLayoutLeft = itemView.findViewById(R.id.pic_layout_left);
            picLayoutRight = itemView.findViewById(R.id.pic_layout_right);

            textTextViewLeft = itemView.findViewById(R.id.chat_plainText_left);
            textTextViewRight = itemView.findViewById(R.id.chat_plainText_right);

            locationImageViewLeft = itemView.findViewById(R.id.chat_location_left);
            locationImageViewRight = itemView.findViewById(R.id.chat_location_right);

            picImageViewLeft = itemView.findViewById(R.id.chat_pic_left);
            picImageViewRight = itemView.findViewById(R.id.chat_pic_right);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onMapListener.OnMapClick(getAdapterPosition());
        }
    }
}
