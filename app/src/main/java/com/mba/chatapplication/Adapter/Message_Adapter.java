package com.mba.chatapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mba.chatapplication.R;

import java.util.List;

import Model.Chat;

public class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.ViewHolder> {
    private Context mContext;
    private List<Chat> mChat;
    private String Image_url;
    private FirebaseUser fuser;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;


    public Message_Adapter(Context mContext,List<Chat> mChat,String imageURL)
    {
        this.mChat=mChat;
        this.mContext =mContext;
        this.Image_url=imageURL;
    }
    @NonNull
    @Override
    public Message_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType ==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new Message_Adapter.ViewHolder(view);

        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new Message_Adapter.ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull final Message_Adapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView show_message;
        public ImageView profile_image;
        public ViewHolder(View itemView)
        {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profileImage12);

        }

    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid()))
        {
            return  MSG_TYPE_RIGHT;
        }
        else
            return MSG_TYPE_LEFT;
    }
}
