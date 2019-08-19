package com.example.myfutsal.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Menus.MyTeamActivity;
import com.example.myfutsal.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupLogotim;
    private ImageView setupFotoTim;
    private Uri mainImageURI = null;
    private Toolbar setupToolbar;
    private String user_id;


    private EditText setupNamaTim;
    private Button setupSimpan;
    private ProgressDialog progressDialog;

    private StorageReference storageReferance;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReferance = FirebaseStorage.getInstance().getReference();


        setupFotoTim = findViewById(R.id.setup_foto_tim);
        setupLogotim = findViewById(R.id.setup_logotim);
        setupNamaTim = findViewById(R.id.setup_namatim);
        setupSimpan = findViewById(R.id.setup_simpan);

        setupToolbar = findViewById(R.id.setuptoolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Edit Profil Tim");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        setupSimpan.setEnabled(false);

        firebaseFirestore.collection("User").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {

                    if (task.getResult().exists()){

                      String name = task.getResult().getString("name");
                      String image = task.getResult().getString("image");

                      mainImageURI = Uri.parse(image);

                      setupNamaTim.setText(name);

                      RequestOptions placeholderRequest = new RequestOptions();
                      placeholderRequest.placeholder(R.drawable.default_image);

                      Glide.with(SetupActivity.this).applyDefaultRequestOptions(placeholderRequest).load(image).into(setupFotoTim);

                    }

                }else{

                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();


                }

                progressDialog.dismiss();
                setupSimpan.setEnabled(true);

            }
        });


        setupSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupNamaTim.getText().toString();

                if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {

                     user_id = firebaseAuth.getCurrentUser().getUid();
                     progressDialog.show();

                    final StorageReference image_path = storageReferance.child("Profile_image").child(user_id + ".jpg");
                    image_path.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return image_path.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {

                                Uri download_uri = task.getResult();

                                Map<String, String> userMap = new HashMap<>();
                                userMap.put("name",  user_name);
                                userMap.put("image", download_uri.toString());

                                firebaseFirestore.collection("User").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            Toast.makeText(SetupActivity.this, "The user Setting are updated. " , Toast.LENGTH_LONG).show();
                                            Intent mainIntent = new Intent(SetupActivity.this, MyTeamActivity.class);
                                            startActivity(mainIntent);
                                            finish();

                                        }else {

                                            String error = task.getException().getMessage();
                                            Toast.makeText(SetupActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                                        }

                                        progressDialog.dismiss();
                                    }
                                });
                                Toast.makeText(SetupActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                progressDialog.dismiss();
                            }

                        }
                    });



                }
            }
        });

        setupFotoTim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(SetupActivity.this, "permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {


                        BringImagePicker();
                    }
                } else {

                    BringImagePicker();
                }

            }
        });
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();

                setupFotoTim.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

