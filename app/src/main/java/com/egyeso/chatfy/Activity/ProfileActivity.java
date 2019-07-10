package com.egyeso.chatfy.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.egyeso.chatfy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    ImageView mProfileImage;
    Button mProfileSendReqBtn,mProfileDeclineBtn;
    DatabaseReference mUsersDatabase,mFriendReqDatabase;
    FirebaseUser mCurrent_user;
    ProgressDialog mProgressDialog;
    String mCurrent_state;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_id = getIntent().getStringExtra("user_id");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileImage = findViewById(R.id.profile_image);
        mProfileSendReqBtn = findViewById(R.id.profile_send_req_btn);
        mProfileDeclineBtn = findViewById(R.id.profile_decline_btn);
        mProfileSendReqBtn.setOnClickListener(this);
        mProfileDeclineBtn.setOnClickListener(this);

        mCurrent_state = "not_friends";
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                String status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                String image = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(mProfileImage);
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profile_send_req_btn:
                SentReq();
                break;

            case R.id.profile_decline_btn:

                break;
        }
    }

    private void SentReq() {
        if (mCurrent_state.equals("not_friends")){
            mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).child("request_type").setValue("send").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).child("request_type")
                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileActivity.this, "Request Sent Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(ProfileActivity.this,"Failed Sending Request.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
