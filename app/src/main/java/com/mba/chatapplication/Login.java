package com.mba.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;


public class Login  extends AppCompatActivity implements View.OnClickListener    {
    EditText etPhone, etOtp;
    Button btSendOtp, login ;
    CountryCodePicker ccp1 ;
    private FirebaseAuth mAuth;
    String nnumberl;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("users");
    String mVerificationId;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;
    boolean verfication=false;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        initFields();
        ccp1.registerCarrierNumberEditText(etPhone);
        ccp1.setNumberAutoFormattingEnabled(false);
        //Add it in the onCreate method, after calling method initFields()
        mAuth = FirebaseAuth.getInstance();

        initFireBaseCallbacks();

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }



    void initFireBaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                verfication=true;
                Toast.makeText(Login.this, "Verification Complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                verfication=false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    etPhone.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(Login.this,
                            "Trying too many times",
                            Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                verfication=true;
                Toast.makeText(Login.this, "Code Sent", Toast.LENGTH_SHORT).show();


                mVerificationId = verificationId;
                //Add this line to save //verification Id
            }
        };
    }
    void initFields() {
        etPhone = findViewById(R.id.et_phone1);
        etOtp = findViewById(R.id.et_otp1);
        btSendOtp = findViewById(R.id.bt_send_otp1);
        login = findViewById(R.id.login);
        ccp1= findViewById(R.id.ccp1);
        btSendOtp.setOnClickListener(this);
        login.setOnClickListener(this);
    }
    @Override
    public void onClick(android.view.View view) {
        switch (view.getId()) {
            case R.id.bt_send_otp1:
                nnumberl = ccp1.getFullNumberWithPlus();
                if (etPhone.getText().toString().length() == 0) {
                    Toast.makeText(Login.this, "Please enter your phone number first", Toast.LENGTH_SHORT).show();
                    break;
                }
                myRef.child(nnumberl).child("fName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            progressDialog = new ProgressDialog(Login.this);
                            progressDialog.setMessage("Loading..."); // Setting Message
                            progressDialog.setTitle("ProgressDialog"); // Setting Title
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                            progressDialog.show(); // Display Progress Dialog
                            progressDialog.setCancelable(false);
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        authphone();
                                        progressDialog.dismiss();


                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }
                                }
                            }).start();


                        } else {
                            Toast.makeText(Login.this, "User Is not Registered", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, FireBaseRegister.class));



                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                break;
            case R.id.login:
                if (etPhone.getText().toString().length() == 0) {
                    Toast.makeText(Login.this, "Please enter your phone number first", Toast.LENGTH_SHORT).show();
                    break;
                } else if (etOtp.getText().toString().length() == 0) {
                    Toast.makeText(Login.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                    break;

                } else if (!verfication) {
                    Toast.makeText(Login.this, "Phone number not verified ", Toast.LENGTH_SHORT).show();
                    break;

                } else {
                    progressDialog = new ProgressDialog(Login.this);
                    progressDialog.setMessage("Loading..."); // Setting Message
                    progressDialog.setTitle("ProgressDialog"); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, etOtp.getText().toString());
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Verification Success", Toast.LENGTH_SHORT).show();
                                        // Display Progress Dialog
                                        Intent intent = new Intent(Login.this, ChatActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("PhoneNumber", nnumberl);
                                        intent.putExtras(bundle);
                                        progressDialog.dismiss();
                                        startActivity(intent);

                                    } else {
                                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            progressDialog.dismiss();
                                            etOtp.setError("Invalid OTP code.");

                                        }
                                    }
                                }
                            });


                    break;

                }
        }
    }
    private void authphone()
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(nnumberl,        // Phone number to verify
                1,                 // Timeout duration
                TimeUnit.MINUTES,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

}
