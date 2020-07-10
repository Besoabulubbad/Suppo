package com.mba.chatapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mba.chatapplication.Adapter.Message_Adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Model.Chat;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
CircleImageView profile_pic;
TextView username;
FirebaseUser fuser;
DatabaseReference reference;
Intent intent;
ImageButton btn_send;
EditText text_sent;
Message_Adapter messageAdapter;
List<Chat> mChat;
ValueEventListener valueEventListener;
    androidx.recyclerview.widget.RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        androidx.appcompat.widget.Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,ChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        recyclerView= findViewById(R.id.recycler_view123);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        profile_pic = findViewById(R.id.profileImage);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_sent = findViewById(R.id.text_send);
        intent = getIntent();
        final String UserID=intent.getStringExtra("UserID");
       final String phoneNumber=intent.getStringExtra("PhoneNumber");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(phoneNumber);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                user.setUserID(dataSnapshot.child("UserID").getValue().toString());
                username.setText(user.getfName()+" "+user.getlName());
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference imagesRef = storageRef.child("ProfileImages/"+user.getUserID()+".jpeg");
                try {
                    final File localFile = File.createTempFile("images", "jpg");
                    imagesRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profile_pic.setImageBitmap(bitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                readMessage(fuser.getPhoneNumber(),user.getPhoneNumber(),imagesRef.getDownloadUrl().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg= text_sent.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(fuser.getPhoneNumber(),phoneNumber,msg);
                    text_sent.setText("");
                }
                else {
                    Toast.makeText(MessageActivity.this,"You can't send empty message.", Toast.LENGTH_SHORT);
                }
                }

        });
        seenMessage(phoneNumber);

    }
    private  void seenMessage(final String phoneNumber)
    {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        valueEventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if(!chat.getReceiver().equals(fuser.getPhoneNumber())&& chat.getSender().equals(phoneNumber))
                    {
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("isseen",true);
                            snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendMessage(String sender,String receiver,String message)
    {
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        reference.child("Chats").push().setValue(hashMap);


    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void status(String status)
    {
        reference = FirebaseDatabase.getInstance().getReference("users").child(Objects.requireNonNull(fuser.getPhoneNumber()));
        HashMap<String,Object> hashMap = new HashMap<>();
        reference.updateChildren(hashMap);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    private void readMessage(final String PhoneNumber, final String UserPhoneNumber, final String ImageURL){
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(PhoneNumber)&&chat.getSender().equals(UserPhoneNumber) || chat.getReceiver().equals(UserPhoneNumber)&&chat.getSender().equals(PhoneNumber))
                    {
                        mChat.add(chat);
                    }
                    messageAdapter = new Message_Adapter(MessageActivity.this,mChat, ImageURL);
                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
