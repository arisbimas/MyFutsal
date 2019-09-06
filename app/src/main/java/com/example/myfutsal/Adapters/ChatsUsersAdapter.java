package com.example.myfutsal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfutsal.Activities.MessageActivity;
import com.example.myfutsal.Model.Chats;
import com.example.myfutsal.Model.Lawan;
import com.example.myfutsal.R;
import com.example.myfutsal.TimeAgo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsUsersAdapter extends RecyclerView.Adapter<ChatsUsersAdapter.ViewHolder> {

    public List<Lawan> userList;
    public Context context;
    public boolean isChat;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String theLastMsg;
    long lastTimeChat;

    public ChatsUsersAdapter(List<Lawan> userList, boolean isChat) {

        this.userList = userList;
        this.isChat = isChat;

    }

    @Override
    public ChatsUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chats, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        return new ChatsUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatsUsersAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final Lawan user = userList.get(position);

        String image = user.getLogo();
        String name = user.getNama_tim();

//        holder.setUserData(name, image);
        holder.setChatUserImage(image);
        holder.setChatUserName(name);

        //start chat
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentSC = new Intent(context, MessageActivity.class);
                intentSC.putExtra("tim_id", user.getTim_id());
                context.startActivity(intentSC);
            }
        });

        if (isChat) {
            holder.lastMessage(user.getTim_id(), holder.lastMsg, userList.get(position).getTim_id());
//            Toast.makeText(context, ""+userList.get(position).getUser_id(), Toast.LENGTH_SHORT).show();
        } else {
            holder.lastMsg.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {

        if (userList != null) {

            return userList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private CircleImageView chatUserImage, img_on, img_off;
        private TextView chatUserName, chatTimestamp, lastMsg;
        private RelativeLayout relativeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            relativeLayout = mView.findViewById(R.id.rl_userchatrow);
            chatUserImage = mView.findViewById(R.id.chat_userimage);
            chatUserName = mView.findViewById(R.id.chat_username);
            chatTimestamp = mView.findViewById(R.id.chat_timestamp);
            img_on = mView.findViewById(R.id.img_on);
            img_off = mView.findViewById(R.id.img_off);
            lastMsg = mView.findViewById(R.id.last_msg);

        }


        public void setChatUserImage(String image) {

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(chatUserImage);

        }

        public void setChatUserName(String name) {

            chatUserName.setText(name);
        }

        //CHeck last msg
        public void lastMessage(String userid, TextView lastMsg, String pss) {

            theLastMsg = "default";
            lastTimeChat = 0;


            Query query = firebaseFirestore.collection("Chats/" + firebaseUser.getUid() + "/" + pss)
                    .orderBy("timestamp");

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {

                        Chats chats = doc.toObject(Chats.class);
                        if (chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(userid)
                                || chats.getReceiver().equals(userid) && chats.getSender().equals(firebaseUser.getUid())) {
                            theLastMsg = chats.getMessage();
                            try {
                                lastTimeChat = chats.getTimestamp().getTime();
                            } catch (Exception e1) {
                                e1.printStackTrace();

                            }
                        }

                    }

                    switch (theLastMsg) {
                        case "default":
                            lastMsg.setText("");
                            chatTimestamp.setText("");
                            break;
                        default:
                            lastMsg.setText(theLastMsg);
                            try {
                                chatTimestamp.setText(TimeAgo.getTimeAgo(lastTimeChat).toString());
                            } catch (Exception e1) {
                                chatTimestamp.setText("");
                            }
                            break;
                    }

                    theLastMsg = "default";
                    lastTimeChat = 0;

                }
            });

        }

    }
}