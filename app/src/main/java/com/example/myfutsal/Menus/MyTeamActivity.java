package com.example.myfutsal.Menus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Activities.EditTimActivity;
import com.example.myfutsal.Activities.SetupActivity;
import com.example.myfutsal.Activities.TambahPemainActivity;
import com.example.myfutsal.Adapters.PemainAdapter;
import com.example.myfutsal.Model.Pemains;
import com.example.myfutsal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyTeamActivity extends AppCompatActivity {

    private Toolbar myTeamToolbar;

    private ImageView fotoTim, popUpTeam;
    private CircleImageView logoTim, fotoPemain;
    private TextView namaTim, txtSiap, txtUmur;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private String current_team_id;

    private RecyclerView pemainRecyclerView;
    private List<Pemains> pemain_list;
    private PemainAdapter pemainAdapter;

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
        txtSiap = findViewById(R.id.txt_siapmain);
        txtUmur = findViewById(R.id.txt_umur);
        popUpTeam = findViewById(R.id.popup_myteam);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        firebaseFirestore.collection("Tim").document(current_team_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {

                    if (task.getResult().exists()){

                        String nama_tim = task.getResult().getString("nama_tim");
                        String foto_tim = task.getResult().getString("foto_tim");
                        String logo = task.getResult().getString("logo");
                        String umur = task.getResult().getString("umur");
                        String siap = task.getResult().getString("siap_main");


                        namaTim.setText(nama_tim);
                        txtSiap.setText(siap);
                        txtUmur.setText(umur);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);

                        Glide.with(MyTeamActivity.this).applyDefaultRequestOptions(placeholderRequest).load(foto_tim).into(fotoTim);
                        Glide.with(MyTeamActivity.this).applyDefaultRequestOptions(placeholderRequest).load(logo).into(logoTim);

                    }

                }else{

                    String error = task.getException().getMessage();
                    Toast.makeText(MyTeamActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();


                }

                progressDialog.dismiss();

            }
        });

        pemain_list = new ArrayList<>();
        pemainRecyclerView = findViewById(R.id.rv_pemain);
        pemainAdapter = new PemainAdapter(getApplicationContext(), pemain_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        pemainRecyclerView.setLayoutManager(layoutManager);
        pemainRecyclerView.setAdapter(pemainAdapter);


        Query query = firebaseFirestore.collection("Pemain")
                .whereEqualTo("team_id", current_team_id)
                .orderBy("nama_pemain", Query.Direction.ASCENDING);
        query.addSnapshotListener(MyTeamActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                if (!documentSnapshots.isEmpty()) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String pemainId = doc.getDocument().getId();
                            Pemains pemain = doc.getDocument().toObject(Pemains.class);
                            pemain_list.add(pemain);
                            pemainAdapter.notifyDataSetChanged();

                        }
                    }

                } else {
//                    pemainRecyclerView.setVisibility(View.GONE);
//                    emptyTxt.setVisibility(View.VISIBLE);
//                    emptyImg.setVisibility(View.VISIBLE);
                }

            }

        });


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
