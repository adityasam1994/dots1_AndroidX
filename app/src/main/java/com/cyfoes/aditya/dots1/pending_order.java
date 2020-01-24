package com.cyfoes.aditya.dots1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class pending_order extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap gmap;
    DatabaseReference dbrorder = FirebaseDatabase.getInstance().getReference("Orders");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseAuth fauth = FirebaseAuth.getInstance();
    Button mapmax, btncancel;
    String pid, oid;
    ImageView btnback;
    TextView locationlable, commentlable;
    TextView tvid, tvtime, tvlocation, tvservice, tvcomment, tvcost;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_order);

        btncancel = (Button)findViewById(R.id.btnaccept);
        locationlable = (TextView)findViewById(R.id.location_lable);
        commentlable = (TextView)findViewById(R.id.comment_label);
        btnback = (ImageView)findViewById(R.id.btnback);
        tvid = (TextView)findViewById(R.id.tvid);
        tvtime = (TextView)findViewById(R.id.tvtime);
        tvlocation = (TextView)findViewById(R.id.tvlocation);
        tvservice = (TextView)findViewById(R.id.tvservice);
        tvcomment = (TextView)findViewById(R.id.tvcomment);
        tvcost = (TextView)findViewById(R.id.tvcost);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.gmap);
        mapFragment.getMapAsync(this);

        pid = getIntent().getExtras().getString("pid");
        oid = getIntent().getExtras().getString("oid");

        settextdetails();

        final ViewTreeObserver observer = tvlocation.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = tvlocation.getHeight();
                LinearLayout.LayoutParams pp = (LinearLayout.LayoutParams) (locationlable).getLayoutParams();
                pp.height = height;
                locationlable.setLayoutParams(pp);
            }
        });

        final ViewTreeObserver observer1 = tvcomment.getViewTreeObserver();
        observer1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = tvcomment.getHeight();
                LinearLayout.LayoutParams pp = (LinearLayout.LayoutParams) (commentlable).getLayoutParams();
                pp.height = height;
                commentlable.setLayoutParams(pp);
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbrorder.child(fauth.getCurrentUser().getUid()).child(oid).child(pid).child("status").setValue("cancelled")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(pending_order.this, "Order was cancelled", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    private void settextdetails() {
        dbrorder.child(fauth.getCurrentUser().getUid()).child(oid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvid.setText(oid);
                if(dataSnapshot.hasChild("time")){
                    tvtime.setText(dataSnapshot.child("time").getValue().toString());
                }

                if(dataSnapshot.hasChild("eaddress")){
                    tvlocation.setText(dataSnapshot.child("eaddress").getValue().toString());
                }

                if(dataSnapshot.hasChild("service")){
                    tvservice.setText(dataSnapshot.child("service").getValue().toString());
                }

                if(dataSnapshot.hasChild("ecomment")){
                    tvcomment.setText(dataSnapshot.child("ecomment").getValue().toString());
                }

                if(dataSnapshot.hasChild("cost")){
                    tvcost.setText(dataSnapshot.child("cost").getValue().toString());
                }

                latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());

                onMapReady(gmap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private BitmapDescriptor bitmatdescriptorfromVector(Context applicationContext, int vector_res_id) {
        Drawable vectorDrawable = ContextCompat.getDrawable(applicationContext, vector_res_id);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        LatLng currentlocation = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentlocation);
        markerOptions.title("I'm here");
        markerOptions.icon(bitmatdescriptorfromVector(getApplicationContext(), R.drawable.ic_iconfinder_pin));
        gmap.addMarker(markerOptions);
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentlocation)
                .tilt(45)
                .zoom(16)
                .build();

        gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
