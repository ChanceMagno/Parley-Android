package com.chancemagno.parley.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.Toast;

import com.chancemagno.parley.R;
import com.chancemagno.parley.constants.Constants;
import com.chancemagno.parley.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.ACTION_PICK;

public class CreateProfileActivity extends AppCompatActivity implements OnClickListener{
    @Bind(R.id.profileImageView) RoundedImageView mProfileImageView;
    @Bind(R.id.useCameraFloatingActionButton) FloatingActionButton mUseCameraButton;
    @Bind(R.id.openGalleryFloatingActionButton) FloatingActionButton mOpenGalleryButton;
    @Bind(R.id.saveUserButton) FloatingActionButton mSaveButton;
    @Bind(R.id.firstNameEditText) EditText mNameEditText;
    @Bind(R.id.lastNameEditText) EditText mLastNameEditText;

    private ProgressDialog mSavingProgressDialog;

    public static final int REQUEST_IMAGE_CAPTURE = 111;
    public static final int ACTIVITY_SELECT_IMAGE = 1234;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mEditor;

    private StorageReference mStorageRef;
    private StorageReference mProfileImageRef;
    FirebaseStorage mStorage;
    Bitmap imageBitmap;
    String profileImageTitle;
    String firstName;
    String lastName;

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

        user = FirebaseAuth.getInstance().getCurrentUser();

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl(Constants.IMAGE_STORAGE_URL);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mSaveButton.setOnClickListener(this);
        mUseCameraButton.setOnClickListener(this);
        mOpenGalleryButton.setOnClickListener(this);

        createAuthProgressDialog();

    }

    @Override
    public void onClick(View v) {
        if(v == mUseCameraButton){
            launchCamera();
        } else if(v == mOpenGalleryButton){
                checkPermissions();
        }  else if(v == mSaveButton){
            mSavingProgressDialog.show();
            uploadFile(imageBitmap);
        }
    }

    public void launchCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(CreateProfileActivity.this.getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void openGallery(){
        Intent takePictureIntent = new Intent(ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(takePictureIntent, ACTIVITY_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (data != null){
            if(requestCode == REQUEST_IMAGE_CAPTURE  && resultCode ==  CreateProfileActivity.this.RESULT_OK){
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                mProfileImageView.setImageBitmap(imageBitmap);
            }
            else if (requestCode == ACTIVITY_SELECT_IMAGE){
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                imageBitmap = BitmapFactory.decodeFile(filePath);
                mProfileImageView.setImageBitmap(imageBitmap);
            }
        }

    }


    private void uploadFile(Bitmap bitmap) {
        if (imageBitmap == null) {
            mSavingProgressDialog.dismiss();
            Toast.makeText(CreateProfileActivity.this, "Please upload a profile picture", Toast.LENGTH_LONG).show();
        } else {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            profileImageTitle = (user.getDisplayName() + ".jpg");
            mStorageRef = storage.getReferenceFromUrl(Constants.IMAGE_STORAGE_URL);
            mProfileImageRef = mStorageRef.child("images/" + user.getUid() + "/" + profileImageTitle);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = mProfileImageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(CreateProfileActivity.this, "Image upload failed", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    getImageUrl(user.getUid());
                }
            });
        }
    }

    public void getImageUrl(String id){
        mStorageRef.child("images/" + id + "/" +  profileImageTitle).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(mProfileImageView.getContext()).load(uri).fit().centerCrop().into(mProfileImageView);
                saveUserToFirebase(uri.toString());
            }
        });
    }

    public void saveUserToFirebase(String uri){
        firstName =mNameEditText.getText().toString().trim();
        lastName = mLastNameEditText.getText().toString().trim();
        boolean validFirstName = isValidFirstName(firstName);
        boolean validLastName = isValidLastName(lastName);

        if(!validFirstName || !validLastName) return;

        formatName();
        final DatabaseReference saveUserProfileReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("profile");

        User newUser = new User(firstName, lastName, user.getEmail(), uri);
        saveUserProfileReference.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setProfileUpdateStatus();
            }
        });
    }

    public void checkPermissions(){
        if (ContextCompat.checkSelfPermission(CreateProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateProfileActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(CreateProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        ACTIVITY_SELECT_IMAGE);
            }
            if(ContextCompat.checkSelfPermission(CreateProfileActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
            }
        } else{openGallery();}
    }

    public void  formatName(){
        firstName = firstName.toLowerCase();
        lastName = lastName.toLowerCase();
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
    }

    private boolean isValidFirstName(String firstName) {
        if(firstName.equals("")){
            mNameEditText.setError("Please enter your first name");
            return false;
        }
        return true;
    }

    private boolean isValidLastName(String lastName) {
        if(lastName.equals("")){
            mNameEditText.setError("Please enter your last name");
            return false;
        }
        return true;
    }

    private void createAuthProgressDialog() {
        mSavingProgressDialog = new ProgressDialog(this);
        mSavingProgressDialog.setTitle("Uploading your profile");
        mSavingProgressDialog.setMessage(String.format("Thanks for registering %s! ", user.getDisplayName()));
        mSavingProgressDialog.setCancelable(false);
    }


    public void setProfileUpdateStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("profileStatus");
        ref.setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mSavingProgressDialog.dismiss();
                Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


}


