package com.chancemagno.parley.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chancemagno.parley.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private ProgressDialog mAuthProgressDialog;

    @Bind(R.id.emailEditText)
    EditText mEmailEditText;
    @Bind(R.id.resetPasswordButton)
    Button mResetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);

        createAuthProgressDialog();

        mAuth = FirebaseAuth.getInstance();
        mResetPasswordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mResetPasswordButton){
            String emailToReset = mEmailEditText.getText().toString().trim();

            boolean validEmail = isValidEmail(emailToReset);

            if(!validEmail) return;

            mAuthProgressDialog.show();

            mAuth.sendPasswordResetEmail(emailToReset).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mAuthProgressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();

                        final Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);

                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3500);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        thread.start();
                } else {
                        mAuthProgressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isValidEmail(String email){
        boolean isGoodEmail = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if(!isGoodEmail){
            mEmailEditText.setError("Please enter a valid email address");
        }
        return isGoodEmail;
    }

    public void createAuthProgressDialog(){
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Attempting to send password reset...");
        mAuthProgressDialog.setCancelable(false);
    }
}

