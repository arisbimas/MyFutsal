package com.example.myfutsal.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Adapters.PemainLawanAdapter;
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

public class ProfileTimActivity extends AppCompatActivity {

    private Toolbar profileToolbar;

    private ImageView fotoTim, popUpTeam;
    private CircleImageView logoTim, fotoPemain;
    private TextView namaTim, txtSiap, txtUmur, tidakAdaMemberTxt;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private String tim_id;

    private RecyclerView pemainRecyclerView;
    private List<Pemains> pemain_list;
    private PemainLawanAdapter pemainLawanAdapter;

    Intent intent;
    private String nama_tim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_tim);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        intent = getIntent();
        tim_id = intent.getStringExtra("tim_id");
        nama_tim = intent.getStringExtra("nama_tim");

        profileToolbar = findViewById(R.id.profiletoolbar);
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setTitle(""+nama_tim);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fotoTim = findViewById(R.id.foto_tim);
        logoTim = findViewById(R.id.logotim);
        namaTim = findViewById(R.id.nama_tim);
        txtSiap = findViewById(R.id.txt_siapmain);
        txtUmur = findViewById(R.id.txt_umur);
        tidakAdaMemberTxt = findViewById(R.id.tdk_ada_member);
        popUpTeam = findViewById(R.id.popup_myteam);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        firebaseFirestore.collection("Tim").document(tim_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

                        Glide.with(ProfileTimActivity.this).applyDefaultRequestOptions(placeholderRequest).load(foto_tim).into(fotoTim);
                        Glide.with(ProfileTimActivity.this).applyDefaultRequestOptions(placeholderRequest).load(logo).into(logoTim);

                    }

                }else{

                    String error = task.getException().getMessage();
                    Toast.makeText(ProfileTimActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();


                }

                progressDialog.dismiss();

            }
        });

        pemain_list = new ArrayList<>();
        pemainRecyclerView = findViewById(R.id.rv_pemain);
        pemainLawanAdapter = new PemainLawanAdapter(getApplicationContext(), pemain_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        pemainRecyclerView.setLayoutManager(layoutManager);
        pemainRecyclerView.setAdapter(pemainLawanAdapter);


        Query query = firebaseFirestore.collection("Pemain")
                .whereEqualTo("team_id", tim_id)
                .orderBy("nama_pemain", Query.Direction.ASCENDING);
        query.addSnapshotListener(ProfileTimActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                if (!documentSnapshots.isEmpty()) {
                    tidakAdaMemberTxt.setVisibility(View.GONE);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String pemainId = doc.getDocument().getId();
                            Pemains pemain = doc.getDocument().toObject(Pemains.class);
                            pemain_list.add(pemain);
                            pemainLawanAdapter.notifyDataSetChanged();


                        }
                    }

                } else if (documentSnapshots.isEmpty()){
                    pemainRecyclerView.setVisibility(View.GONE);

                }

            }

        });


        popUpTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog d = new AlertDialog.Builder(ProfileTimActivity.this)

                        .setItems(new String[]{"Chat"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dlg, int position) {

                                if (position == 0) {

                                    Intent toChatIntent = new Intent(ProfileTimActivity.this, MessageActivity.class);
                                    toChatIntent.putExtra("tim_id", tim_id);
                                    startActivity(toChatIntent);

                                }

                            }
                        })
                        .create();
                d.show();
            }
        });


    }
}
