package aditya.cyfoes.com.dots1;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class provider_home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference dbrorder = FirebaseDatabase.getInstance().getReference("Orders");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    StorageReference sref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydots-554f6.appspot.com/");
    TextView tvservice, tvcode, tvdetail, tvtime, tvcomment, tvservicetype, tvcost;
    ImageView btnlines, getdirection, imgplay, imgdownload;
    EditText etcode;
    Button scanqr, btncancel, btnstart;
    LinearLayout details;
    FirebaseAuth fauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        fauth = FirebaseAuth.getInstance();
        details = (LinearLayout)findViewById(R.id.details);
        getdirection = (ImageView)findViewById(R.id.getdirection);
        imgplay = (ImageView)findViewById(R.id.imgplay);
        imgdownload = (ImageView)findViewById(R.id.imgdownload);
        tvservice = (TextView)findViewById(R.id.tvcservice);
        tvcode = (TextView)findViewById(R.id.tvcode);
        tvdetail = (TextView)findViewById(R.id.tvcdetail);
        tvtime = (TextView)findViewById(R.id.tvctime);
        tvcomment = (TextView)findViewById(R.id.tvccomment);
        tvservicetype = (TextView)findViewById(R.id.tvtype);
        tvcost = (TextView)findViewById(R.id.tvcost);
        etcode = (EditText) findViewById(R.id.etcode);
        scanqr = (Button)findViewById(R.id.scanqr);
        btncancel = (Button)findViewById(R.id.btncancel);
        btnstart = (Button)findViewById(R.id.btnstart);

        btnlines = (ImageView)findViewById(R.id.lines);

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
                for(DataSnapshot duser: dataSnapshot.getChildren()){
                    for(DataSnapshot dcode: duser.getChildren()){
                        if(dcode.hasChild(fauth.getCurrentUser().getUid())){
                            if(dcode.child(fauth.getCurrentUser().getUid()).child("status").equals("pending")){
                                if(dcode.hasChild("service")) {
                                    tvservice.setText(dcode.child("service").getValue().toString());
                                }
                                if(dcode.hasChild("Service_date") && dcode.hasChild("time")) {
                                    tvtime.setText(dcode.child("Service_date").getValue().toString()+"/"+dcode.child("time").getValue().toString());
                                }
                                if(dcode.hasChild("ecomment")) {
                                    tvcomment.setText(dcode.child("ecomment").getValue().toString());
                                }
                                if(dcode.hasChild("servicetype")) {
                                    tvservicetype.setText(dcode.child("servicetype").getValue().toString());
                                }
                                if(dcode.hasChild("cost")) {
                                    tvcost.setText(dcode.child("cost").getValue().toString());
                                }
                                setuserdetails(duser.getKey().toString(), duser.child("address").getValue().toString());
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

    /*Set user detail*/
    private void setuserdetails(String customer_id, final String address) {
        dbruser.child(customer_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("fname") && dataSnapshot.hasChild("lname")) {
                    tvdetail.setText(dataSnapshot.child("fname") + " " + dataSnapshot.child("lname") + "\n" + address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        } /*else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
