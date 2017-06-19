package com.chancemagno.parley.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chancemagno.parley.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mAuthProgressDialog;

    @Bind(R.id.loginButton) Button mLoginButton;
    @Bind(R.id.passwordLoginEditText) EditText mPasswordLoginEditText;
    @Bind(R.id.emailLoginEditText) EditText mEmailLoginEditText;
    @Bind(R.id.forgotPasswordTextView) TextView mForgotPasswordTextView;
    @Bind(R.id.registerTextView) TextView mRegisterTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if(mUser != null){
                    mAuthProgressDialog.dismiss();
                    checkIfEmailVerified();
                }
            }
        };
        mLoginButton.setOnClickListener(this);
        createAuthProgressDialog();

        mAuth = FirebaseAuth.getInstance();

    }

    public void createAuthProgressDialog(){
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
    }

    private void checkIfEmailVerified(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            mAuthProgressDialog.dismiss();
            mPasswordLoginEditText.setText("");
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(LoginActivity.this, "Please verify your email to login", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {

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
