package com.cyfoes.aditya.dots1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class myorders extends AppCompatActivity {

    LinearLayout orderlayout;
    DatabaseReference dbrorder = FirebaseDatabase.getInstance().getReference("Orders");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseAuth fauth=FirebaseAuth.getInstance();
    ImageView btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorders);

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

    private void setorders(){
        dbrorder.child(fauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot dcode: dataSnapshot.getChildren()){
                    final LayoutInflater inflater = LayoutInflater.from(myorders.this);
                    LinearLayout lay = (LinearLayout)inflater.inflate(R.layout.myorder_items, orderlayout,false);

                    TextView srvname = lay.findViewById(R.id.servicename);
                    TextView srvid = lay.findViewById(R.id.serviceid);
                    TextView srvstatus = lay.findViewById(R.id.servicestatus);

                    srvid.setText(dcode.getKey().toString());

                    if(dcode.hasChild("service")){
                        srvname.setText(dcode.child("service").getValue().toString());
                    }
                    String st = "NOT FOUND";
                    String pid = "";
                    for(DataSnapshot delements: dcode.getChildren()){

                        if(delements.getKey().toString().length() > 20){
                            String st1 = delements.child("status").getValue().toString();
                            if(st1.equals("accepted")){
                                st = "ACCEPTED";
                                pid = delements.getKey().toString();
                                break;
                            }else if(st1.equals("pending")){
                                st = "PENDING";
                                pid = delements.getKey().toString();
                                break;
                            }else if(st1.equals("cancelled")) {
                                st = "CANCELLED";
                                break;
                            }else if(st1.equals("completed")){
                                st = "COMPLETED";
                                break;
                            }else if(st1.equals("rejected")){
                                st = "REJECTED";
                            }
                        }
                    }

                    srvstatus.setText(st);

                    orderlayout.addView(lay);

                    final String finalSt = st;
                    final String finalPid = pid;
                    lay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(finalSt.equals("ACCEPTED")){
                                Intent intent = new Intent(myorders.this, order_accepted.class);
                                intent.putExtra("pid", finalPid);
                                intent.putExtra("oid", dcode.getKey().toString());
                                intent.putExtra("lastpage", "myorders");
                                startActivity(intent);
                            }else if(finalSt.equals("COMPLETED")){
                                Toast.makeText(myorders.this, "This order has been completed", Toast.LENGTH_SHORT).show();
                            }else if(finalSt.equals("NOT FOUND")){
                                Toast.makeText(myorders.this, "No provider for found", Toast.LENGTH_SHORT).show();
                            }else if(finalSt.equals("CANCELLED")){
                                Toast.makeText(myorders.this, "This order was cancelled by you", Toast.LENGTH_SHORT).show();
                            }else if(finalSt.equals("PENDING")){
                                Intent intent = new Intent(myorders.this, pending_order.class);
                                intent.putExtra("pid", finalPid);
                                intent.putExtra("oid", dcode.getKey().toString());
                                startActivity(intent);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
