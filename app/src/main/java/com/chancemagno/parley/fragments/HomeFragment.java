package com.chancemagno.parley.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chancemagno.parley.R;
import com.chancemagno.parley.constants.Constants;
import com.chancemagno.parley.ui.CreateProfileActivity;
import com.chancemagno.parley.ui.LoginActivity;
import com.chancemagno.parley.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.profileButton) Button mProfileButton;
    @Bind(R.id.eventButton) Button mEventButton;
    @Bind(R.id.friendsButton) Button mFriendsButton;
    @Bind(R.id.logoutButton) Button mLogoutButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mEditor;
    Boolean mProfileStatus;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);

        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (mSharedPreference.getBoolean(String.valueOf(Constants.PREFERENCES_PROFILE_STATUS), false))
            mProfileStatus = true;
        else mProfileStatus = false;

        Log.i("profile status", profileStatus.toString());


        mProfileButton.setOnClickListener(this);
        mEventButton.setOnClickListener(this);
        mFriendsButton.setOnClickListener(this);
        mLogoutButton.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        return view;
    }


    @Override
    public void onClick(View v) {
        if(v == mLogoutButton){
            FirebaseAuth.getInstance().signOut();
        } else if (v == mProfileButton){
            if(mProfileStatus){
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), CreateProfileActivity.class)
            }

        }
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
