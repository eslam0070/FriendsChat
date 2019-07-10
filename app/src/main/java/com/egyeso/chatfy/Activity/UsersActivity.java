package com.egyeso.chatfy.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.egyeso.chatfy.Models.Users;
import com.egyeso.chatfy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    RecyclerView mUsersList;
    DatabaseReference mUsersDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, UsersViewHolder>
                        (Users.class,R.layout.users_single_layout,UsersViewHolder.class,mUsersDatabase) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, final int i) {
                usersViewHolder.setDisplayName(users.getName());
                usersViewHolder.setUserStatus(users.getStatus());
                usersViewHolder.setUserImage(users.getThumb_image(),getApplicationContext());
                final String user_id = getRef(i).getKey();
                usersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UsersActivity.this,ProfileActivity.class);
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }
                });
            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        TextView userNameView,userStatusView;
        CircleImageView circleImageView;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
        }


        void setDisplayName(String name) {
            userNameView = itemView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        void setUserStatus(String status){
            userStatusView = itemView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }
        void setUserImage(String image,Context context){
            circleImageView = itemView.findViewById(R.id.user_single_image);
            Glide.with(context).load(image).placeholder(R.drawable.default_avatars).into(circleImageView);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
