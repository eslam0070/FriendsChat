package com.egyeso.chatfy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.egyeso.chatfy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView mDisplayImage;
    TextView mName, mStatus;
    Button mChangeImage, mChangeStatus;
    DatabaseReference mUserDatabase;
    FirebaseUser mCurrentUser;
    static final int GALLERY_PICK = 1;
    StorageReference mImageStorge;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDisplayImage = findViewById(R.id.settings_image);
        mName = findViewById(R.id.settings_name);
        mStatus = findViewById(R.id.settings_status);
        mChangeImage = findViewById(R.id.settings_image_btn);
        mChangeStatus = findViewById(R.id.setting_status_btn);
        mChangeImage.setOnClickListener(this);
        mChangeStatus.setOnClickListener(this);

        mImageStorge = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                String image = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                String status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                String thumb_image = Objects.requireNonNull(dataSnapshot.child("thumb_image").getValue()).toString();

                mName.setText(name);
                mStatus.setText(status);
                if (!image.equals("default"))
                    Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.default_avatars).into(mDisplayImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_image_btn:
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
                break;

            case R.id.setting_status_btn:
                String status_intent = mStatus.getText().toString().trim();
                Intent intent = new Intent(SettingsActivity.this, StatusActivity.class);
                intent.putExtra("status_value", status_intent);
                startActivity(intent);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    mProgressDialog = new ProgressDialog(this);
                    mProgressDialog.setTitle("Uploading Image.");
                    mProgressDialog.setMessage("Please wait while we upload and process the image.");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    Uri resultUri = result.getUri();
                    final File thumb_filePath = new File(resultUri.getPath());
                    String current_id = mCurrentUser.getUid();
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    StorageReference filepath = mImageStorge.child("profile_images").child(current_id + ".jpg");
                    final StorageReference thumb_filepath = mImageStorge.child("profile_images").child("thumbs").child(current_id + ".jpg");
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                final String download_url = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getDownloadUrl()).toString();
                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        final String thumb_downloadUrl = Objects.requireNonNull(thumb_task.getResult().getDownloadUrl()).toString();
                                        if (thumb_task.isSuccessful()) {
                                            Map update_hashMap = new HashMap<>();
                                            update_hashMap.put("image", download_url);
                                            update_hashMap.put("thumb_image", thumb_downloadUrl);
                                            mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mProgressDialog.dismiss();
                                                        Toast.makeText(SettingsActivity.this, "Uploading Success.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(SettingsActivity.this, "Error in Uploading thumbnail.",
                                                                Toast.LENGTH_SHORT).show();
                                                        mProgressDialog.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(SettingsActivity.this, "Error in Uploading.",
                                        Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
