package com.mba.chatapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mba.chatapplication.Adapter.UserAdapter;
import com.mba.chatapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import Model.Chat;
import Model.User;


public class ChatsFragment extends Fragment {
        private  androidx.recyclerview.widget.RecyclerView recyclerView;
        private UserAdapter userAdapter;
        private List<User> mUsers;
        FirebaseUser fuser;
        DatabaseReference reference;
        List<String> userList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getSender().equals(fuser.getPhoneNumber()))
                    {
                        userList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(fuser.getPhoneNumber()))
                    {
                        userList.add(chat.getSender());
                    }
                    readChats();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
    private void readChats()
    {
        mUsers = new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    for(String phoneNumber : userList)
                    {
                        if(user.getPhoneNumber().equals(phoneNumber))
                        {
                            if(mUsers.size()!=0)
                            {
                                for (ListIterator<User> it = mUsers.listIterator(); it.hasNext(); ) {
                                    User user1 = it.next();

                                    if (!user.getPhoneNumber().equals(user1.getPhoneNumber()) &!mUsers.contains(user))
                                    {

                                        it.add(user);
                                    }
                                }
                            }
                            else {mUsers.add(user);}
                        }
                    }
                }
                userAdapter= new UserAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
