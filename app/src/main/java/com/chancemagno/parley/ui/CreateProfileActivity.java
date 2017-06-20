package com.chancemagno.parley.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chancemagno.parley.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateProfileActivity extends AppCompatActivity implements OnClickListener{
    private StorageReference mStorageRef;
    @Bind(R.id.profileImageView)
    ImageView mProfileImageView;
    @Bind(R.id.takePicButton)
    Button mtakePicButton;
    public static final int REQUEST_IMAGE_CAPTURE = 111;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Bitmap imageBitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(CreateProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mtakePicButton.setOnClickListener(this);

//        checkFilePermissions();

//        addFilePaths();
    }

    @Override
    public void onClick(View v) {
        if(v == mtakePicButton){
            launchCamera();
        }
    }

    public void launchCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(CreateProfileActivity.this.getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode ==  CreateProfileActivity.this.RESULT_OK){
            Bundle extras = data.getExtras();
             imageBitmap = (Bitmap) extras.get("data");
            uploadFile(imageBitmap);

        }
    }


    private void uploadFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String profileImageTitle = (user.getDisplayName() + ".jpg");
        StorageReference storageRef = storage.getReferenceFromUrl("gs://parley-ca23c.appspot.com/");
        StorageReference profileImageRef = storageRef.child("images/" + user.getUid()+ "/" +  profileImageTitle);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = profileImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(CreateProfileActivity.this, "epic fail", Toast.LENGTH_LONG);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CreateProfileActivity.this, "success", Toast.LENGTH_LONG);
            }
        });

    }




}
