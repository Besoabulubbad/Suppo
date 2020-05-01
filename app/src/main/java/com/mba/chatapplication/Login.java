package com.mba.chatapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Login  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

}
