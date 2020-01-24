package com.cyfoes.aditya.dots1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class provider_order_accepted extends AppCompatActivity {

    DatabaseReference dbrorder = FirebaseDatabase.getInstance().getReference("Orders");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    StorageReference sref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydots-554f6.appspot.com/");
    FirebaseAuth fauth=FirebaseAuth.getInstance();
    ImageView btnback, getdirection, imgplay, imgdownload;
    TextView tvname, tvservice, tvcode, tvcdetail, tvctime, tvctype, tvccomment, tvcphone;
    Button btncall, scanqr, btnsubmit;
    EditText etcode;
    String pid, oid;
    SharedPreferences spref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_order_accepted);

        spref = getSharedPreferences("timer",MODE_PRIVATE);

        btnback = (ImageView)findViewById(R.id.btnback);
        btnsubmit = (Button)findViewById(R.id.btnsubmit);
        getdirection = (ImageView)findViewById(R.id.getdirection);
        imgplay = (ImageView)findViewById(R.id.imgplay);
        imgdownload = (ImageView)findViewById(R.id.imgdownload);

        tvname = (TextView)findViewById(R.id.tvname);
        tvservice = (TextView)findViewById(R.id.tvcservice);
        tvcode = (TextView)findViewById(R.id.tvcode);
        tvcdetail = (TextView)findViewById(R.id.tvcdetail);
        tvctime = (TextView)findViewById(R.id.tvctime);
        tvctype = (TextView)findViewById(R.id.tvctype);
        tvccomment = (TextView)findViewById(R.id.tvccomment);
        tvcphone = (TextView)findViewById(R.id.txtphone);

        btncall = (Button)findViewById(R.id.btncall);
        scanqr = (Button)findViewById(R.id.scanqr);

        etcode = (EditText)findViewById(R.id.etcode);

        pid = getIntent().getExtras().getString("pid");
        oid = getIntent().getExtras().getString("oid");

        scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(provider_order_accepted.this);
                integrator.setPrompt("Scan a QRcode");
                integrator.setOrientationLocked(true);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
            }
        });

        setname();
        setdetails();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                etcode.setText(result.getContents());
            }
        }
    }

    private void setdetails() {
        dbrorder.child(pid).child(oid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("time")){
                    tvctime.setText(dataSnapshot.child("time").getValue().toString());
                }
                if(dataSnapshot.hasChild("ecomment")){
                    tvccomment.setText(dataSnapshot.child("ecomment").getValue().toString());
                }
                if(dataSnapshot.hasChild("service")){
                    tvservice.setText(dataSnapshot.child("service").getValue().toString());
                }
                if(dataSnapshot.hasChild("servicetype")){
                    tvctype.setText(dataSnapshot.child("servicetype").getValue().toString());
                }
                tvcode.setText(oid);

                final String lat = dataSnapshot.child("latitude").getValue().toString();
                final String lng = dataSnapshot.child("longitude").getValue().toString();

                if(dataSnapshot.hasChild("format")) {
                    final String format = dataSnapshot.child("format").getValue().toString();

                    File nfile = new File("test");

                    if (format.equals("image")) {
                        File externalStorageDirectory = Environment.getExternalStorageDirectory();
                        StringBuilder sb = new StringBuilder();
                        sb.append(externalStorageDirectory);
                        sb.append("/Dots/received/");
                        File file = new File(sb.toString());
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        String filename = oid + ".jpg";
                        nfile = new File(file, filename);

                        if (nfile.exists()) {
                            imgdownload.setVisibility(View.GONE);
                            imgplay.setVisibility(View.VISIBLE);
                        } else {
                            imgdownload.setVisibility(View.VISIBLE);
                            imgplay.setVisibility(View.GONE);
                        }
                    } else {
                        File externalStorageDirectory = Environment.getExternalStorageDirectory();
                        StringBuilder sb = new StringBuilder();
                        sb.append(externalStorageDirectory);
                        sb.append("/Dots/received/");
                        File file = new File(sb.toString());
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        String filename = oid + ".mp4";
                        nfile = new File(file, filename);

                        if (nfile.exists()) {
                            imgdownload.setVisibility(View.GONE);
                            imgplay.setVisibility(View.VISIBLE);
                        } else {
                            imgdownload.setVisibility(View.VISIBLE);
                            imgplay.setVisibility(View.GONE);
                        }
                    }

                    final File finalNfile1 = nfile;
                    imgplay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(format.equals("image")) {
                                Uri fromFile2 = Uri.fromFile(finalNfile1);
                                Intent intent2 = new Intent("android.intent.action.VIEW");
                                intent2.setDataAndType(fromFile2, "image/*");
                                startActivity(Intent.createChooser(intent2, "Open image Using"));
                            }else {
                                Uri fromFile2 = Uri.fromFile(finalNfile1);
                                Intent intent2 = new Intent("android.intent.action.VIEW");
                                intent2.setDataAndType(fromFile2, "video/*");
                                startActivity(Intent.createChooser(intent2, "Open video Using"));
                            }
                        }
                    });

                    final File finalNfile = nfile;
                    imgdownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(provider_order_accepted.this, "Download started...", Toast.LENGTH_SHORT).show();
                            sref.child("order").child(pid).child(oid).getFile(finalNfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    imgdownload.setVisibility(View.GONE);
                                    imgplay.setVisibility(View.VISIBLE);
                                    Toast.makeText(provider_order_accepted.this, "Download Completed", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(provider_order_accepted.this, "File does not exists", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    });
                }
                getdirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("google.navigation:q=");
                        sb.append(lat);
                        sb.append(",");
                        sb.append(lng);
                        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(sb.toString()));
                        intent.setPackage("com.google.android.apps.maps");
                        provider_order_accepted.this.startActivity(intent);
                    }
                });

                btnsubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent = new Intent(provider_order_accepted.this, timer.class);
                        intent.putExtra("pid",pid);
                        intent.putExtra("oid",oid);
                        dbrorder.child(pid).child(oid).child(fauth.getCurrentUser().getUid()).child("status").setValue("order_in_progress");
                        String stime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(Calendar.getInstance().getTime());
                        int ct = (int) Calendar.getInstance().getTimeInMillis();
                        spref.edit().putInt("start_time",ct).commit();
                        dbrorder.child(pid).child(oid).child(fauth.getCurrentUser().getUid()).child("start_time").setValue(stime).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(intent);
                            }
                        });

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setname() {
        dbruser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Setting my name
                String nm = "";
                if(dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("fname")){
                    nm = dataSnapshot.child(fauth.getCurrentUser().getUid()).child("fname").getValue().toString();
                }
                tvname.setText(nm);

                //setting customer's name
                String dt = "";
                if(dataSnapshot.child(pid).hasChild("fname")){
                    dt = dataSnapshot.child(pid).child("fname").getValue().toString();
                }
                if(dataSnapshot.child(pid).hasChild("lname")){
                    dt = dt +" "+ dataSnapshot.child(pid).child("lname").getValue().toString();
                }
                if(dataSnapshot.child(pid).hasChild("eaddress")){
                    dt = dt +"\n"+dataSnapshot.child(pid).child("eaddress").getValue().toString();
                }
                tvcdetail.setText(dt);

                if(dataSnapshot.child(pid).hasChild("ph")){
                    tvcphone.setText(dataSnapshot.child(pid).child("ph").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
