package com.example.myfutsal.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class EditPostActivity extends AppCompatActivity {

    private ImageView mSelectImage;
    private EditText mDesc;
    private Button mSubmit;
    private Toolbar editPostToolbar;
    private Uri mainImageUri;


    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_tim_id;
    private String blogPostId;
    private SpotsDialog dialog;
    private boolean is_Changed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        editPostToolbar = findViewById(R.id.editposttoolbar);
        setSupportActionBar(editPostToolbar);
        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        mSelectImage = findViewById(R.id.edit_post_image);
        mDesc = findViewById(R.id.edit_post_desc);
        mSubmit = findViewById(R.id.edit_btn);

        current_tim_id = firebaseAuth.getCurrentUser().getUid();

        dialog = new SpotsDialog(EditPostActivity.this);

        blogPostId = getIntent().getStringExtra("post_id");

        dialog.show();
        firebaseFirestore.collection("Posts").document(blogPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        String desc = task.getResult().getString("keterangan");
                        String image = task.getResult().getString("foto_post");

                        mainImageUri = Uri.parse(image);

                        mDesc.setText(desc);

                        RequestOptions placeholderReq = new RequestOptions();
                        placeholderReq.placeholder(R.drawable.profile_placeholder);
                        Glide.with(EditPostActivity.this).setDefaultRequestOptions(placeholderReq).load(image).into(mSelectImage);

                    }

                } else {

                    String errMSg = task.getException().getMessage();
                    Toast.makeText(EditPostActivity.this, "" + errMSg, Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();
            }
        });


        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BringImagePicker();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String desc = mDesc.getText().toString();

                if (!TextUtils.isEmpty(desc) && mainImageUri != null) {
//                    setupProgress.setVisibility(View.VISIBLE);
                    dialog.show();

                    if (is_Changed) {

                        String randomName = UUID.randomUUID().toString();

                        StorageReference file_path = storageReference.child("post_image").child(randomName + ".jpg");
                        UploadTask uploadTask = file_path.putFile(mainImageUri);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return file_path.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {

                                    Uri downloadUri = task.getResult();
                                    String imageUri = downloadUri.toString();
                                    String des = desc.toString();

                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("foto_post", imageUri);
                                    postMap.put("keterangan", des);
                                    postMap.put("tim_id", current_tim_id);
                                    postMap.put("waktu", FieldValue.serverTimestamp());

                                    storeFirestore(task, blogPostId, desc);
                                } else {
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(EditPostActivity.this, "Image Error " + errMsg, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });

                    } else {

                        storeFirestore(null , blogPostId, desc);

                    }
                } else if (mainImageUri == null) {

                    Toast toast = Toast.makeText(EditPostActivity.this, "Please select photo", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                } else if (TextUtils.isEmpty(desc)) {

                    Toast toast = Toast.makeText(EditPostActivity.this, "Please enter Description Post", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }

            }
        });

    }


    private void BringImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(EditPostActivity.this);
    }

    private void storeFirestore(@NonNull Task<Uri> task, String post_id,  String desc) {

        SpotsDialog dialog = new SpotsDialog(EditPostActivity.this);

        Uri downloadUriImg;

        if (task != null) {

            downloadUriImg = task.getResult();

        } else {

            downloadUriImg = mainImageUri;

        }

        Map<String, Object> userMap = new HashMap<>();

        userMap.put("keterangan" , desc);
        userMap.put("foto_post", downloadUriImg.toString());

        firebaseFirestore.collection("Posts").document(post_id).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

//                    setupProgress.setVisibility(View.INVISIBLE);
                    dialog.show();

                    Toast.makeText(EditPostActivity.this, "Post Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditPostActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    String errMSg = task.getException().getMessage();
                    Toast.makeText(EditPostActivity.this, ""+ errMSg, Toast.LENGTH_SHORT).show();

                }
//                setupProgress.setVisibility(View.INVISIBLE);
                dialog.dismiss();

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageUri = result.getUri();
                mSelectImage.setImageURI(mainImageUri);

                is_Changed = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}