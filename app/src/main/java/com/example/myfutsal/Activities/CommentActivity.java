package com.example.myfutsal.Activities;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Adapters.CommentsRecyclerAdapter;
import com.example.myfutsal.Model.Comments;
import com.example.myfutsal.R;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.annotations.Until;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private EditText comment_field;
    private ImageView comment_post_btn;
    private TextView blogKet, blogUser, timeAgo;
    private CircleImageView blogLogo;

    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        comment_list = findViewById(R.id.rv_comment);
        comment_field = findViewById(R.id.isi_komen);
        comment_post_btn = findViewById(R.id.kirim_komen);
        blogLogo = findViewById(R.id.user_foto);
        blogUser = findViewById(R.id.username);
        blogKet = findViewById(R.id.user_ket);

        mToolbar = findViewById(R.id.commenttoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Komentar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUserId = firebaseAuth.getCurrentUser().getUid();

        //GET EXTRA FROM BLOGRECVIEW
        final String blogPostId = getIntent().getStringExtra("blog_id");
        final String blogUserId = getIntent().getStringExtra("tim_id");
        final String logo = getIntent().getStringExtra("logo");
        final String desc = getIntent().getStringExtra("keterangan");
        final String nama_tim = getIntent().getStringExtra("nama_tim");

        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);


        //SHOW COMMENTS
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(CommentActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    Comments comments = doc.getDocument().toObject(Comments.class);
                                    commentsList.add(comments);
                                    commentsRecyclerAdapter.notifyItemChanged(0);
                                    commentsRecyclerAdapter.notifyDataSetChanged();

                                }
                            }

                        }

                    }
                });

        //Tekan Btn Comment
        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message = comment_field.getText().toString();

                if (TextUtils.isEmpty(comment_message)){
                    Toast.makeText(CommentActivity.this, "Beri Suatu Komentar.", Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog dialog = new ProgressDialog(CommentActivity.this);
                    dialog.setMessage("Loading...");
                    dialog.show();

                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("message", comment_message);
                    commentsMap.put("tim_id", currentUserId);
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if(!task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(CommentActivity.this, "Error Posting Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(CommentActivity.this, "Komentar terkirim", Toast.LENGTH_SHORT).show();
                                comment_field.setText("");
                                dialog.dismiss();

                            }

                        }
                    });
                }

            }
        });


        //SHOW DETAIL POS
        blogUser.setText(nama_tim);
        //set Desc Post
        blogKet.setText(desc);
        //Set Image Post
        RequestOptions placeholderReq = new RequestOptions();
        placeholderReq.placeholder(R.drawable.profile_placeholder);
        Glide.with(getApplicationContext()).applyDefaultRequestOptions(placeholderReq).load(logo).into(blogLogo);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}