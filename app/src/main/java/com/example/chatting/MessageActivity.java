package com.example.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatting.Adapter.MessageAdapter;
import com.example.chatting.Model.Chat;
import com.example.chatting.Model.Userdata;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    ImageView ProfilePhoto;
    TextView Username;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ImageButton SendButton;
    EditText SendText;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ProfilePhoto = findViewById(R.id.ProfilePhoto);
        Username     = findViewById(R.id.Username);
        SendButton   = findViewById(R.id.SendButton);
        SendText     = findViewById(R.id.SendText);


        intent = getIntent();
        String UserID = intent.getStringExtra("USERID");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Message = SendText.getText().toString();
                if (!Message.equals("")){
                    sendMessage(firebaseUser.getUid(),UserID,Message);
                }else {
                    Toast.makeText(getApplicationContext(),"Please Type A message",Toast.LENGTH_SHORT).show();
                }
                SendText.setText("");
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("USERS").child(UserID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Userdata userdata = snapshot.getValue(Userdata.class);
                Username.setText(userdata.getUSERNAME());
                if (userdata.getIMAGEURL().equals("default")){
                    ProfilePhoto.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getApplicationContext()).load(userdata.getIMAGEURL()).into(ProfilePhoto);
                }
                readMessage(firebaseUser.getUid(), UserID, userdata.getIMAGEURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(UserID);

    }

    private void seenMessage(String userID){
        databaseReference = FirebaseDatabase.getInstance().getReference("CHATS");
        seenEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getRECEIVER().equals(firebaseUser.getUid()) && chat.getSENDER().equals(userID)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("ISSEEN", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String Sender, String Receiver, String Message){
        String UserID = intent.getStringExtra("USERID");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("SENDER", Sender);
        hashMap.put("RECEIVER",Receiver);
        hashMap.put("MESSAGE", Message);
        hashMap.put("ISSEEN", false);

        databaseReference.child("CHATS").push().setValue(hashMap);

        DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("CHATLIST").
                child(firebaseUser.getUid()).child(UserID);

        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatref.child("IDS").setValue(UserID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage(final String myID, final String userID, final String ImageURL){
        mChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("CHATS");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getRECEIVER().equals(myID) && chat.getSENDER().equals(userID) ||
                            chat.getRECEIVER().equals(userID) && chat.getSENDER().equals(myID)){
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this,mChat,ImageURL);
                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void status(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference("USERS").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("STATUS", status);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenEventListener);
        status("Offline");
    }
}