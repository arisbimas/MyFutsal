package com.example.myfutsal.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import com.example.myfutsal.Menus.CariLawanActivity;
import com.example.myfutsal.Menus.ChatsActivity;
import com.example.myfutsal.Menus.MyTeamActivity;
import com.example.myfutsal.Menus.PostsActivity;
import com.example.myfutsal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private CardView cvCari, cvSiap, cvPosts, cvTimKu, cvChats, cvExit;
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
        cvChats = findViewById(R.id.cv_chats);
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

                    firebaseFirestore.collection("Tim").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){

                                if (task.getResult().exists()){
                                    String siap = task.getResult().get("siap_main").toString();

                                    if (siap.equals("Siap Main")){
                                        Intent intentCariLawan = new Intent(MainActivity.this, CariLawanActivity.class);
                                        startActivity(intentCariLawan);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Tim Anda Belum Bisa Bertanding", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    sendToSetup();
                                }

                            }
                        }
                    });

                }
            });

            firebaseFirestore.collection("Tim").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        if (task.getResult().exists()){
                            String siap = task.getResult().get("siap_main").toString();

                            if (siap.equals("Tim Siap Bertanding")){
                                cvSiap.setCardBackgroundColor(Color.YELLOW);
                            } else if (siap.equals("Tim Belum Bisa Bertanding")){
                                cvSiap.setCardBackgroundColor(Color.WHITE);
                            }
                        } else {
                            sendToSetup();
                        }

                    }
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

                                if (siap.equals("Tim Siap Bertanding")){

                                    firebaseFirestore.collection("Tim").document(current_user_id).update("siap_main", "Tim Belum Bisa Bertanding").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MainActivity.this, "Tim Belum Bisa Bertanding", Toast.LENGTH_SHORT).show();
                                            cvSiap.setCardBackgroundColor(Color.WHITE);
                                        }
                                    });
                                } else if (siap.equals("Tim Belum Bisa Bertanding")){

                                    firebaseFirestore.collection("Tim").document(current_user_id).update("siap_main", "Tim Siap Bertanding").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MainActivity.this, "Tim Siap Bertanding", Toast.LENGTH_SHORT).show();
                                            cvSiap.setCardBackgroundColor(Color.YELLOW);
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
                    Intent newPostIntent = new Intent(MainActivity.this, PostsActivity.class);
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

            cvChats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentChat = new Intent(MainActivity.this, ChatsActivity.class);
                    startActivity(intentChat);
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

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Tim").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        if (task.getResult().exists()){

                            String nama_tim = task.getResult().getString("nama_tim");
                            String siap_main = task.getResult().getString("siap_main");
                            String foto_tim = task.getResult().getString("foto_tim");
                            String logo = task.getResult().getString("logo");
                            String umur = task.getResult().getString("umur");

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

                        } else {
                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();
                        }

                    }
                }
            });
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case R.id.action_logout_btn:
////                logout();
//                return true;
//            case R.id.action_setting_btn:
//                Intent intent = new Intent(MainActivity.this, SetupActivity.class);
//                startActivity(intent);
//
//                return true;
//
//            default:
//                return false;
//
//
//        }
//
//
//    }

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
                        finish();

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

    private void sendToSetup() {
        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(intent);
        finish();
    }
}
