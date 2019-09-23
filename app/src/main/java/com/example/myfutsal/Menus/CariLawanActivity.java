package com.example.myfutsal.Menus;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfutsal.Adapters.CariLawanAdapter;
import com.example.myfutsal.Model.Lawan;
import com.example.myfutsal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class CariLawanActivity extends AppCompatActivity {

    private static final String TAG = "CariLawanActivity";
    private Toolbar cariLawanToolbar;
    private RecyclerView cariLawanRv;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_team_id;
    private Boolean isFirstPageFirstLoad = true;
    private DocumentSnapshot lastVisible;
    private TextView tidakAdaLawan;

    private ProgressDialog dialog;

    private List<Lawan> lawans_list;
    private CariLawanAdapter cariLawanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_lawan);

        lawans_list = new ArrayList<>();

        cariLawanToolbar = findViewById(R.id.carilawantoolbar);
        cariLawanRv = findViewById(R.id.rv_carilawan);
        tidakAdaLawan = findViewById(R.id.tidak_ada_lawan);


        firebaseAuth = FirebaseAuth.getInstance();
        current_team_id = firebaseAuth.getUid();

        setSupportActionBar(cariLawanToolbar);
        getSupportActionBar().setTitle("Tim Yang Tersedia");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cariLawanAdapter = new CariLawanAdapter(lawans_list);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(CariLawanActivity.this, 2);
        cariLawanRv.setLayoutManager(mGridLayoutManager);
        cariLawanRv.setAdapter(cariLawanAdapter);
        cariLawanRv.setHasFixedSize(true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            firstQ();

        }


    }

    private void firstQ() {

        firebaseFirestore.collection("Tim").document(current_team_id).get().addOnCompleteListener(this,new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    String usiatimsaya = task.getResult().get("umur").toString();

                    CollectionReference collectionReference = firebaseFirestore.collection("Tim");

                    Query firstQuery = collectionReference.whereEqualTo("umur", usiatimsaya);

                    firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "listen:error", e);
                                return;
                            }

                            if (!queryDocumentSnapshots.isEmpty()) {

                                if (isFirstPageFirstLoad) {

                                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                    lawans_list.clear();

                                }

                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        String lawanId = doc.getDocument().getId();
                                        Lawan lawanList = doc.getDocument().toObject(Lawan.class).withId(lawanId);

                                        //ALGORITMANYA
                                        if (!lawanList.getTim_id().equals(current_team_id) && lawanList.getSiap_main().contains("Siap Main")) {
                                            lawans_list.add(lawanList);
                                            tidakAdaLawan.setVisibility(View.GONE);
                                            cariLawanAdapter.notifyItemInserted(lawans_list.size());
                                            cariLawanAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }

                                    }
                                }
                                if (lawans_list.isEmpty()) {
                                    tidakAdaLawan.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                }


                            } else {

                                Toast.makeText(getApplicationContext(), "Tidak Ada Lawan Tersedia Saat Ini", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });

                }

            }
        });

    }
}
