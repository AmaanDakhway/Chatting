package com.example.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText Username, EmailAddress, Password, Phone;
    Button Registration;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);

        Toolbar toolbar  = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("REGISTER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Username         = findViewById(R.id.Username);
        EmailAddress     = findViewById(R.id.EmailAddress);
        Password         = findViewById(R.id.Password);
        Phone            = findViewById(R.id.Phone);
        Registration     = findViewById(R.id.Registration);

        firebaseAuth = FirebaseAuth.getInstance();

        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username        = Username.getText().toString();
                String emailaddress    = EmailAddress.getText().toString();
                String password        = Password.getText().toString();
                String phone           = Phone.getText().toString();

                if (TextUtils.isEmpty(username)||TextUtils.isEmpty(emailaddress)||TextUtils.isEmpty(password)||TextUtils.isEmpty(phone)){
                    Toast.makeText(getApplicationContext(),"ALL FIELDS ARE NECESSARY",Toast.LENGTH_SHORT).show();
                }else if (password.length()<6){
                    Toast.makeText(getApplicationContext(),"PASSWORD SHOULD BE GRATER THAN 6 CHARACTERS",Toast.LENGTH_SHORT).show();
                }else {
                    registration(username, emailaddress, password, phone);
                }

            }
        });

    }

    private void registration(final String Username, String EmailAddress, String Password, String Phone){

        firebaseAuth.createUserWithEmailAndPassword(EmailAddress,Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        String userID = firebaseUser.getUid();

                                        databaseReference = FirebaseDatabase.getInstance().getReference("USERS").child(userID);

                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("ID",userID);
                                        hashMap.put("EMAILADDRESS",EmailAddress);
                                        hashMap.put("USERNAME",Username);
                                        hashMap.put("PHONE",Phone);
                                        hashMap.put("IMAGEURL", "default");
                                        hashMap.put("STATUS", "Offline");
                                        hashMap.put("SEARCH", Username.toLowerCase());

                                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    FirebaseAuth.getInstance().signOut();
                                                    Toast.makeText(getApplicationContext(), "You are Registered. Please Verify your email, to continue", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }else {
                                                    Toast.makeText(getApplicationContext(), "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else {
                                        Toast.makeText(getApplicationContext(), "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(getApplicationContext(), "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}