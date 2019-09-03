package com.example.myfutsal.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.HomeFragment;
import com.example.myfutsal.Menus.CariLawanActivity;
import com.example.myfutsal.Menus.InfoAppActivity;
import com.example.myfutsal.Menus.MyTeamActivity;
import com.example.myfutsal.ProfileFragment;
import com.example.myfutsal.R;
import com.example.myfutsal.UsersFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private CardView cvCari, cvSiap, cvPosts, cvTimKu, cvInfoApp, cvExit;
    private String current_user_id;

    private TextView namaTeam, siapMain, txtUmur;
    private CircleImageView logoTeam;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cvCari = findViewById(R.id.cv_carilawan);
        cvSiap = findViewById(R.id.cv_siapbertanding);
        cvPosts = findViewById(R.id.cv_posts);
        cvTimKu = findViewById(R.id.cv_timsaya);
        cvInfoApp = findViewById(R.id.cv_infoapp);
        cvExit = findViewById(R.id.cv_keluar);

        namaTeam = findViewById(R.id.nama_tim);
        siapMain = findViewById(R.id.txt_siapmain);
        logoTeam = findViewById(R.id.logo_tim);
        txtUmur = findViewById(R.id.txt_umur);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();




        if (mAuth.getCurrentUser() != null) {

            current_user_id = mAuth.getCurrentUser().getUid();

            //PILIH MENU

            cvCari.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentCariLawan = new Intent(MainActivity.this, CariLawanActivity.class);
                    startActivity(intentCariLawan);
                }
            });

            cvSiap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    firebaseFirestore.collection("Tim").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                String siap = task.getResult().getString("siap_main");

                                if (siap.equals("Siap Main")){

                                    firebaseFirestore.collection("Tim").document(current_user_id).update("siap_main", "Belum Siap Main").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MainActivity.this, "Belum Siap Main", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else if (siap.equals("Belum Siap Main")){

                                    firebaseFirestore.collection("Tim").document(current_user_id).update("siap_main", "Siap Main").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MainActivity.this, "Siap Main", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        }
                    });

                }
            });

            cvPosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(newPostIntent);
                }
            });

            cvTimKu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentMyTeam = new Intent(MainActivity.this, MyTeamActivity.class);
                    startActivity(intentMyTeam);
                }
            });

            cvInfoApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentInfoApp = new Intent(MainActivity.this, InfoAppActivity.class);
                    startActivity(intentInfoApp);
                }
            });

            cvExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //exit
                    signOut();
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {

            sendToLogin();

        } else {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading..");
            progressDialog.show();

            firebaseFirestore.collection("Tim").document(current_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    if (e != null) {
                        Log.w("MainAc", "Listen failed.", e);
                        return;
                    }

                    if (!documentSnapshot.exists()){

                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        startActivity(setupIntent);
                        finish();

                    } else if (documentSnapshot != null && documentSnapshot.exists()) {
                        String nama_tim = documentSnapshot.getString("nama_tim");
                        String siap_main = documentSnapshot.getString("siap_main");
                        String foto_tim = documentSnapshot.getString("foto_tim");
                        String logo = documentSnapshot.getString("logo");
                        String umur = documentSnapshot.getString("umur");

                        namaTeam.setText(nama_tim);
                        siapMain.setText(siap_main);
                        txtUmur.setText(umur);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);

//                        Glide.with(MainActivity.this).applyDefaultRequestOptions(placeholderRequest).load(foto_tim).into(fotoTim);
                        try {
                            Glide.with(MainActivity.this).applyDefaultRequestOptions(placeholderRequest).load(logo).into(logoTeam);
                        } catch (Exception e1){
                            Toast.makeText(MainActivity.this, ""+e1, Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();
                    }
                    else {
                        Log.d("MainAc", "Current data: null");
                    }

                }
            });
//            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                    if (task.isSuccessful()) {
//
//
//
//
//                        if (!task.getResult().exists()) {
//
//
//                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
//                            startActivity(setupIntent);
//                            finish();
//
//                        }
//                    } else {
//
//                        String errorMessage = task.getException().getMessage();
//                        Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
//                    }
//                }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
//                logout();
                return true;
            case R.id.action_setting_btn:
                Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(intent);

                return true;

            default:
                return false;


        }


    }

    //sign out method
    public void signOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Keluar dari Aplikasi?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mAuth.signOut();
                        sendToLogin();


                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginForm.class);
        startActivity(intent);
        finish();
    }
}
