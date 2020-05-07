package com.mba.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FireBaseRegister extends AppCompatActivity implements View.OnClickListener {
    EditText etPhone, etOtp;
    Button btSendOtp, btResendOtp, btVerifyOtp ;
    CountryCodePicker ccp1 ;
    private FirebaseAuth mAuth;
    String mVerificationId;
    String nnumberl;
    ProgressDialog progressDialog;
    LinearLayout l1;
    FirebaseUser firebaseUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("users");
    SmsVerifyCatcher smsVerifyCatcher;
boolean verfication=false;




    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
@Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
    initFields();
    ccp1.registerCarrierNumberEditText(etPhone);
    ccp1.setNumberAutoFormattingEnabled(false);
    //Add it in the onCreate method, after calling method initFields()
        mAuth = FirebaseAuth.getInstance();
firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    initFireBaseCallbacks();
    smsVerifyCatcher();



}
void smsVerifyCatcher() {
    smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
        @Override
        public void onSmsCatch(String message) {
            String code = extractDigits(message);//Parse verification code
            etOtp.setText(code);//set code in edit text
            //then you can send verification code to server
        }
    });

}
    public static String extractDigits(final String in) {
        final Pattern p = Pattern.compile( "(\\d{6})" );
        final Matcher m = p.matcher( in );
        if ( m.find() ) {
            return m.group( 0 );
        }
        return "";
    }
    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    void initFireBaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                verfication=true;
                Toast.makeText(FireBaseRegister.this, "Verification Complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                verfication=false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    etPhone.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(FireBaseRegister.this,
                            "Trying too many times",
                            Toast.LENGTH_SHORT).show();
                }
            }
            


            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                verfication=true;
                Toast.makeText(FireBaseRegister.this, "Code Sent", Toast.LENGTH_SHORT).show();


                mVerificationId = verificationId;
                //Add this line to save //verification Id
            }
        };
    }


    void initFields() {
        etPhone = findViewById(R.id.et_phone);
        etOtp = findViewById(R.id.et_otp);
        btSendOtp = findViewById(R.id.bt_send_otp);
        btResendOtp = findViewById(R.id.bt_resend_otp);
        l1 = findViewById(R.id.l1);
        btVerifyOtp = findViewById(R.id.bt_verify_otp);
        ccp1= findViewById(R.id.ccp);
        btResendOtp.setOnClickListener(this);
        btVerifyOtp.setOnClickListener(this);
        btSendOtp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send_otp:
                nnumberl = ccp1.getFullNumberWithPlus();
                if (etPhone.getText().toString().length() == 0) {
                    Toast.makeText(FireBaseRegister.this, "Please enter your phone number first", Toast.LENGTH_SHORT).show();
                    break;
                }
                myRef.child(nnumberl).child("fName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(FireBaseRegister.this, "User Already Exist", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(FireBaseRegister.this, Login.class));
                            // use "username" already exists
                            // Let the user know he needs to pick another username.
                        } else {
                            // User does not exist. NOW call createUserWithEmailAndPassword;
                            // Your previous code here.
                            progressDialog = new ProgressDialog(FireBaseRegister.this);
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


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                break;

            case R.id.bt_resend_otp:
                if (etPhone.getText().toString().length() == 0) {
                    Toast.makeText(FireBaseRegister.this, "Please enter your phone number first", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            etPhone.getText().toString(),        // Phone number to verify
                            1,                 // Timeout duration
                            TimeUnit.MINUTES,   // Unit of timeout
                            this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                    break;
                }
            case R.id.bt_verify_otp:
                if (etPhone.getText().toString().length() == 0) {
                    Toast.makeText(FireBaseRegister.this, "Please enter your phone number first", Toast.LENGTH_SHORT).show();
                    break;
                } else if (etOtp.getText().toString().length() == 0) {
                    Toast.makeText(FireBaseRegister.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                    break;

                } else if (!verfication) {
                    Toast.makeText(FireBaseRegister.this, "Phone number not verified ", Toast.LENGTH_SHORT).show();
                    break;

                } else {
                    progressDialog = new ProgressDialog(FireBaseRegister.this);
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
                                            Toast.makeText(FireBaseRegister.this, "Verification Success", Toast.LENGTH_SHORT).show();
                                            // Display Progress Dialog
                                            Intent intent = new Intent(FireBaseRegister.this, UploadData.class);
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
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
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
