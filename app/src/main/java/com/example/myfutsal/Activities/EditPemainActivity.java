package com.example.myfutsal.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPemainActivity extends AppCompatActivity {

    private Toolbar editpemaintoolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private CircleImageView fotoPemain;
    private EditText namaPemain, umurPemain, umurPemainGrade;
    private Button btnSimpan;
    Uri mainFotoPemain = null;

    private String current_tim_id;
    private String current_pemain_id;
    private ProgressDialog dialog;
    private boolean is_Changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pemain);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        current_tim_id = mAuth.getUid();
        Intent intent = getIntent();
        current_pemain_id = intent.getStringExtra("pemain_id");

        editpemaintoolbar = findViewById(R.id.editpemaintoolbar);
        fotoPemain = findViewById(R.id.foto_pemain);
        namaPemain = findViewById(R.id.nama_pemain);
        umurPemain = findViewById(R.id.umur_pemain);
        umurPemainGrade = findViewById(R.id.umur_pemaingrade);

        btnSimpan = findViewById(R.id.btn_editpemain);

        setSupportActionBar(editpemaintoolbar);
        getSupportActionBar().setTitle("Edit Pemain");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(EditPemainActivity.this);
        dialog.show();

        firebaseFirestore.collection("Pemain").document(current_pemain_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists()){

                        String name = task.getResult().getString("nama_pemain");
                        String umurangka = task.getResult().getString("umur_angka");
                        String umurgrade = task.getResult().getString("umur_pemain");
                        String image = task.getResult().getString("foto_pemain");

                        mainFotoPemain = Uri.parse(image);
                        namaPemain.setText(name);
                        umurPemain.setText(umurangka);
                        umurPemainGrade.setText(umurgrade);

                        RequestOptions placeholderReq = new RequestOptions();
                        placeholderReq.placeholder(R.drawable.profile_placeholder);
                        Glide.with(EditPemainActivity.this).setDefaultRequestOptions(placeholderReq).load(image).into(fotoPemain);

                    }

                } else {

                    String errMSg = task.getException().getMessage();
                    Toast.makeText(EditPemainActivity.this, ""+ errMSg, Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();

            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nama = namaPemain.getText().toString();
                final String umur = umurPemain.getText().toString();
                final String umurgrd = umurPemainGrade.getText().toString();
                final Integer umurint = Integer.parseInt(umur);

                if (!TextUtils.isEmpty(nama) && !TextUtils.isEmpty(umur) && mainFotoPemain != null) {
                    dialog.show();

                    if(is_Changed) {

                        firebaseFirestore.collection("Tim").document(current_tim_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                final String cekumurtim = task.getResult().get("umur").toString();
//                            Integer cekumurint = Integer.parseInt(cekumurtim);
                                String dataummur = "kosong";

                                Integer anak = 16;
                                Integer remaja = 22;
                                Integer dewasa = 29;
//                            Integer tuabanget = 28+;

                                if (umurint <= anak ){
                                    dataummur = "Anak Anak";
                                } else if (umurint > anak && umurint <= remaja){
                                    dataummur = "Remaja";
                                } else if (umurint > remaja){
                                    dataummur = "Dewasa";
                                }

//                            Toast.makeText(TambahPemainActivity.this, ""+cekumurtim, Toast.LENGTH_SHORT).show();

                                if (dataummur.contains(cekumurtim)){

                                    Toast.makeText(EditPemainActivity.this, "Umur Sesuai", Toast.LENGTH_SHORT).show();
                                    dialog.setMessage("Tambah Pemain...");
                                    dialog.show();

                                    final StorageReference image_path = storageReference.child("Foto_Pemain").child(current_pemain_id + ".jpg");

                                    UploadTask uploadTask = image_path.putFile(mainFotoPemain);
                                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()) {
                                                throw task.getException();
                                            }

                                            // Continue with the task to get the download URL
                                            return image_path.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {

                                                storeFirestore(task, current_pemain_id, nama, cekumurtim, umurint);

                                            } else {

                                                String errMsg = task.getException().getMessage();
                                                Toast.makeText(EditPemainActivity.this, "Image Error " + errMsg, Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(EditPemainActivity.this, "Umur tidak sesuai dengan kriteria Tim ini", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                    }else {

                        storeFirestore(null, current_pemain_id, nama, umurgrd , umurint);

                    }
                } else if (mainFotoPemain == null){

                    Toast toast = Toast.makeText(EditPemainActivity.this, "Please select photo", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                } else if (TextUtils.isEmpty(nama)){

                    Toast toast = Toast.makeText(EditPemainActivity.this, "Isikan nama pemain", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                } else if (TextUtils.isEmpty(umur)){

                    Toast toast = Toast.makeText(EditPemainActivity.this, "Isikan umur", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }

            }
        });

        fotoPemain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(EditPemainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(EditPemainActivity.this, "Permission Ok", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(EditPemainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }
        });

    }

    private void storeFirestore(@NonNull Task<Uri> task,String user_id,  String username, String umur, Integer umurangka) {

        final ProgressDialog dialog = new ProgressDialog(EditPemainActivity.this);

        Uri downloadUri;

        if (task != null) {

            downloadUri = task.getResult();

        } else {

            downloadUri = mainFotoPemain;

        }

        Map<String, String> userMap = new HashMap<>();

        userMap.put("pemain_id" , user_id);
        userMap.put("nama_pemain" , username);
        userMap.put("umur_pemain" , umur);
        userMap.put("foto_pemain", downloadUri.toString());
        userMap.put("umur_angka", umurangka.toString());
        userMap.put("team_id", current_tim_id);

        firebaseFirestore.collection("Pemain").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

//                    setupProgress.setVisibility(View.INVISIBLE);
                    dialog.show();

                    Toast.makeText(EditPemainActivity.this, "The User Setting are Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditPemainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    String errMSg = task.getException().getMessage();
                    Toast.makeText(EditPemainActivity.this, ""+ errMSg, Toast.LENGTH_SHORT).show();

                }
//                setupProgress.setVisibility(View.INVISIBLE);
                dialog.dismiss();

            }
        });
    }

    private void BringImagePicker() {

        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(EditPemainActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainFotoPemain = result.getUri();
                fotoPemain.setImageURI(mainFotoPemain);

                is_Changed = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
