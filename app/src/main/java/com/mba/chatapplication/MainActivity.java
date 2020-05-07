package com.mba.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    RelativeLayout rellay;
    View txt;
FirebaseUser firebaseUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("users");
    ;
    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            txt=findViewById(R.id.logo);
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams)txt.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL,0);
            txt.setLayoutParams(layoutParams);
            rellay.setVisibility(View.VISIBLE);


        }
    };

  @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null)
        {
            reference.child(firebaseUser.getPhoneNumber()).child("fName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        startActivity(new Intent(MainActivity.this,ChatActivity.class) );
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });



            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splashscreen);

        txt= findViewById(R.id.logo);
        rellay = findViewById(R.id.rellay);
        handler.postDelayed(runnable, 2000);//
        configNextButton();


    }
    private void configNextButton()
    {

        Button btn= findViewById(R.id.button);
       Button btn2= findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Login.class));

            }

        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,FireBaseRegister.class));

            }
        });
    }


}
