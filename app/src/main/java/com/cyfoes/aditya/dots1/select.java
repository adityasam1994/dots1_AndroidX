package com.cyfoes.aditya.dots1;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class select extends AppCompatActivity {

    Button btnprovider, btncustomer;
    FirebaseAuth fauth=FirebaseAuth.getInstance();
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        btncustomer = (Button)findViewById(R.id.btncustomer);
        btnprovider = (Button)findViewById(R.id.btnprovider);

        btncustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbruser.child(fauth.getCurrentUser().getUid()).child("status").setValue("customer");
                startActivity(new Intent(select.this, newdrawer.class));
            }
        });

        btnprovider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbruser.child(fauth.getCurrentUser().getUid()).child("status").setValue("provider");
                startActivity(new Intent(select.this, provider_detail.class));
            }
        });
    }
}
