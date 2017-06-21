package com.chancemagno.parley.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chancemagno.parley.R;
import com.chancemagno.parley.constants.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.createUserButton)
    Button mCreateUserButton;
    @Bind(R.id.nameEditText)
    EditText mNameEditText;
    @Bind(R.id.emailEditText) EditText mEmailEditText;
    @Bind(R.id.passwordEditText) EditText mPasswordEditText;
    @Bind(R.id.confirmPasswordEditText) EditText mConfirmPasswordEditText;
    @Bind(R.id.loginTextView)
    TextView mLoginTextView;

    private FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mAuthProgressDialog;
    private String mName;
    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mEditor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreference.edit();

        mAuth = FirebaseAuth.getInstance();
        String mName;
        mLoginTextView.setOnClickListener(this);
        mCreateUserButton.setOnClickListener(this);

        createAuthStateListener();


    }

    @Override
    public void onClick(View view){
        if(view == mLoginTextView) {
            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        if(view == mCreateUserButton) {
            createNewUser();
        }
    }

    private void createNewUser(){
        mName = mNameEditText.getText().toString().trim();
        createAuthProgressDialog();
        final String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        String confirmPassword = mConfirmPasswordEditText.getText().toString().trim();

        boolean validEmail = isValidEmail(email);
        boolean validName = isValidName(mName);
        boolean validPassword = isValidPassword(password, confirmPassword);

        if(!validName || !validEmail || !validPassword) return;

        mAuthProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mAuthProgressDialog.dismiss();
                        if(task.isSuccessful()){
                            updateProfile();
                            sendVerificationEmail();
                            setProfileUpdateStatus();
                        } else {
                            Toast.makeText(CreateAccountActivity.this, "authentication failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Email failed to send", Toast.LENGTH_SHORT).show();

                }    FirebaseAuth.getInstance().signOut();
            }
        });
    }

    public void updateProfile(){
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(mName).build();
        user.updateProfile(profileUpdate);
    }


    private void createAuthStateListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    updateProfile();
                    Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    public void setProfileUpdateStatus(){
        mEditor.putBoolean(String.valueOf(Constants.PREFERENCES_PROFILE_STATUS), false).apply();
    }

    private boolean isValidEmail(String email){
        boolean isGoodEmail = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if(!isGoodEmail){
            mEmailEditText.setError("Please enter a valid email address");
        }
        return isGoodEmail;
    }

    private boolean isValidName(String name) {
        if(name.equals("")){
            mNameEditText.setError("Please enter your name");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confirmPassword){
        if(password.length() < 6){
            mPasswordEditText.setError("Password much be atleast 6 characters long");
            return false;
        } else if(!password.equals(confirmPassword)){
            mPasswordEditText.setError("Passwords do not match");
            return false;
        }
        return true;

    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Creating your account");
        mAuthProgressDialog.setMessage(String.format("Thanks for registering %s!", mName));
        mAuthProgressDialog.setCancelable(false);
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