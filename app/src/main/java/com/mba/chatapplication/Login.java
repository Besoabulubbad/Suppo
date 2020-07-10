package com.mba.chatapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Login  extends AppCompatActivity implements View.OnClickListener , com.mba.chatapplication.AsyncResponce2 ,AsyncResponce3{
    EditText etPhone, etOtp;
    Button btSendOtp, login;
    CountryCodePicker ccp1;
    private FirebaseAuth mAuth;
    String nnumberl;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("users");
    String mVerificationId;
    ProgressDialog progressDialog1;
    FirebaseUser firebaseUser;
    boolean verfication = false;
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    void initFireBaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                verfication = true;
                Toast.makeText(Login.this, "Verification Complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                verfication = false;
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


                verfication = true;
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
        ccp1 = findViewById(R.id.ccp1);
        btSendOtp.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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

                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        authphone();


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

                    progressDialog1= ProgressDialog.show(this,"Wait till verification end","Face verification"); // Display Progress Dialog
                    progressDialog1.setCancelable(false);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, etOtp.getText().toString());
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Verification Success", Toast.LENGTH_SHORT).show();
                                        try {
                                            GetImageFromGallery();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                    } else {
                                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            etOtp.setError("Invalid OTP code.");
                                            progressDialog1.dismiss();

                                        }
                                    }
                                }
                            });


                    break;

                }
        }
    }

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 2;
    Uri photoURI;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void GetImageFromGallery() throws IOException {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.mba.chatapplication.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            }
        }
    }

    private void doCrop(Uri uri) {
        CropImage.activity(uri).setAspectRatio(300, 400)
                .start(this);
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = Math.max(onlyBoundsOptions.outHeight, onlyBoundsOptions.outWidth);

        double ratio = (originalSize > 300) ? (originalSize / 300) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }


    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    Bitmap photo;
    Uri resultUri;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
            try {
                photo = getThumbnail(resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert photo != null;
            handleUpload(photo);

        }
        if (requestCode == 2) {
            doCrop(photoURI);
        }
    }

    private void handleUpload(Bitmap bitmap) {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final String uid = FirebaseAuth.getInstance().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("ProfileImages")
                .child(uid + "1" + ".jpeg");
        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final StorageReference reference1 = FirebaseStorage.getInstance().getReference()
                                .child("ProfileImages")
                                .child(uid + ".jpeg");
                        getDownloadUrl(reference, reference1);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public Uri url, url1;

    private void getDownloadUrl(StorageReference reference, StorageReference reference1) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri;
            }
        });
        reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url1 = uri;
                if (url != null & url1 != null) {
                    Compare compare = new Compare();
                    compare.delegate= Login.this;
                    compare.execute(url);
                    Compare compare1 = new Compare();
                    compare1.delegate= Login.this;
                    compare1.execute(url1);
                }
            }
        });

    }
ArrayList<String> faceIds = new ArrayList<>();

    @Override
    public void processFinish(String output) throws JSONException {
        faceIds.add(output);
        if(faceIds.size()==2) {
            Identical identical = new Identical();
            identical.delegate=Login.this;
            identical.execute(faceIds);
        }
    }

    @Override
    public void processFinish(Boolean output) {
        progressDialog1.dismiss();
        if(output)
        {
            Toast.makeText(Login.this, "Face Verification complete", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,ChatActivity.class));
        }
        else

            {
                Toast.makeText(Login.this, "Face Verification failed please try again", Toast.LENGTH_LONG).show();

            }
    }

    public static class Compare extends AsyncTask<Uri, String, String> {
        public com.mba.chatapplication.AsyncResponce2 delegate = null;

        @Override
        protected String doInBackground(Uri... strings) {
            Uri pictureurl;
            pictureurl = strings[0];
            String fpuid = null;
            String responseJson = null;
            String uri = "https://eastus.api.cognitive.microsoft.com/face/v1.0/detect";
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{ \"url\": \" " + pictureurl + " \"}");
            Request request = new Request.Builder()
                    .url(Objects.requireNonNull(HttpUrl.get(uri)))
                    .method("POST", body)
                    .addHeader("Ocp-Apim-Subscription-Key", "a7765594776345408145562743a14743")
                    .addHeader("Content-Type", "application/json")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    responseJson = Objects.requireNonNull(response.body()).string();
                    JSONArray respons = new JSONArray(responseJson);
                    JSONObject respons2=respons.getJSONObject(0);
                    fpuid = respons2.getString("faceId");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


            return fpuid;

        }
        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            try {
                delegate.processFinish(string);
            } catch (JSONException e) {
                e.printStackTrace();
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
