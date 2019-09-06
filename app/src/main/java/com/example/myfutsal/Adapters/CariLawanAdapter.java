package com.example.myfutsal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Activities.MessageActivity;
import com.example.myfutsal.Activities.ProfileTimActivity;
import com.example.myfutsal.Model.Lawan;
import com.example.myfutsal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String blogPostId = lawan_list.get(position).LawanId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        final String tim_id = lawan_list.get(position).getTim_id();

        final String namatim = lawan_list.get(position).getNama_tim();
        holder.setNamaTim(namatim);

        String fototim = lawan_list.get(position).getFoto_tim();
        holder.setFotoTim(fototim);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfileTime = new Intent(context, ProfileTimActivity.class);
                intentProfileTime.putExtra("tim_id", tim_id);
                intentProfileTime.putExtra("nama_tim", namatim);
                context.startActivity(intentProfileTime);
            }
        });

        holder.fotoTim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfileTime = new Intent(context, ProfileTimActivity.class);
                intentProfileTime.putExtra("tim_id", tim_id);
                intentProfileTime.putExtra("nama_tim", namatim);
                context.startActivity(intentProfileTime);
            }
        });

        holder.dotChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.cardView);

                popupMenu.getMenuInflater().inflate(R.menu.menu_chat, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.to_chat:

                                Intent intent = new Intent(context, MessageActivity.class);
                                intent.putExtra("tim_id", lawan_list.get(position).getTim_id());
                                context.startActivity(intent);

                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }

        });


    }


    @Override
    public int getItemCount() {
        return lawan_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private CardView cardView;
        private TextView namaTim;
        private ImageView fotoTim, dotChat;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            cardView = mView.findViewById(R.id.cardview);
            dotChat = mView.findViewById(R.id.dot_tochat);
            fotoTim = mView.findViewById(R.id.ivFotoTim);
        }

        public void setNamaTim(String descText){

            namaTim = mView.findViewById(R.id.tvNamaTim);
            namaTim.setText(descText);

        }

        public void setFotoTim(String fototim) {


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