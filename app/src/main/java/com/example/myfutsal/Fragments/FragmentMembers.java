package com.example.myfutsal.Fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Activities.SetupActivity;
import com.example.myfutsal.Activities.TambahPemainActivity;
import com.example.myfutsal.Adapters.PemainAdapter;
import com.example.myfutsal.Fragments.FragmentMembers;
import com.example.myfutsal.Fragments.FragmentPosts;
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


public class FragmentMembers extends Fragment {

    private RecyclerView pemainRecyclerView;
    private List<Pemains> pemain_list;
    private PemainAdapter pemainAdapter;
    private Button tbhPemain;
    private TextView tidakAdaMemberTxt;
    private Context context;
    private String current_team_id;


    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public FragmentMembers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_members, container, false);

        context = getContext();
        storageReference    = FirebaseStorage.getInstance().getReference();
        firebaseFirestore   = FirebaseFirestore.getInstance();
        firebaseAuth        = FirebaseAuth.getInstance();
        tidakAdaMemberTxt = view.findViewById(R.id.tdk_ada_member);
        tbhPemain = view.findViewById(R.id.btn_tbhpemain);

        current_team_id = firebaseAuth.getCurrentUser().getUid();

        pemain_list = new ArrayList<>();
        pemainRecyclerView = view.findViewById(R.id.rv_pemain);
        pemainAdapter = new PemainAdapter(context, pemain_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        pemainRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        pemainRecyclerView.setAdapter(pemainAdapter);

        Query query = firebaseFirestore.collection("Pemain")
                .whereEqualTo("team_id", current_team_id)
                .orderBy("nama_pemain", Query.Direction.ASCENDING);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                if (!documentSnapshots.isEmpty()) {
                    tidakAdaMemberTxt.setVisibility(View.GONE);
                    tbhPemain.setVisibility(View.GONE);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String pemainId = doc.getDocument().getId();
                            Pemains pemain = doc.getDocument().toObject(Pemains.class);
                            pemain_list.add(pemain);
                            pemainAdapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    pemainRecyclerView.setVisibility(View.GONE);
                }

            }

        });

        tbhPemain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, TambahPemainActivity.class));
            }
        });

        return view;
    }

}
