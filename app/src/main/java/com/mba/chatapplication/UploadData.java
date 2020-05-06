package com.mba.chatapplication;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class UploadData extends AppCompatActivity  implements View.OnClickListener{
    ImageButton profilePicture;
    EditText fn;
    EditText ln;
    Uri uri;
    ProgressDialog progressDialog;
    com.google.android.material.textfield.TextInputEditText passcode;
    EditText rep;
    Intent CropIntent;
    int image;
    Intent GalIntent;
   String  phoneNumber;
   FirebaseUser firebaseUser;
    Bundle bundle;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("users");

    Button next;
    private static final String TAG = "UploadData";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_data);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        initFields();
         image = profilePicture.getBackground().hashCode();
        bundle=getIntent().getExtras();
        assert bundle != null;
        phoneNumber = bundle.getString("PhoneNumber");


    }

    void initFields() {
        profilePicture = findViewById(R.id.imageButton1);
        fn = findViewById(R.id.fname);
        ln = findViewById(R.id.lname);
        passcode = findViewById(R.id.etPassword);
        rep = findViewById(R.id.rep);
        next = findViewById(R.id.next);

        next.setOnClickListener(this);
        profilePicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton1:
                 GetImageFromGallery();



            break;
                case R.id.next:
                    if(fn.getText().toString().equals("")| fn.getText().toString().length()<=2 ){
                        fn.setError("Wrong Name");
                        break;

                    }
                    if(ln.getText().toString().equals("")| fn.getText().toString().length()<=2 ){
                        ln.setError("Wrong Name");
                        break;
                    }
                    if(passcode.getText().toString().equals("")| passcode.getText().toString().length()<=5 ){
                        passcode.setError("Please Write 6 Digit code");
                        break;

                    }
                    if(!rep.getText().toString().equals(passcode.getText().toString())){
                        rep.setError("PassCode Dose not match");
                        break;

                    }
                    if(image ==profilePicture.getBackground().hashCode())
                    {
                        Toast.makeText(this,"Please Upload Profile Picture", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else
                        {
                            progressDialog = new ProgressDialog(UploadData.this);
                            progressDialog.setMessage("Loading..."); // Setting Message
                            progressDialog.setTitle("ProgressDialog"); // Setting Title
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                            progressDialog.show(); // Display Progress Dialog
                            progressDialog.setCancelable(false);
                            myRef.child(firebaseUser.getPhoneNumber()).child("fName").setValue(fn.getText().toString());
                            myRef.child(firebaseUser.getPhoneNumber()).child("lName").setValue(ln.getText().toString());
                            myRef.child(firebaseUser.getPhoneNumber()).child("passCode").setValue(passcode.getText().toString());

                                        myRef.child(firebaseUser.getPhoneNumber()).child("fName").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(UploadData.this,ChatActivity.class);
                                                    Bundle bundel1 = new Bundle();
                                                    bundel1.putString("PhoneNumber",phoneNumber);
                                                    intent.putExtras(bundel1);
                                                    startActivity(intent);
                                                    // use "username" already exists
                                                    // Let the user know he needs to pick another username.
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(UploadData.this,"Failed Uploading Data Please Try Again", Toast.LENGTH_LONG).show();
                                                    // User does not exist. NOW call createUserWithEmailAndPassword;
                                                    // Your previous code here.
                                                    return;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });




                        }


        }
    }
    public void GetImageFromGallery(){

        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 2);

    }
    public void ImageCropFunction() {
        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");

            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 300);
            CropIntent.putExtra("outputY", 300);
            CropIntent.putExtra("aspectX", 1);
            CropIntent.putExtra("aspectY", 1);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent, 1);

        } catch (ActivityNotFoundException e) {

        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {

            if (data != null) {

                uri = data.getData();

                ImageCropFunction();

            }
        }
        else if(requestCode==1) {
            if (data != null) {

                Bundle bundle = data.getExtras();

                Bitmap bitmap = bundle.getParcelable("data");
                profilePicture.setImageBitmap(getCroppedBitmap(bitmap));
                profilePicture.setBackground(ContextCompat.getDrawable(this,R.drawable.drawbg));

                handleUpload(bitmap);
            }
        }



        }


    private  void handleUpload (Bitmap bitmap)
    {
        progressDialog = new ProgressDialog(UploadData.this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("ProgressDialog"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        String uid = FirebaseAuth.getInstance().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("ProfileImages")
                .child(uid+".jpeg");
        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        getDownloadUrl(reference,progressDialog);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"On Failure:",e.getCause());
                    }
                });
    }
    private void getDownloadUrl(StorageReference reference, final ProgressDialog p)
    {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserPicture(uri,p);
            }
        });
    }
    private void setUserPicture(Uri uri, final ProgressDialog p)

    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        p.dismiss();
                        Toast.makeText(UploadData.this,"Profile Picture Success..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        p.dismiss();
                        Toast.makeText(UploadData.this,"Profile Image Failed",Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }
    @Override
    public void onBackPressed() {

    }

}
