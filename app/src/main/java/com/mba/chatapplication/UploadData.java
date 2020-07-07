package com.mba.chatapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadData extends AppCompatActivity  implements View.OnClickListener,AsyncResponce{
    ImageButton profilePicture;
    EditText fn;
    EditText ln;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton1:

                    GetImageFromCamera();



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
                            progressDialog.setMessage("Please wait..."); // Setting Message
                            progressDialog.setTitle("Image processing"); // Setting Title
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                            progressDialog.show(); // Display Progress Dialog
                            progressDialog.setCancelable(false);
                            myRef.child(firebaseUser.getPhoneNumber()).child("UserID").setValue(firebaseUser.getUid());
                            myRef.child(firebaseUser.getPhoneNumber()).child("PhoneNumber").setValue(firebaseUser.getPhoneNumber());
                            myRef.child(firebaseUser.getPhoneNumber()).child("fName").setValue(fn.getText().toString());
                            myRef.child(firebaseUser.getPhoneNumber()).child("lName").setValue(ln.getText().toString());
                            myRef.child(firebaseUser.getPhoneNumber()).child("passCode").setValue(passcode.getText().toString());
                            myRef.child(firebaseUser.getPhoneNumber()).child("status").setValue("offline");


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
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 2;
    Uri photoURI;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void GetImageFromCamera() {
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
        CropImage.activity(uri).setAspectRatio(300,400)
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
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
        canvas.drawCircle(bitmap.getWidth() >> 1, bitmap.getHeight() >> 1,
                bitmap.getWidth() >> 1, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
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
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }


    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }
    Bitmap photo;
    Uri resultUri;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
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

            profilePicture.setImageBitmap(getCroppedBitmap(photo));
            profilePicture.setBackground(ContextCompat.getDrawable(this, R.drawable.drawbg));

            handleUpload(photo);
        }
        if (requestCode==2) {
          doCrop(photoURI);
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
    public Uri url;
    private void getDownloadUrl(StorageReference reference, final ProgressDialog p)
    {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url=uri;
                GetFaceId getFaceId = new GetFaceId();
                getFaceId.delegate=UploadData.this;
                getFaceId.execute(url);
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
                        Toast.makeText(UploadData.this,"Profile Picture Success..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadData.this,"Profile Image Failed",Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }
    @Override
    public void onBackPressed() {

    }

    @Override
    public void processFinish(String output) throws JSONException {
        progressDialog.dismiss();
        if(output.equals("[]"))
        {
            Toast.makeText(UploadData.this,"No face detected in the picture, Please take another picture with clear face",Toast.LENGTH_SHORT).show();
            profilePicture.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimaryDark));
            image = profilePicture.getBackground().hashCode();
        }
    }
}
