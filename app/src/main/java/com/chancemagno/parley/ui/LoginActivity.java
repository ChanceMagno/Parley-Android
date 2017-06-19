package com.chancemagno.parley.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chancemagno.parley.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
        mRegisterTextView.setOnClickListener(this);

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
        if(v == mLoginButton){
            logInExistingUser();
        } else if (v == mRegisterTextView){
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        }
    }

    public void logInExistingUser(){
        String email = mEmailLoginEditText.getText().toString().trim();
        String password = mPasswordLoginEditText.getText().toString().trim();

        boolean validEmail = isValidEmail(email);
        boolean validPassword = isValidPassword(password);

       if(!validEmail || !validPassword) return;

        mAuthProgressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mAuthProgressDialog.dismiss();
                if(!task.isSuccessful()){
                    mAuthProgressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                    mPasswordLoginEditText.setText("");
                }
            }
        });
    }

    private boolean isValidEmail(String email){
        boolean isGoodEmail = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if(!isGoodEmail){
            mEmailLoginEditText.setError("Please enter a valid email address");
        }
        return isGoodEmail;
    }

    private boolean isValidPassword(String password){
        boolean isGoodPassword = (password != null && password.length() > 5);
        if(!isGoodPassword){
            mPasswordLoginEditText.setError("Please enter a password");
        }
        return isGoodPassword;
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
