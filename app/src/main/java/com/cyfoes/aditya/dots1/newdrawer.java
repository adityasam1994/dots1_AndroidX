package com.cyfoes.aditya.dots1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karan.churi.PermissionManager.PermissionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;

public class newdrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btnnewline;
    LinearLayout gridview;
    DatabaseReference dbrservice = FirebaseDatabase.getInstance().getReference("services");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseAuth fauth = FirebaseAuth.getInstance();
    PermissionManager permission;
    TextView currentlocation;
    TextView tvname;
    ImageView ivpic;
    Button btnprovider;
    StorageReference sref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydots-554f6.appspot.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newdrawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        permission = new PermissionManager() {
        };
        //permission.checkAndRequestPermissions(this);

        btnnewline = (Button) findViewById(R.id.btnnewline);
        gridview = (LinearLayout) findViewById(R.id.gridview);
        currentlocation = (TextView) findViewById(R.id.location);

        setuserprofile();

        createcards();

        checkgpsstate();

        btnnewline.setOnClickListener(new View.OnClickListener() {
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

    /*Setting profile pic and name*/

    private void setuserprofile() {
        //setprofilepicture();
        dbruser.child(fauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = "";

                if (!dataSnapshot.hasChild("profilepic")) {
                    setprofilepicture();
                } else {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ivpic = (ImageView) drawer.findViewById(R.id.ivpic);
                    Picasso.get().load(dataSnapshot.child("profilepic").getValue().toString()).resize(100, 100).into(ivpic);
                }

                if (dataSnapshot.hasChild("fname") && dataSnapshot.hasChild("lname")) {
                    name = dataSnapshot.child("fname").getValue().toString() + " " + dataSnapshot.child("lname").getValue().toString();
                }
                String email = fauth.getCurrentUser().getEmail().toString();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                tvname = (TextView) drawer.findViewById(R.id.tvname);
                TextView tvemail = (TextView) drawer.findViewById(R.id.tvemail);
                tvname.setText(name);
                tvemail.setText(email);

                btnprovider = (Button)drawer.findViewById(R.id.btnasprovider);

                btnprovider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbruser.child(fauth.getCurrentUser().getUid()).child("current_status").setValue("provider").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(newdrawer.this, provider_home.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(newdrawer.this, "Failed to switch", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                if (dataSnapshot.hasChild("status")) {
                    if (dataSnapshot.child("status").getValue().toString().equals("provider")) {
                        btnprovider.setVisibility(View.VISIBLE);
                    }
                }
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
                ivpic = (ImageView) drawer.findViewById(R.id.ivpic);
                Picasso.get().load(uri).resize(300, 300).into(ivpic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(newdrawer.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*GPS state check*/

    private void checkgpsstate() {
        if (!SmartLocation.with(newdrawer.this).location().state().isGpsAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(newdrawer.this);
            builder.setTitle("GPS Disabled!");
            builder.setMessage("GPS should be enabled to get your location");
            builder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(newdrawer.this, "Okay", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        } else {
            getlocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permission.checkResult(requestCode, permissions, grantResults);

        getlocation();

    }

    /* Getting Location*/

    private void getlocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(newdrawer.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permission.checkAndRequestPermissions(newdrawer.this);
                return;
            }
            SmartLocation.with(newdrawer.this).location().oneFix().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    SmartLocation.with(newdrawer.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                        @Override
                        public void onAddressResolved(Location location, List<Address> list) {
                            if (list.size() > 0) {
                                currentlocation.setText(list.get(0).getAddressLine(0));
                            }
                        }
                    });
                }
            });
        } else {
            SmartLocation.with(newdrawer.this).location().oneFix().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    SmartLocation.with(newdrawer.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                        @Override
                        public void onAddressResolved(Location location, List<Address> list) {
                            if (list.size() > 0) {
                                currentlocation.setText(list.get(0).getAddressLine(0));
                            }
                        }
                    });
                }
            });
        }
    }

    /*Create cards*/

    private void createcards() {
        dbrservice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /*Card Colors*/
                ArrayList<String> mycolors = new ArrayList<>();
                mycolors.add("#0075cd");
                mycolors.add("#60a7df");
                mycolors.add("#00a1d8");
                mycolors.add("#46d9bf");
                mycolors.add("#cb603e");
                mycolors.add("#fec400");

                /*End card color*/

                String logolink1 = "";
                String name1 = "";
                String distance1 = "";
                int count = 0;
                int colorwheel = 0;
                for (final DataSnapshot dservice : dataSnapshot.getChildren()) {
                    count++;
                    if (count == 1) {
                        logolink1 = dservice.child("logolink").getValue().toString();
                        name1 = dservice.getKey().toString();
                    }

                    if (count == 2) {
                        final LayoutInflater inflater = LayoutInflater.from(newdrawer.this);
                        LinearLayout lay = (LinearLayout) inflater.inflate(R.layout.menu_items, null, false);
                        gridview.addView(lay);

                        LinearLayout layout1 = lay.findViewById(R.id.layout1);
                        LinearLayout layout2 = lay.findViewById(R.id.layout2);

                        int c1 = 0;
                        int c2 = 0;

                        c1 = colorwheel % 5;
                        c2 = c1 + 1;

                        colorwheel += 2;

                        layout1.setBackgroundColor(Color.parseColor(mycolors.get(c1)));
                        layout2.setBackgroundColor(Color.parseColor(mycolors.get(c2)));

                        ImageView image1 = lay.findViewById(R.id.image1);
                        ImageView image2 = lay.findViewById(R.id.image2);

                        TextView service1 = lay.findViewById(R.id.service1);
                        TextView service2 = lay.findViewById(R.id.service2);

                        TextView dis1 = lay.findViewById(R.id.distance1);
                        TextView dis2 = lay.findViewById(R.id.distance2);

                        Picasso.get().load(Uri.parse(logolink1)).into(image1);
                        Picasso.get().load(Uri.parse(dservice.child("logolink").getValue().toString())).into(image2);

                        service1.setText(name1);
                        service2.setText(dservice.getKey().toString());

                        count = 0;

                        final String finalName = name1;
                        layout1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(newdrawer.this, neworder.class);
                                intent.putExtra("service", finalName);
                                startActivity(intent);
                            }
                        });

                        layout2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(newdrawer.this, neworder.class);
                                intent.putExtra("service", dservice.getKey().toString());
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*Get Distance*/

    private double getdistance(String s) {
        final double[] dis = {100.0};

        dbruser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double mylati = 0.0;
                double mylongi = 0.0;
                if (dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("lati") && dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("longi")) {
                    mylati = Double.parseDouble(dataSnapshot.child(fauth.getCurrentUser().getUid()).child("lati").getValue().toString());
                    mylongi = Double.parseDouble(dataSnapshot.child(fauth.getCurrentUser().getUid()).child("longi").getValue().toString());

                    for (DataSnapshot dusers : dataSnapshot.getChildren()) {
                        if (!dusers.getKey().toString().equals(fauth.getCurrentUser().getUid().toString())) {
                            if (dusers.hasChild("info")) {
                                double lati = Double.parseDouble(dusers.child("info").child("lati").getValue().toString());
                                double longi = Double.parseDouble(dusers.child("info").child("longi").getValue().toString());

                                Location provider = new Location("");
                                provider.setLatitude(lati);
                                provider.setLongitude(longi);

                                Location customer = new Location("");
                                customer.setLongitude(mylati);
                                customer.setLongitude(mylongi);

                                double distance = customer.distanceTo(provider);

                                if (distance < dis[0]) {
                                    dis[0] = distance;
                                }
                            }
                        }
                    }

                } else {
                    dis[0] = 100000.0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return dis[0];
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newdrawer, menu);
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

        if (id == R.id.nav_account) {
            startActivity(new Intent(newdrawer.this, myaccount.class));
        } else if (id == R.id.lout) {
            fauth.signOut();
            startActivity(new Intent(newdrawer.this, newlogin.class));
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(newdrawer.this, myorders.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
