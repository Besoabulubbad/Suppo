package com.mba.chatapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mba.chatapplication.MessageActivity;
import com.mba.chatapplication.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import Model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
private Context mContext;
private List<User> mUsers;
private  boolean isChat;
public UserAdapter(Context mContext,List<User> mUser,boolean isChat)

{
    this.mUsers=mUser;
    this.mContext =mContext;
    this.isChat=isChat;
}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType)
    {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getfName()+" "+user.getlName());
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("ProfileImages/"+user.getUserID()+".jpeg");
        try {
            final File localFile = File.createTempFile("images", "jpg");
            imagesRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.profile_image.setImageBitmap(bitmap);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(isChat)
        {
            if(user.getStatus()=="online")
            {
                holder.image_on.setVisibility(View.VISIBLE);
                holder.image_off.setVisibility(View.GONE);
            }
            else
                {
                    holder.image_on.setVisibility(View.GONE);
                    holder.image_off.setVisibility(View.VISIBLE);
                }
        }
        else
            {
                holder.image_on.setVisibility(View.GONE);
                holder.image_off.setVisibility(View.GONE);
            }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("UserID",user.getUserID());
                intent.putExtra("PhoneNumber",user.getPhoneNumber());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
{
    public TextView username;
    public ImageView profile_image;
    public ImageView image_on;
    public ImageView image_off;

    public ViewHolder(View itemView)
    {
        super(itemView);
        username=itemView.findViewById(R.id.username12);
        profile_image = itemView.findViewById(R.id.profileImage12);
        image_off = itemView.findViewById(R.id.img_off);
        image_on = itemView.findViewById(R.id.img_on);
    }

}
}
