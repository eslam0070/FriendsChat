package com.egyeso.chatfy.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.egyeso.chatfy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout mStatus;
    Button btnStatus;
    DatabaseReference mStatusDatabase;
    FirebaseUser mCurrentUser;
    ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = Objects.requireNonNull(mCurrentUser).getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mStatus = findViewById(R.id.status_input);
        btnStatus = findViewById(R.id.status_save_btn);
        btnStatus.setOnClickListener(this);
        mProgress = new ProgressDialog(this);

        String status_value = getIntent().getStringExtra("status_value");
        Objects.requireNonNull(mStatus.getEditText()).setText(status_value);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.status_save_btn:
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save the changes");
                mProgress.show();
                String status = Objects.requireNonNull(mStatus.getEditText()).getText().toString().trim();
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgress.dismiss();
                            startActivity(new Intent(StatusActivity.this,SettingsActivity.class));
                            finish();
                        }else {
                            Toast.makeText(StatusActivity.this, "There was some error in saving Change.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }
}
