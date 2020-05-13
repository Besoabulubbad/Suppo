package com.mba.chatapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
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
import com.mba.chatapplication.Fragments.ChatsFragment;
import com.mba.chatapplication.Fragments.UsersFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    CircleImageView profileImage;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    String ref;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.chat_layout);
        androidx.appcompat.widget.Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
            initFields();

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getPhoneNumber());
            getDownloadableLink();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user =dataSnapshot.getValue(User.class);
                    String a= dataSnapshot.child("UserID").getValue().toString();
                    user.setUserID(a);
                    username.setText(user.getfName() + " "+user.getlName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
viewPagerAdapter.addFragment(new ChatsFragment(),"Chats");
viewPagerAdapter.addFragment(new UsersFragment(),"Users");
viewPager.setAdapter(viewPagerAdapter);
tabLayout.setupWithViewPager(viewPager);

    }



    void getDownloadableLink()
    {
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("ProfileImages/"+firebaseUser.getUid()+".jpeg");
        try {
            final File localFile = File.createTempFile("images", "jpg");
            imagesRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    profileImage.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {}

    }
    void initFields() {
        profileImage = findViewById(R.id.profileImage);
        username = findViewById(R.id.username);
         tabLayout = findViewById(R.id.tab_layout);
         viewPager = findViewById(R.id.viewPager);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this,MainActivity.class));
                finish();
                return true;
        }
        return false;
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
       public ViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
       public  void addFragment(Fragment fragment, String title)
       {
           fragments.add(fragment);
           titles.add(title);
       }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}
