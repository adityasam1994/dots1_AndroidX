package com.cyfoes.aditya.dots1;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class splashscreeen extends AppCompatActivity {

    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseAuth fauth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreeen);

        if(fauth.getCurrentUser() != null) {
            selecthome();
        }else {
            startActivity(new Intent(this, newlogin.class));
        }
    }

    private void selecthome() {
        dbruser.child(fauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("current_status")){
                    String status = dataSnapshot.child("current_status").getValue().toString();
                    if(status.equals("customer")){
                        startActivity(new Intent(splashscreeen.this, newdrawer.class));
                    }else if(status.equals("provider")){
                        startActivity(new Intent(splashscreeen.this, provider_home.class));
                    }
                }else if(dataSnapshot.hasChild("status")){
                    String status = dataSnapshot.child("status").getValue().toString();
                    if(status.equals("customer")){
                        startActivity(new Intent(splashscreeen.this, newdrawer.class));
                    }else if(status.equals("provider")){
                        startActivity(new Intent(splashscreeen.this, provider_home.class));
                    }
                }else {
                    Toast.makeText(splashscreeen.this, "Please select your account type", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(splashscreeen.this, select.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
