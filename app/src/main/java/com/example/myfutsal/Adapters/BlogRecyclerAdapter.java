package com.example.myfutsal.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Activities.CommentActivity;
import com.example.myfutsal.Activities.MessageActivity;
import com.example.myfutsal.Activities.ProfileTimActivity;
import com.example.myfutsal.Model.Blog;
import com.example.myfutsal.R;
import com.example.myfutsal.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static android.support.constraint.Constraints.TAG;


public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<Blog> blog_list;

    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<Blog> blog_list){

        this.blog_list = blog_list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String blogPostId = blog_list.get(position).BlogPostId;
        final String blogUserId = blog_list.get(position).getTim_id();
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String desc_data = blog_list.get(position).getKeterangan();
        holder.setDescText(desc_data);

        String image_url = blog_list.get(position).getFoto_post();

        holder.setBlogImage(image_url);

        String user_id = blog_list.get(position).getTim_id();

//        if (user_id.equals(currentUserId)){
//            holder.deleteBtn.setEnabled(true);
//            holder.deleteBtn.setVisibility(View.VISIBLE);
//        }

        //User Data will be retrieved here...
        firebaseFirestore.collection("Tim").document(user_id)
                .addSnapshotListener((Activity) context,new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        if (documentSnapshot.exists()) {

                            String userImageUrl = documentSnapshot.get("logo").toString();
                            String userNameData = documentSnapshot.get("nama_tim").toString();
                            if (userNameData.isEmpty()) {
                                userNameData = "No name";
                            }
                            holder.blogUserName.setText(userNameData);
                            RequestOptions placeholderOption = new RequestOptions();
                            placeholderOption.placeholder(R.drawable.profile_placeholder);

                            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(userImageUrl).into(holder.blogUserImage);

                            //KLIK LOGO
                            String finalUserNameData = userNameData;
                            holder.blogUserImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intentProfileTime = new Intent(context, ProfileTimActivity.class);
                                    intentProfileTime.putExtra("tim_id", user_id);
                                    intentProfileTime.putExtra("nama_tim", finalUserNameData);
                                    context.startActivity(intentProfileTime);
                                }
                            });

                            //KLIK TOMBOL KOMEN
                            String finalUserNameData2 = userNameData;
                            holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(context, CommentActivity.class);
                                    intent.putExtra("blog_id",blogPostId);
                                    intent.putExtra("tim_id",blogUserId);
                                    intent.putExtra("logo",userImageUrl);
                                    intent.putExtra("keterangan",desc_data);
                                    intent.putExtra("nama_tim", finalUserNameData2);

                                    context.startActivity(intent);

                                }
                            });

                            //KLIK FOTO POST
                            holder.blogImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, CommentActivity.class);
                                    intent.putExtra("blog_id",blogPostId);
                                    intent.putExtra("tim_id",blogUserId);
                                    intent.putExtra("logo",userImageUrl);
                                    intent.putExtra("keterangan",desc_data);
                                    intent.putExtra("nama_tim", finalUserNameData2);

                                    context.startActivity(intent);
                                }
                            });

                        } else {
                            Log.w("USER_DETAIL", "Empty DOC");
                        }
                    }
                });




        long millisecond = blog_list.get(position).getWaktu().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();

        String timeAgo = TimeAgo.getTimeAgo(millisecond);
//        Toast.makeText(context, ""+timeAgo, Toast.LENGTH_SHORT).show();
        holder.setTime(timeAgo);

        //Get Comments Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updateCommentsCount(count);

                } else {

                    holder.updateCommentsCount(0);

                }

            }
        });


        //POPUP
        holder.popUpHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu =  new PopupMenu(context, holder.popUpHome);

                popupMenu.getMenuInflater().inflate(R.menu.menu_chat, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.to_chat:

                                Intent intent = new Intent(context, MessageActivity.class);
                                intent.putExtra("tim_id", blog_list.get(position).getTim_id());
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
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private ImageView blogImageView;
        private CircularImageView popUpUserImage;
        private TextView blogDate, blogAddress;

        private TextView blogUserName, popUpUserName, popUpUserEmail, popUpUserPhone;
        private CircularImageView blogUserImage;

        private ImageView blogLikeBtn, popUpHome;
        private TextView blogLikeCount;
        private LinearLayout hideItem;

        private ImageView blogCommentBtn;

        private Button deleteBtn, viewProfileBtn;

        private Dialog mDialog;
        private SpotsDialog spotsDialog;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            blogCommentBtn = mView.findViewById(R.id.btn_comment);
//            deleteBtn = mView.findViewById(R.id.btn_delete);
            popUpHome = mView.findViewById(R.id.popup_home);
            blogUserName = mView.findViewById(R.id.blog_username);
            blogUserImage = mView.findViewById(R.id.blog_user_image);



        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.blog_desc);
            String upperString = descText.substring(0,1).toUpperCase() + descText.substring(1);
            descView.setText(upperString);

        }

        public void setBlogImage(String downloadUri){

            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).into(blogImageView);

        }

        public void setTime(String date) {

            blogDate = mView.findViewById(R.id.time_post);
            blogDate.setText(date);

        }


        public void updateCommentsCount(int count){

            blogLikeCount = mView.findViewById(R.id.comment_count);
            blogLikeCount.setText(count +"");

        }

//        public void showDataPopUpProfile(String popupName){
//            popUpUserName = mDialog.findViewById(R.id.popup_username);
//            popUpUserEmail = mDialog.findViewById(R.id.popup_user_email);
//            popUpUserPhone = mDialog.findViewById(R.id.popup_user_phone);
//            popUpUserImage = mDialog.findViewById(R.id.popup_user_image);
//            viewProfileBtn = mDialog.findViewById(R.id.btnviewprofile);
//
//            firebaseFirestore.collection("Tim").document(popupName).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                    if (documentSnapshot.exists()){
//                        String popupusername = documentSnapshot.get("name").toString();
//                        String popupuseremail = documentSnapshot.get("email").toString();
//                        String popupuserphone = documentSnapshot.get("phone").toString();
//                        String popupuserimg = documentSnapshot.get("image").toString();
//
//                        popUpUserName.setText(popupusername);
//                        popUpUserEmail.setText(popupuseremail);
//                        popUpUserPhone.setText(popupuserphone);
//                        Glide.with(context).load(popupuserimg).into(popUpUserImage);
//
//                        viewProfileBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent seeProfile = new Intent(context, DetailProfileActivity.class);
//                                seeProfile.putExtra("user_id", popupName);
//                                context.startActivity(seeProfile);
//                            }
//                        });
//                    }
//                }
//            });
//        }
    }

}