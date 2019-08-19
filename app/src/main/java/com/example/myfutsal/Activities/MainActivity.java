package com.example.myfutsal.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private CardView cvCari, cvSiap, cvPosts, cvTimKu, cvInfoApp, cvExit;
    private String current_user_id;

    private FloatingActionButton addPostBtn;

    private BottomNavigationView mainbottomNav;

    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private UsersFragment usersFragment;



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

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {

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

        }else{

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("User").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        if (!task.getResult().exists()) {


                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }
                    }else{

                    String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error : " + errorMessage,Toast.LENGTH_LONG).show();
                    }
                }
            });
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
