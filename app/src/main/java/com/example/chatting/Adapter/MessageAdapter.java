package com.example.chatting.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT= 1;

    FirebaseUser firebaseUser;

    private Context mContext;
    private List<Chat> mChat;
    private String ImageURL;

    public MessageAdapter (Context mContext, List<Chat> mChat, String ImageURL){
        this.mContext = mContext;
        this.mChat = mChat;
        this.ImageURL = ImageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat =mChat.get(position);
        holder.ShowMessage.setText(chat.getMESSAGE());
        if (ImageURL.equals("default")){
            holder.ProfilePhoto.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(ImageURL).into(holder.ProfilePhoto);
        }

        if (position == mChat.size()-1){
            if (chat.isISSEEN()){
                holder.TextSeen.setText("Seen");
            }else {
                holder.TextSeen.setText("Delivered");
            }
        }else {
            holder.TextSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ShowMessage;
        public ImageView ProfilePhoto;
        public TextView TextSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ShowMessage = itemView.findViewById(R.id.ShowMessage);
            ProfilePhoto = itemView.findViewById(R.id.ProfilePhoto);
            TextSeen = itemView.findViewById(R.id.TextSeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSENDER().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
