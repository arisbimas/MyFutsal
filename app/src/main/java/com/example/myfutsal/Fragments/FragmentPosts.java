package com.example.myfutsal.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfutsal.Adapters.MyPostAdapter;
import com.example.myfutsal.Adapters.PemainAdapter;
import com.example.myfutsal.Model.Blog;
import com.example.myfutsal.Model.PemainId;
import com.example.myfutsal.Model.Pemains;
import com.example.myfutsal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class FragmentPosts extends Fragment {

    private RecyclerView myPostRv;
    private List<Blog> blog_list;
    private MyPostAdapter adapter;
    private Button tbhPost;
    private TextView tidakAdaPost;
    private Context context;
    private String current_team_id;


    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public FragmentPosts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        context = getContext();
        storageReference    = FirebaseStorage.getInstance().getReference();
        firebaseFirestore   = FirebaseFirestore.getInstance();
        firebaseAuth        = FirebaseAuth.getInstance();
        tidakAdaPost = view.findViewById(R.id.tdk_ada_post);
        tbhPost = view.findViewById(R.id.btn_tbhpost);

        current_team_id = firebaseAuth.getCurrentUser().getUid();

        blog_list = new ArrayList<>();
        myPostRv = view.findViewById(R.id.rv_myposts);
        adapter = new MyPostAdapter(context, blog_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        myPostRv.setLayoutManager(new GridLayoutManager(context, 3));
        myPostRv.setAdapter(adapter);

        Query query = firebaseFirestore.collection("Posts")
                .whereEqualTo("tim_id", current_team_id)
                .orderBy("waktu", Query.Direction.ASCENDING);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                if (!documentSnapshots.isEmpty()) {
                    tidakAdaPost.setVisibility(View.GONE);
                    tbhPost.setVisibility(View.GONE);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogpostid = doc.getDocument().getId();
                            Blog blog = doc.getDocument().toObject(Blog.class).withId(blogpostid);
                            blog_list.add(blog);
                            adapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    myPostRv.setVisibility(View.GONE);
                }

            }

        });

        // Inflate the layout for this fragment
        return view;

    }

}
