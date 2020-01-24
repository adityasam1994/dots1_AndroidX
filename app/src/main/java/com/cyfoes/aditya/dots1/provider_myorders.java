package com.cyfoes.aditya.dots1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class provider_myorders extends AppCompatActivity {

    LinearLayout orderlayout;
    DatabaseReference dbrorder = FirebaseDatabase.getInstance().getReference("Orders");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseAuth fauth=FirebaseAuth.getInstance();
    ImageView btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_myorders);

        orderlayout = (LinearLayout)findViewById(R.id.myorders);
        btnback = (ImageView)findViewById(R.id.btnback);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setorders();
    }

    private void setorders() {
        dbrorder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot dusers: dataSnapshot.getChildren()){
                    for(final DataSnapshot dcode: dusers.getChildren()){
                        if(dcode.hasChild(fauth.getCurrentUser().getUid())){
                            if(dcode.child(fauth.getCurrentUser().getUid()).hasChild("status")){
                                LayoutInflater inflater = LayoutInflater.from(provider_myorders.this);
                                LinearLayout lay = (LinearLayout)inflater.inflate(R.layout.provider_accepted_items, orderlayout,false);

                                String st = dcode.child(fauth.getCurrentUser().getUid()).child("status").getValue().toString();
                                String tst = "PENDING";
                                if(st.equals("accepted")){
                                    tst = "ACCEPTED";
                                }else if(st.equals("cancelled")){
                                    tst = "CANCELLED";
                                }else if(st.equals("rejected")){
                                    tst = "REJECTED";
                                }else if(st.equals("completed")){
                                    tst = "COMPLETED";
                                }else if(st.equals("order_in_progress")){
                                    tst = "IN PROGRESS";
                                }

                                TextView txtstatus = lay.findViewById(R.id.orderstatus);
                                TextView txtcode = lay.findViewById(R.id.ordercode);

                                txtstatus.setText(tst);

                                txtcode.setText(dcode.getKey());

                                orderlayout.addView(lay);

                                final String finalTst = tst;
                                lay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(finalTst.equals("ACCEPTED")){
                                            Intent intent = new Intent(provider_myorders.this, provider_order_accepted.class);
                                            intent.putExtra("pid", dusers.getKey().toString());
                                            intent.putExtra("oid",dcode.getKey().toString());
                                            startActivity(intent);
                                        }if(finalTst.equals("IN PROGRESS")){
                                            Intent intent = new Intent(provider_myorders.this, timer.class);
                                            intent.putExtra("pid", dusers.getKey().toString());
                                            intent.putExtra("oid",dcode.getKey().toString());
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
