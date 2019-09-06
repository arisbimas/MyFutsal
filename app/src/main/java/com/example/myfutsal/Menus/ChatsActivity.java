package com.example.myfutsal.Menus;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfutsal.Adapters.ChatsUsersAdapter;
import com.example.myfutsal.Model.Chatlist;
import com.example.myfutsal.Model.Lawan;
import com.example.myfutsal.Notifications.Token;
import com.example.myfutsal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ChatsUsersAdapter chatsUsersAdapter;
    private TextView noMsg;
    private ImageView noMsgImg;

    private List<Lawan> mUsers;
    private List<Chatlist> userList;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        toolbar = findViewById(R.id.chatstoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rv_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userList = new ArrayList<>();

        Query query = firebaseFirestore.collection("Chatlist/" + firebaseUser.getUid() + "/" +firebaseUser.getUid());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

//                userList.clear();
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Chatlist chatlist = doc.getDocument().toObject(Chatlist.class);
                        userList.add(chatlist);

                    }


                }

                if (userList.isEmpty()){
                    Toast.makeText(ChatsActivity.this, "Empty Chat", Toast.LENGTH_SHORT).show();
                }

                chatList();

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token){
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Tokens").document(firebaseUser.getUid());

        Token token1 = new Token(token);
        documentReference.set(token1);
    }

    private void chatList() {

        mUsers = new ArrayList<>();

        Query query1 = firebaseFirestore.collection("Tim");
        query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                mUsers.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {

                    Lawan user = doc.toObject(Lawan.class);
                    for (Chatlist chatlist : userList){
                        try {
                            if (user.getTim_id().equals(chatlist.getId())){
                                mUsers.add(user);
                            }
                        } catch (Exception e1){
                            e1.printStackTrace();
                        }

                    }
                }
                chatsUsersAdapter = new ChatsUsersAdapter(mUsers, true);
                recyclerView.setAdapter(chatsUsersAdapter);

            }
        });

    }
}
