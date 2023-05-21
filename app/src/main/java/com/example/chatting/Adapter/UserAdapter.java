package com.example.chatting.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatting.MessageActivity;
import com.example.chatting.Model.Chat;
import com.example.chatting.Model.Userdata;
import com.example.chatting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Userdata> mUsers;
    private boolean isChat;
    String TheLastMessage;

    public UserAdapter (Context mContext, List<Userdata> mUsers,  boolean isChat){
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.useritem,parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        Userdata userdata = mUsers.get(position);
        holder.Username.setText(userdata.getUSERNAME());
        if (userdata.getIMAGEURL().equals("default")){
            holder.ProfilePhoto.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(userdata.getIMAGEURL()).into(holder.ProfilePhoto);
        }

        if (isChat){
            lastmessage(userdata.getID(),holder.LastMessage);
        }else {
            holder.LastMessage.setVisibility(View.GONE);
        }

        if (isChat){
            if (userdata.getSTATUS().equalsIgnoreCase("Online")){
                holder.Online.setVisibility(View.VISIBLE);
                holder.Offline.setVisibility(View.GONE);
            }else {
                holder.Online.setVisibility(View.GONE);
                holder.Offline.setVisibility(View.VISIBLE);
            }
        }else {
            holder.Online.setVisibility(View.GONE);
            holder.Offline.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("USERID",userdata.getID());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView Username, LastMessage;
        public ImageView ProfilePhoto;
        private ImageView Online, Offline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Username = itemView.findViewById(R.id.Username);
            ProfilePhoto = itemView.findViewById(R.id.ProfilePhoto);
            Online = itemView.findViewById(R.id.Online);
            Offline = itemView.findViewById(R.id.Offline);
            LastMessage = itemView.findViewById(R.id.LastMessage);
        }
    }

    private void lastmessage(String userid, TextView LastMessage){
        TheLastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CHATS");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getRECEIVER().equals(firebaseUser.getUid()) && chat.getSENDER().equals(userid) ||
                            chat.getRECEIVER().equals(userid) && chat.getSENDER().equals(firebaseUser.getUid())) {
                        TheLastMessage = chat.getMESSAGE();
                    }
                }

                switch (TheLastMessage){
                    case "default":
                        LastMessage.setText("No New Message");
                        break;

                    default:
                        LastMessage.setText(TheLastMessage);
                        break;
                }

                TheLastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
