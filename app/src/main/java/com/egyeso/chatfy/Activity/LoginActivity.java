package com.egyeso.chatfy.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.egyeso.chatfy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    TextInputLayout mLoginEmail,mLoginPassword;
    TextView IntentLogin;
    Button mLogin;
    ProgressDialog mLogProgress;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        IntentLogin = findViewById(R.id.Intent_login);
        IntentLogin.setOnClickListener(this);
        mLoginEmail = findViewById(R.id.txt_email_login);
        mLoginPassword = findViewById(R.id.txt_password_login);
        mLogin = findViewById(R.id.btn_login);
        mLogin.setOnClickListener(this);
        mLogProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Intent_login:
                startActivity(new Intent(this,RegisterActivity.class));
                break;

            case R.id.btn_login:
                String email = Objects.requireNonNull(mLoginEmail.getEditText()).getText().toString().trim();
                String password = Objects.requireNonNull(mLoginPassword.getEditText()).getText().toString().trim();
                if (!TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    mLogProgress.setTitle("Login User");
                    mLogProgress.setMessage("Please wait while we Login your account !");
                    mLogProgress.setCanceledOnTouchOutside(false);
                    mLogProgress.show();
                    Login(email,password);
                }
                    break;
        }
    }

    private void Login(final String email, final String password ) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mLogProgress.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();
                        } else {
                           mLogProgress.hide();
                            Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getResult()).toString(),
                                    Toast.LENGTH_SHORT).show();                        }
                    }
                });
    }
}
