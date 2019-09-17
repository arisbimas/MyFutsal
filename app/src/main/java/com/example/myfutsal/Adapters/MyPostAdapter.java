package com.example.myfutsal.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.example.myfutsal.Activities.EditPostActivity;
import com.example.myfutsal.Activities.SetupActivity;
import com.example.myfutsal.Activities.TambahPemainActivity;
import com.example.myfutsal.Menus.MyTeamActivity;
import com.example.myfutsal.Model.Blog;
import com.example.myfutsal.Model.Pemains;
import com.example.myfutsal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blog_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public MyPostAdapter(Context context, List<Blog> blog_list) {
        this.context = context;
        this.blog_list = blog_list;
    }


    @NonNull
    @Override
    public MyPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mypost, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyPostAdapter.ViewHolder holder, final int position) {

        final Blog blog = blog_list.get(holder.getAdapterPosition());
        String img = blog.getFoto_post();
        String keterangan = blog.getKeterangan();

        final String blogPostId = blog_list.get(position).BlogPostId;


//        holder.keterangan.setText(keterangan);

        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.profile_placeholder);
        Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(img).into(holder.fotoPost);

        holder.fotoPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                alertbox.setItems(new String[]{"Edit Post", "Hapus Post"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            Intent intent = new Intent(context, EditPostActivity.class);
                            intent.putExtra("post_id", blogPostId);
                            context.startActivity(intent);

                        } else if (which == 1) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Hapus Pemain?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            holder.deletePost(blogPostId, position);

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
                    }
                });
                alertbox.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView fotoPost;
        private TextView keterangan;
        private Dialog mDialog;
        private TextView popUpNamaPemin, popUpUmurPemain;
        private CircleImageView popUpImg;


        public ViewHolder(View itemView) {
            super(itemView);
            fotoPost = itemView.findViewById(R.id.mini_mypost);
//            keterangan = itemView.findViewById(R.id.nama_pemain_tim);

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

        public void deletePost(String id, final int index) {

            final ProgressDialog dialog = new ProgressDialog(context);
            dialog.show();
            firebaseFirestore.collection("Posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Berhasil Hapus Post!", Toast.LENGTH_SHORT).show();
                            blog_list.remove(index);
                            notifyItemRemoved(index);
                            dialog.dismiss();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error deleting post.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        }

    }
}