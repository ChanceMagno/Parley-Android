package com.chancemagno.parley.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chancemagno.parley.R;
import com.chancemagno.parley.constants.Constants;
import com.chancemagno.parley.ui.CreateEventActivity;
import com.chancemagno.parley.ui.CreateProfileActivity;
import com.chancemagno.parley.ui.LoginActivity;
import com.chancemagno.parley.ui.ProfileActivity;
import com.chancemagno.parley.ui.SearchForFriendsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.profileButton) Button mProfileButton;
    @Bind(R.id.createEventButton) Button mCreateEventButton;
    @Bind(R.id.addFriendsButton) Button mSearchForFriendsButton;
    @Bind(R.id.logoutButton) Button mLogoutButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private ViewPager mViewPager;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);

        mProfileButton.setOnClickListener(this);
        mCreateEventButton.setOnClickListener(this);
        mSearchForFriendsButton.setOnClickListener(this);
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
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        } else if (v == mCreateEventButton){
            Intent intent2 = new Intent(getActivity(), CreateEventActivity.class);
            startActivity(intent2);
        } else if(v == mSearchForFriendsButton){
            Intent intent1 = new Intent(getActivity(), SearchForFriendsActivity.class);
            startActivity(intent1);
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
