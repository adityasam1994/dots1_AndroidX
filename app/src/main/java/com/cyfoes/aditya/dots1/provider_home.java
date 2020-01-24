package com.cyfoes.aditya.dots1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.os.Environment;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

import java.io.File;

public class provider_home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference dbrorder = FirebaseDatabase.getInstance().getReference("Orders");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    StorageReference sref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydots-554f6.appspot.com/");
    TextView tvservice, tvcode, tvdetail, tvtime, tvcomment, tvservicetype, tvcost;
    ImageView btnlines, getdirection, imgplay, imgdownload;
    EditText etcode;
    Button scanqr, btncancel, btnstart, btncustomer;
    LinearLayout details;
    FirebaseAuth fauth;
    TextView txtname, tvname;
    ImageView ivpic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        fauth = FirebaseAuth.getInstance();
        txtname = (TextView) findViewById(R.id.txtname);
        details = (LinearLayout) findViewById(R.id.details);
        getdirection = (ImageView) findViewById(R.id.getdirection);
        imgplay = (ImageView) findViewById(R.id.imgplay);
        imgdownload = (ImageView) findViewById(R.id.imgdownload);
        tvservice = (TextView) findViewById(R.id.tvcservice);
        tvcode = (TextView) findViewById(R.id.tvcode);
        tvdetail = (TextView) findViewById(R.id.tvcdetail);
        tvtime = (TextView) findViewById(R.id.tvctime);
        tvcomment = (TextView) findViewById(R.id.tvccomment);
        tvservicetype = (TextView) findViewById(R.id.tvtype);
        tvcost = (TextView) findViewById(R.id.tvcost);
        etcode = (EditText) findViewById(R.id.etcode);
        scanqr = (Button) findViewById(R.id.scanqr);
        btncancel = (Button) findViewById(R.id.btncancel);
        btnstart = (Button) findViewById(R.id.btnstart);

        btnlines = (ImageView) findViewById(R.id.lines);
        setuserprofile();
        checkorders();

        btnlines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    /*Check Orders*/
    private void checkorders() {
        dbrorder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot duser : dataSnapshot.getChildren()) {
                    for (final DataSnapshot dcode : duser.getChildren()) {
                        if (dcode.hasChild(fauth.getCurrentUser().getUid())) {

                            if (dcode.child(fauth.getCurrentUser().getUid()).child("status").getValue().toString().equals("pending")) {

                                //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                //details.setLayoutParams(params);

                                details.setVisibility(View.VISIBLE);

                                if (dcode.hasChild("service")) {
                                    tvservice.setText(dcode.child("service").getValue().toString());
                                }
                                if (dcode.hasChild("Service_date") && dcode.hasChild("time")) {
                                    tvtime.setText(dcode.child("Service_date").getValue().toString() + "/" + dcode.child("time").getValue().toString());
                                }
                                if (dcode.hasChild("ecomment")) {
                                    tvcomment.setText(dcode.child("ecomment").getValue().toString());
                                }
                                if (dcode.hasChild("servicetype")) {
                                    tvservicetype.setText(dcode.child("servicetype").getValue().toString());
                                }
                                if (dcode.hasChild("cost")) {
                                    tvcost.setText(dcode.child("cost").getValue().toString());
                                }

                                btnstart.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dbrorder.child(duser.getKey().toString()).child(dcode.getKey().toString()).child(fauth.getCurrentUser().getUid()).child("status").setValue("accepted");
                                        details.setVisibility(View.GONE);
                                        Toast.makeText(provider_home.this, "Order was accepted", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                btncancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dbrorder.child(duser.getKey().toString()).child(dcode.getKey().toString()).child(fauth.getCurrentUser().getUid()).child("status").setValue("cancelled");
                                        details.setVisibility(View.GONE);
                                        Toast.makeText(provider_home.this, "Order was rejected", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                scanqr.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        IntentIntegrator integrator = new IntentIntegrator(provider_home.this);
                                        integrator.setPrompt("Scan a QRcode");
                                        integrator.setOrientationLocked(true);
                                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                                        integrator.setBeepEnabled(true);
                                        integrator.initiateScan();
                                    }
                                });

                                final String lat = dcode.child("latitude").getValue().toString();
                                final String lng = dcode.child("longitude").getValue().toString();

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
                                        provider_home.this.startActivity(intent);
                                    }
                                });

                                final String format = dcode.child("format").getValue().toString();

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
                                    String filename = dcode.getKey().toString() + ".jpg";
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
                                    String filename = dcode.getKey().toString() + ".mp4";
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
                                        Toast.makeText(provider_home.this, "Download started...", Toast.LENGTH_SHORT).show();
                                        sref.child("order").child(duser.getKey()).child(dcode.getKey()).getFile(finalNfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                imgdownload.setVisibility(View.GONE);
                                                imgplay.setVisibility(View.VISIBLE);
                                                Toast.makeText(provider_home.this, "Download Completed", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(provider_home.this, "File does not exists", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                });

                                setuserdetails(duser.getKey().toString(), duser.child(dcode.getKey().toString()).child("eaddress").getValue().toString());
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

    /*Set user detail*/
    private void setuserdetails(String customer_id, final String address) {
        dbruser.child(customer_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("fname") && dataSnapshot.hasChild("lname")) {
                    tvdetail.setText(dataSnapshot.child("fname").getValue() + " " + dataSnapshot.child("lname").getValue() + "\n" + address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*Setting profile pic and name*/

    private void setuserprofile() {
        setprofilepicture();
        dbruser.child(fauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = "";
                if (dataSnapshot.hasChild("fname") && dataSnapshot.hasChild("lname")) {
                    name = dataSnapshot.child("fname").getValue().toString() + " " + dataSnapshot.child("lname").getValue().toString();
                }
                String email = fauth.getCurrentUser().getEmail().toString();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                tvname = (TextView) drawer.findViewById(R.id.tvpname);
                TextView tvemail = (TextView) drawer.findViewById(R.id.tvpmail);
                tvname.setText(name);
                tvemail.setText(email);

                btncustomer = (Button)drawer.findViewById(R.id.btnascustomer);
                btncustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbruser.child(fauth.getCurrentUser().getUid()).child("current_status").setValue("customer").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(provider_home.this, newdrawer.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(provider_home.this, "Failed to switch", Toast.LENGTH_SHORT).show();
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

    /*Setting profile picture*/

    private void setprofilepicture() {
        sref.child("images").child(fauth.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ivpic = (CircleImageView) drawer.findViewById(R.id.ivppic);
                Picasso.get().load(uri).resize(300, 300).into(ivpic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(provider_home.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.provider_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            fauth.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(provider_home.this, newlogin.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(provider_home.this, myaccount.class));
        } else if(id == R.id.nav_orders){
            startActivity(new Intent(provider_home.this, provider_myorders.class));
        }else if(id == R.id.nav_report){
            startActivity(new Intent(provider_home.this, report_problem.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
