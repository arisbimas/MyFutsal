package com.example.myfutsal.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Activities.SetupActivity;
import com.example.myfutsal.Activities.TambahPemainActivity;
import com.example.myfutsal.Menus.MyTeamActivity;
import com.example.myfutsal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileTeamFragment extends Fragment {


    private ImageView fotoTim, popUpTeam;
    private CircleImageView logoTim, fotoPemain;
    private TextView namaTim, txtSiap, txtUmur, tidakAdaMemberTxt;


    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private String current_team_id;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;

    private Context context;

    public ProfileTeamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_team, container, false);

        context = getContext();
        storageReference    = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_team_id = firebaseAuth.getCurrentUser().getUid();

        fotoTim = view.findViewById(R.id.foto_tim);
        logoTim = view.findViewById(R.id.logotim);
        namaTim = view.findViewById(R.id.nama_tim);
        txtSiap = view.findViewById(R.id.txt_siapmain);
        txtUmur = view.findViewById(R.id.txt_umur);

        popUpTeam = view.findViewById(R.id.popup_myteam);

        progressDialog = new ProgressDialog(context);
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

                        Glide.with(getActivity()).applyDefaultRequestOptions(placeholderRequest).load(foto_tim).into(fotoTim);
                        Glide.with(getActivity()).applyDefaultRequestOptions(placeholderRequest).load(logo).into(logoTim);

                    }

                }else{

                    String error = task.getException().getMessage();
                    Toast.makeText(getActivity(), "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();


                }

                progressDialog.dismiss();

            }
        });


        popUpTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog d = new AlertDialog.Builder(context)

                        .setNegativeButton("Cancel", null)
                        .setItems(new String[]{"Edit Profil Tim", "Tambah Pemain",}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dlg, int position) {

                                if (position == 0) {

                                    Intent editTimIntent = new Intent(context, SetupActivity.class);
                                    startActivity(editTimIntent);

                                } else if (position == 1) {

                                    Intent tambahpemainIntent = new Intent(context, TambahPemainActivity.class);
                                    startActivity(tambahpemainIntent);

                                }

                            }
                        })
                        .create();
                d.show();
            }
        });
        return view;
    }
}
