package com.example.myfutsal.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myfutsal.APIService;
import com.example.myfutsal.Adapters.MsgAdapter;
import com.example.myfutsal.Model.Chats;
import com.example.myfutsal.Model.Lawan;
import com.example.myfutsal.Notifications.Client;
import com.example.myfutsal.Notifications.Data;
import com.example.myfutsal.Notifications.MyResponse;
import com.example.myfutsal.Notifications.Sender;
import com.example.myfutsal.Notifications.Token;
import com.example.myfutsal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profileImageMsg;
    TextView username_msg, txt_useronline;
    ImageButton btnSendMsg;
    EditText txt_send;

    List<Chats> mChats;

    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    Intent intent;

    APIService apiService;

    boolean notify = false;

    private String TAG = "MSG_ACTY";
    private final List<Chats> chatsList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MsgAdapter msgAdapter;

    private String user_id_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar_msg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(ChatActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        intent = getIntent();
        user_id_msg = intent.getStringExtra("tim_id");

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String cUser = firebaseUser.getUid();

        msgAdapter = new MsgAdapter(chatsList);

        recyclerView = findViewById(R.id.rv_msg);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(msgAdapter);

        loadMsg(cUser, user_id_msg);

        profileImageMsg = findViewById(R.id.image_profile_msg);
        username_msg = findViewById(R.id.username_msg);
        btnSendMsg = findViewById(R.id.send_msg);
        txt_send = findViewById(R.id.text_send);
        txt_useronline = findViewById(R.id.user_online);

        firebaseFirestore.collection("Tim").document(user_id_msg).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String name = documentSnapshot.getString("nama_tim");
                String image = documentSnapshot.getString("logo");


                //SET IMG USER CHAT
                Glide.with(getApplicationContext()).load(image).into(profileImageMsg);
                //SET USERNAME
                username_msg.setText(name);
                //SET STATUS
//                if (userstts.equals("online")){
//                    txt_useronline.setVisibility(View.VISIBLE);
//                } else {
//                    txt_useronline.setVisibility(View.GONE);
//                }

//                readMessage(firebaseUser.getUid(), user_id_msg, image);
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notify = true;
                String msg = txt_send.getText().toString();

                if (!msg.equals("")){
                    sendMessage(firebaseUser.getUid(), user_id_msg, msg);
                } else {

                    Toast toast = Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                txt_send.setText("");
            }
        });

    }

    private void loadMsg(String cUser, String user_id_msg) {

        String chatRef = "Chats/" + cUser + "/" + user_id_msg;

        Query query = firebaseFirestore.collection(chatRef).orderBy("timestamp", Query.Direction.ASCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        Chats chats = doc.getDocument().toObject(Chats.class);
                        chatsList.add(chats);
                        msgAdapter.notifyDataSetChanged();

                    }
                }

                msgAdapter = new MsgAdapter(chatsList);
                recyclerView.setAdapter(msgAdapter);
                msgAdapter.notifyDataSetChanged();
            }
        });

    }


    private void sendMessage(String sender, String receiver, String message) {

        //ADD CHATLIST JIKA TIDAK ADA
        HashMap<String, Object> hs = new HashMap<>();
        hs.put("id", receiver);
        hs.put("timestamp", FieldValue.serverTimestamp());

        HashMap<String, Object> hs1 = new HashMap<>();
        hs1.put("id", firebaseUser.getUid());
        hs1.put("timestamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Chatlist/" + firebaseUser.getUid() +"/"+firebaseUser.getUid())
                .document(receiver).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (!documentSnapshot.exists()){

                    firebaseFirestore.collection("Chatlist/" + firebaseUser.getUid() +"/"+firebaseUser.getUid())
                            .document(receiver).set(hs);

                    firebaseFirestore.collection("Chatlist/" + receiver +"/"+receiver)
                            .document(firebaseUser.getUid()).set(hs1);
                }
            }
        });


        // ADD MSG

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("timestamp", FieldValue.serverTimestamp());

        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("sender", sender);
        hashMap1.put("receiver", receiver);
        hashMap1.put("message", message);
        hashMap1.put("timestamp", FieldValue.serverTimestamp());

//        firebaseFirestore.collection("Chats").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentReference> task) {
//                if (task.isSuccessful()){
//
//                    Toast.makeText(ChatActivity.this, "Message Sent" + FieldValue.serverTimestamp(), Toast.LENGTH_SHORT).show();
//
//                } else {
//
//                    Toast.makeText(ChatActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });

        String current_user_ref = "Chats/" + firebaseUser.getUid() + "/" + receiver;
        String chat_user_ref = "Chats/" + receiver + "/" + firebaseUser.getUid();

        firebaseFirestore.collection(current_user_ref).add(hashMap);

        firebaseFirestore.collection(chat_user_ref).add(hashMap1);



        final String msg = message;
        firebaseFirestore.collection("Tim").document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                Lawan user = documentSnapshot.toObject(Lawan.class);
                sendNotification(receiver, user.getNama_tim(), msg);
                notify = false;
            }
        });

    }

    private void sendNotification(String receiver, final String username, String message) {

        user_id_msg = intent.getStringExtra("tim_id");

        CollectionReference tokens = FirebaseFirestore.getInstance().collection("Tokens");
        Query query = tokens;

        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                if (!documentSnapshots.isEmpty()) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            Token token = doc.getDocument().toObject(Token.class);
                            Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message", user_id_msg);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200){
                                                if (response.body().success != 1){
//                                                        Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                                    Log.d("NOTIF:", "Failed Notif");
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                        }
                                    });

                        }

                    }
                }

            }
        });

    }

//    private void readMessage(String myid, String userid, String imgurl){
//        mChats = new ArrayList<>();
//
//
////        firebaseFirestore.collection("Chats").addSnapshotListener(new EventListener<QuerySnapshot>() {
////            @Override
////            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
////
////                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
////                    Chats chat = documentChange.getDocument().toObject(Chats.class);
////
////                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid)
////                            || chat.getReceiver().equals(userid) && chat.getSender().equals(myid) ){
////
////                        mChats.add(chat);
////                    }
////
////                }
////                messageAdapter = new MessageAdapter(ChatActivity.this, mChats, imgurl);
////
////                recyclerView.setAdapter(messageAdapter);
////                messageAdapter.notifyDataSetChanged();
////
////            }
////        });
//
//        firebaseFirestore.collection("Chats")
//                .orderBy("timestamp", Query.Direction.ASCENDING)
//                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//
//                        if (!documentSnapshots.isEmpty()) {
//
//                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//
//                                if (doc.getType() == DocumentChange.Type.ADDED) {
//
//                                    Chats chat = doc.getDocument().toObject(Chats.class);
//                                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid)
//                                            || chat.getReceiver().equals(userid) && chat.getSender().equals(myid) ){
//
//                                        mChats.add(chat);
//                                    }
//                                    messageAdapter = new MessageAdapter(mChats, imgurl);
//                                    messageAdapter.notifyItemChanged(0);
//                                    messageAdapter.notifyDataSetChanged();
//                                    recyclerView.setAdapter(messageAdapter);
//                                }
//                            }
//
//
//                        }
//
//                    }
//                });
//    }

//    private void status (String status){
//
//        firebaseFirestore = FirebaseFirestore.getInstance();
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("status", status);
//
//        firebaseFirestore.collection("Tim").document(firebaseUser.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
////                Toast.makeText(ChatActivity.this, "online", Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "Current USER ONLINE");
//
//            }
//        });
//
//    }

}