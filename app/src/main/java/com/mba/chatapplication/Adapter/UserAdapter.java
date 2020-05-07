package com.mba.chatapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mba.chatapplication.R;

import java.util.List;

import Model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
private Context mContext;
private List<User> mUsers;

public UserAdapter(Context mContext,List<User> mUser)
{
    this.mUsers=mUser;
    this.mContext =mContext;
}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType)
    {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getfName()+" "+user.getlName());
        holder.profile_image.setImageResource(R.drawable.ic_account);
        }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
{
    public TextView username;
    public ImageView profile_image;
    public ViewHolder(View itemView)
    {
        super(itemView);
        username=itemView.findViewById(R.id.username12);
        profile_image = itemView.findViewById(R.id.profileImage12);
    }

}
}
