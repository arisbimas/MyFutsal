package com.example.myfutsal.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Model.Lawan;
import com.example.myfutsal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import static android.support.constraint.Constraints.TAG;


public class CariLawanAdapter extends RecyclerView.Adapter<CariLawanAdapter.ViewHolder> {

    public List<Lawan> lawan_list;

    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public CariLawanAdapter(List<Lawan> lawan_list){

        this.lawan_list = lawan_list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_carilawan, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String blogPostId = lawan_list.get(position).LawanId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String namatim = lawan_list.get(position).getNama_tim();
        holder.setNamaTim(namatim);

        String fototim = lawan_list.get(position).getFoto_tim();
        holder.setFotoTim(fototim);



    }


    @Override
    public int getItemCount() {
        return lawan_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView namaTim;
        private ImageView fotoTim;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setNamaTim(String descText){

            namaTim = mView.findViewById(R.id.tvNamaTim);
            namaTim.setText(descText);

        }

        public void setFotoTim(String fototim) {

            fotoTim = mView.findViewById(R.id.ivFotoTim);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.post_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(fototim).into(fotoTim);

        }

//        public void setBlogImage(String downloadUri, String thumbUri){
//
//            blogImageView = mView.findViewById(R.id.blog_image);
//
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.drawable.image_placeholder);
//
//            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(thumbUri).thumbnail(
//                    Glide.with(context).load(thumbUri)
//            ).into(blogImageView);
//
//        }
    }

}