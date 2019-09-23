package com.example.myfutsal.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupLogotim;
    private ImageView setupFotoTim;
    private Uri mainFotoURI = null;
    private Uri mainLogoURI = null;
    private Toolbar setupToolbar;
    private String user_id;
    Uri pictureUri = null;

    private EditText setupNamaTim;
    private Button setupSimpan;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private SpotsDialog progressDialog;

    private StorageReference storageReferance;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    static int CAMERA_REQUEST_CODE = 228;
    static int CAMERA_REQUEST_CODE1 = 229;

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
        radioGroup = findViewById(R.id.radiogroup);
        setupSimpan = findViewById(R.id.setup_simpan);

        setupToolbar = findViewById(R.id.setuptoolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Edit Profil Tim");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new SpotsDialog(this);
        progressDialog.show();
        setupSimpan.setEnabled(false);

        firebaseFirestore.collection("Tim").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        String nama_tim = task.getResult().getString("nama_tim");
                        String foto_tim = task.getResult().getString("foto_tim");
                        String logo = task.getResult().getString("logo");

                        mainFotoURI = Uri.parse(foto_tim);
                        mainLogoURI = Uri.parse(logo);

                        setupNamaTim.setText(nama_tim.toUpperCase());

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);

                        Glide.with(SetupActivity.this).applyDefaultRequestOptions(placeholderRequest).load(foto_tim).into(setupFotoTim);
                        Glide.with(SetupActivity.this).applyDefaultRequestOptions(placeholderRequest).load(logo).into(setupLogotim);

                    }

                } else {

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

                final String nama_tim = setupNamaTim.getText().toString();

                if (!TextUtils.isEmpty(nama_tim) && mainFotoURI != null && mainLogoURI != null) {

                    user_id = firebaseAuth.getCurrentUser().getUid();
                    progressDialog.show();

                    final StorageReference foto_path = storageReferance.child("Foto_Tim").child(user_id + ".jpg");
                    foto_path.putFile(mainFotoURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return foto_path.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull final Task<Uri> task1) {

                            if (task1.isSuccessful()) {

                                final StorageReference logo_path = storageReferance.child("Logo_Tim").child(user_id + ".jpg");

                                logo_path.putFile(mainLogoURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        return logo_path.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task2) {

                                        if (task2.isSuccessful()) {

                                            Uri download_urifoto = task1.getResult();
                                            Uri download_urilogo = task2.getResult();

                                            // get selected radio button from radioGroup
                                            int selectedId = radioGroup.getCheckedRadioButtonId();

                                            // find the radiobutton by returned id
                                            radioButton = (RadioButton) findViewById(selectedId);

                                            Map<String, String> userMap = new HashMap<>();
                                            userMap.put("nama_tim", nama_tim.toUpperCase());
                                            userMap.put("foto_tim", download_urifoto.toString());
                                            userMap.put("logo", download_urilogo.toString());
                                            userMap.put("umur", radioButton.getText().toString());
                                            userMap.put("siap_main", "Tim Belum Bisa Bertanding");
                                            userMap.put("tim_id", user_id);


                                            firebaseFirestore.collection("Tim").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(SetupActivity.this, "Setting are updated. ", Toast.LENGTH_LONG).show();
                                                        Intent mainIntent = new Intent(SetupActivity.this, MyTeamActivity.class);
                                                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(mainIntent);
                                                        progressDialog.dismiss();
                                                        finish();

                                                    } else {

                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(SetupActivity.this, "(DB Error) : " + error, Toast.LENGTH_LONG).show();

                                                    }

                                                    progressDialog.dismiss();
                                                }
                                            });

                                        } else {

                                            String error = task2.getException().getMessage();
                                            Toast.makeText(SetupActivity.this, "(Logo Tim Error) : " + error, Toast.LENGTH_LONG).show();

                                            progressDialog.dismiss();

                                        }
                                    }
                                });

                                Toast.makeText(SetupActivity.this, "Tersimpan", Toast.LENGTH_LONG).show();

                            } else {

                                String error = task1.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "(Foto Tim Error) : " + error, Toast.LENGTH_LONG).show();

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
                    invokeCamera();
                }

            }
        });

        setupLogotim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    invokeCamera1();
                }

            }
        });

    }

    public void invokeCamera() {

        // get a file reference
//        mainFotoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName(), createImageFile()); // Make Uri file example file://storage/emulated/0/Pictures/Civil_ID20180924_180619.jpg


        Uri picUri = pictureUri;
        startCropImageActivity(picUri, RC_CROP);
    }

    public void invokeCamera1() {

        // get a file reference
//        mainLogoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName(), createImageFile()); // Make Uri file example file://storage/emulated/0/Pictures/Civil_ID20180924_180619.jpg


        Uri picUri = pictureUri;
        startCropImageActivity(picUri, RC_CROP1);
    }

    private static final int RC_CROP = 100;
    private static final int RC_CROP1 = 200;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)  // resultCode: -1
        {
            if (requestCode == CAMERA_REQUEST_CODE) // requestCode: 288
            {

                Toast.makeText(SetupActivity.this, "Image 1 save",
                        Toast.LENGTH_SHORT).show();
            }
            if (requestCode == CAMERA_REQUEST_CODE1) {

                Toast.makeText(SetupActivity.this, "Image 2 save",
                        Toast.LENGTH_SHORT).show();
            }

            if (requestCode == RC_CROP) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                //put image on first ImageView
                mainFotoURI = result.getUri();
                setupFotoTim.setImageURI(mainFotoURI);
            }

            if (requestCode == RC_CROP1) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                //put image on second ImageView
                mainLogoURI = result.getUri();
                setupLogotim.setImageURI(mainLogoURI);
            }

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            // if there is any error show it
//            Exception error = result.getError();
            Toast.makeText(this, "+ error", Toast.LENGTH_LONG).show();
        }
    }


    private void startCropImageActivity(Uri imageUri, int requestCode) {
        Intent vCropIntent = CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(512,512)
                .setMultiTouchEnabled(true)
                .getIntent(this);

        startActivityForResult(vCropIntent, requestCode);
    }
}

