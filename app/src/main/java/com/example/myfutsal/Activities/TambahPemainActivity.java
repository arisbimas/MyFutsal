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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfutsal.Menus.MyTeamActivity;
import com.example.myfutsal.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

public class TambahPemainActivity extends AppCompatActivity {

    private Toolbar tbhToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private CircleImageView fotoPemain;
    private EditText namaPemain, umurPemain;
    private Button btnTbh;
    Uri mainFotoPemain = null;

    private String current_tim_id;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pemain);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        current_tim_id = mAuth.getUid();

        tbhToolbar = findViewById(R.id.tbhtoolbar);
        fotoPemain = findViewById(R.id.foto_pemain);
        namaPemain = findViewById(R.id.nama_pemain);
        umurPemain = findViewById(R.id.umur_pemain);
        btnTbh = findViewById(R.id.btn_tbhpemain);

        setSupportActionBar(tbhToolbar);
        getSupportActionBar().setTitle("Tambah Pemain");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(TambahPemainActivity.this);


        fotoPemain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(TambahPemainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(TambahPemainActivity.this, "Permission Ok", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(TambahPemainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }
        });

        btnTbh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nama = namaPemain.getText().toString();
                final String umurstr = umurPemain.getText().toString();


                if (!TextUtils.isEmpty(nama) && !TextUtils.isEmpty(umurstr) && mainFotoPemain != null) {

                    final Integer umurint = Integer.parseInt(umurstr);
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

                                Toast.makeText(TambahPemainActivity.this, "Umur Sesuai", Toast.LENGTH_SHORT).show();
                                dialog.setMessage("Tambah Pemain...");
                                dialog.show();

                                String randomfotoPemain = UUID.randomUUID().toString();
                                final StorageReference image_path = storageReference.child("Foto_Pemain").child(randomfotoPemain + ".jpg");

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

                                            simpanDataPemain(task, current_tim_id, nama, cekumurtim, umurint);

                                        } else {

                                            String errMsg = task.getException().getMessage();
                                            Toast.makeText(TambahPemainActivity.this, "Image Error " + errMsg, Toast.LENGTH_SHORT).show();

//                                    setupProgress.setVisibility(View.INVISIBLE);
                                            dialog.dismiss();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(TambahPemainActivity.this, "Umur tidak sesuai dengan kriteria Tim ini", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                } else {
                    Toast.makeText(TambahPemainActivity.this, "tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void simpanDataPemain(@NonNull Task<Uri> task, String timid,  String username, String umur_pemain, Integer umurint) {


        Uri downloadUri;

        if (task != null) {

            downloadUri = task.getResult();

        } else {

            downloadUri = mainFotoPemain;

        }

        Map<String, String> pemainMap = new HashMap<>();

        String randomIdPemain = UUID.randomUUID().toString();

        pemainMap.put("pemain_id" , randomIdPemain);
        pemainMap.put("nama_pemain" , username);
        pemainMap.put("umur_pemain" , umur_pemain);
        pemainMap.put("foto_pemain", downloadUri.toString());
        pemainMap.put("team_id", timid);
        pemainMap.put("umur_angka", umurint.toString());

        firebaseFirestore.collection("Pemain").document(randomIdPemain).set(pemainMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(TambahPemainActivity.this, "Pemain Berhasil Ditambahkan", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TambahPemainActivity.this, MyTeamActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }


    private void BringImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(TambahPemainActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainFotoPemain = result.getUri();
                fotoPemain.setImageURI(mainFotoPemain);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
