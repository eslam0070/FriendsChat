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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout mDisplayName,mEmail,mPassword;
    Button buttonCreate;
    TextView InLog;
    FirebaseAuth mAuth;
    ProgressDialog mRegProgress;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mDisplayName = findViewById(R.id.txt_display);
        mEmail = findViewById(R.id.txt_email);
        mPassword = findViewById(R.id.txt_password);
        buttonCreate = findViewById(R.id.create_acc);
        InLog = findViewById(R.id.Intent_login);
        buttonCreate.setOnClickListener(this);
        InLog.setOnClickListener(this);
        mRegProgress = new ProgressDialog(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.create_acc:
                String display_name = Objects.requireNonNull(mDisplayName.getEditText()).getText().toString().trim();
                String email = Objects.requireNonNull(mEmail.getEditText()).getText().toString().trim();
                String password = Objects.requireNonNull(mPassword.getEditText()).getText().toString().trim();
                if (!TextUtils.isEmpty(display_name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    CreateAccount(display_name,email,password);
                }
                break;

            case R.id.Intent_login:
                startActivity(new Intent(this,LoginActivity.class));
                break;
        }
    }

    private void CreateAccount(final String display_name, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = Objects.requireNonNull(current_user).getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name",display_name);
                            userMap.put("status","Hi there I`m using Friends Chat.");
                            userMap.put ("image","default");
                            userMap.put("thumb_image","default");
                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        mRegProgress.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        } else {
                            mRegProgress.hide();
                            Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getResult()).toString(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
