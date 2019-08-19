package com.example.myfutsal.Menus;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myfutsal.Activities.EditTimActivity;
import com.example.myfutsal.Activities.SetupActivity;
import com.example.myfutsal.Activities.TambahPemainActivity;
import com.example.myfutsal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyTeamActivity extends AppCompatActivity {

    private Toolbar myTeamToolbar;

    private ImageView fotoTim, popUpTeam;
    private CircleImageView logoTim, fotoPemain;
    private TextView namaTim;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_team_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_team_id = firebaseAuth.getCurrentUser().getUid();

        myTeamToolbar = findViewById(R.id.myteamtoolbar);
        setSupportActionBar(myTeamToolbar);
        getSupportActionBar().setTitle("Tim Saya");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fotoTim = findViewById(R.id.foto_tim);
        logoTim = findViewById(R.id.logotim);
        namaTim = findViewById(R.id.nama_tim);
        popUpTeam = findViewById(R.id.popup_myteam);

        popUpTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog d = new AlertDialog.Builder(MyTeamActivity.this)

                        .setNegativeButton("Cancel", null)
                        .setItems(new String[]{"Edit Profil Tim", "Tambah Pemain",}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dlg, int position) {

                                if (position == 0) {

                                    Intent editTimIntent = new Intent(MyTeamActivity.this, SetupActivity.class);
                                    startActivity(editTimIntent);

                                } else if (position == 1) {

                                    Intent tambahpemainIntent = new Intent(MyTeamActivity.this, TambahPemainActivity.class);
                                    startActivity(tambahpemainIntent);

                                }

                            }
                        })
                        .create();
                d.show();
            }
        });
    }
}
