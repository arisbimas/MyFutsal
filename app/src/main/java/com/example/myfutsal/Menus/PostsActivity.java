package com.example.myfutsal.Menus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.myfutsal.Activities.NewPostActivity;
import com.example.myfutsal.Adapters.BlogRecyclerAdapter;
import com.example.myfutsal.Model.Blog;
import com.example.myfutsal.Model.Lawan;
import com.example.myfutsal.R;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PostsActivity extends AppCompatActivity {


    private RecyclerView blog_list_view;
        private List<Blog> blog_list;
    private List<Lawan> user_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private BlogRecyclerAdapter blogRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    private ProgressDialog dialog;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        Toolbar toolbar = findViewById(R.id.poststoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        blog_list = new ArrayList<>();
        user_list = new ArrayList<>();

        blog_list_view = findViewById(R.id.rv_posts);

        firebaseAuth = FirebaseAuth.getInstance();

        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(PostsActivity.this));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Tunggu...");
        dialog.show();

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();
            user_id = firebaseAuth.getCurrentUser().getUid();

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {

//                        loadMorePost();

                    }

                }
            });


            firstQ();


        }
    }

        private void firstQ () {
            CollectionReference collectionReference = firebaseFirestore.collection("Posts");

            Query firstQuery = collectionReference;
            firstQuery.addSnapshotListener(PostsActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (e != null) {
                        Log.w("BLOG", "listen:error", e);
                        return;
                    }

                    if (!documentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            blog_list.clear();

                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {


                                String blogPostId = doc.getDocument().getId();
                                Blog blogPost = doc.getDocument().toObject(Blog.class).withId(blogPostId);

                                if (isFirstPageFirstLoad) {

                                    blog_list.add(blogPost);
                                    Collections.sort(blog_list, new Comparator<Blog>() {
                                        @Override
                                        public int compare(Blog o1, Blog o2) {
                                            return o2.getWaktu().compareTo(o1.getWaktu());
                                        }
                                    });
                                    blogRecyclerAdapter.notifyItemInserted(blog_list.size());
                                    blogRecyclerAdapter.notifyDataSetChanged();


                                } else {

                                    blog_list.add(0, blogPost);
                                    Collections.sort(blog_list, new Comparator<Blog>() {
                                        @Override
                                        public int compare(Blog o1, Blog o2) {
                                            return o2.getWaktu().compareTo(o1.getWaktu());
                                        }
                                    });

                                }
                                dialog.dismiss();
                            }
                        }

                        isFirstPageFirstLoad = false;

                    }

                }

            });
        }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_posts, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.add_post) {

                Intent intent = new Intent(this, NewPostActivity.class);
                startActivity(intent);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }
