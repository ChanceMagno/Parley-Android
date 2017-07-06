package com.chancemagno.parley.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.chancemagno.parley.R;
import com.chancemagno.parley.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {
    @Bind(R.id.nameTextView) TextView mNameTextView;
    @Bind(R.id.friendInviteTextView) TextView mFriendInviteTextView;
    @Bind(R.id.eventInviteTextView) TextView mEventInviteTextView;
    @Bind(R.id.profileImageView) ImageView mProfileImageView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public User userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = mAuth.getCurrentUser();
            if(user == null){
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            }
        };

        getUserInfoFromDatabase();
    }

    public void getUserInfoFromDatabase(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference userProfileRef = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid()).child("profile");
        userProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              userProfile = dataSnapshot.getValue(User.class);
                setProfileInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setProfileInfo(){
        mNameTextView.setText(userProfile.getFullName());
        Picasso.with(mProfileImageView.getContext()).load(userProfile.getPhotoURL()).fit().centerCrop().into(mProfileImageView);
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}