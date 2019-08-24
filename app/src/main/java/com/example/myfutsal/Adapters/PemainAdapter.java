package com.example.myfutsal.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Activities.EditPemainActivity;
import com.example.myfutsal.Activities.SetupActivity;
import com.example.myfutsal.Activities.TambahPemainActivity;
import com.example.myfutsal.Menus.MyTeamActivity;
import com.example.myfutsal.Model.Pemains;
import com.example.myfutsal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PemainAdapter extends RecyclerView.Adapter<PemainAdapter.ProfileBlogViewHolder> {

    private Context context;
    private List<Pemains> pemain_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public PemainAdapter(Context context, List<Pemains> pemain_list) {
        this.context = context;
        this.pemain_list = pemain_list;
    }


    @NonNull
    @Override
    public PemainAdapter.ProfileBlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pemain, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ProfileBlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PemainAdapter.ProfileBlogViewHolder holder, final int position) {

        final Pemains pemain = pemain_list.get(holder.getAdapterPosition());
        String img = pemain.getFoto_pemain();
        String namapemain = pemain.getNama_pemain();

        final String blogPostId = pemain_list.get(position).PemainId;

        holder.namaPemain.setText(namapemain);

        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.profile_placeholder);
        Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(img).into(holder.fotoPemain);

        holder.fotoPemain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
//                        builder.setItems(new String[]{"Lihat Profil Pemain","Edit Profil Pemain", "Hapus Pemain"}, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dlg, int position) {
//
//                                if (position == 0) {
//
//                                    holder.showDataPopUpProfile(pemain_list.get(position).getPemain_id());
//                                    holder.mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                    holder.mDialog.show();
//
//                                } else if (position == 1) {
//
//                                    Intent intent = new Intent(context, EditPemainActivity.class);
//                                    context.startActivity(intent);
//
//                                } else if (position == 2) {
//                                    Toast.makeText(context, "hapus", Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                        })
//                        .create();
//                        builder.show();

                final AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                alertbox.setItems(new String[]{"Lihat Profil Pemain", "Edit Profil Pemain", "Hapus Pemain"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            holder.showDataPopUpProfile(pemain_list.get(position).getPemain_id());
                            holder.mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            holder.mDialog.show();

                        } else if (which == 1) {

                            Intent intent = new Intent(context, EditPemainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        } else if (which == 2) {
                            Toast.makeText(context, "hapus", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertbox.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return pemain_list.size();
    }

    public class ProfileBlogViewHolder extends RecyclerView.ViewHolder {

        private ImageView fotoPemain;
        private TextView namaPemain;
        private Dialog mDialog;
        private TextView popUpNamaPemin, popUpUmurPemain;
        private CircleImageView popUpImg;


        public ProfileBlogViewHolder(View itemView) {
            super(itemView);
            fotoPemain = itemView.findViewById(R.id.foto_pemain_tim);
            namaPemain = itemView.findViewById(R.id.nama_pemain_tim);

            mDialog = new Dialog(context);
            mDialog.setContentView(R.layout.popup_detail_pemain);
        }

        public void showDataPopUpProfile(String popupName) {
            Toast.makeText(context, ""+popupName, Toast.LENGTH_SHORT).show();
            popUpNamaPemin = mDialog.findViewById(R.id.popup_namapemain);
            popUpUmurPemain = mDialog.findViewById(R.id.popup_user_umur);
            popUpImg = mDialog.findViewById(R.id.popup_img_pemain);

            firebaseFirestore.collection("Pemain").document(popupName).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {

                        String nama = documentSnapshot.get("nama_pemain").toString();
                        String umur = documentSnapshot.get("umur_pemain").toString();
                        String img = documentSnapshot.get("foto_pemain").toString();

                        popUpNamaPemin.setText(nama);
                        popUpUmurPemain.setText(umur);

                        Glide.with(context).load(img).into(popUpImg);

                    }
                }
            });
        }
    }
}