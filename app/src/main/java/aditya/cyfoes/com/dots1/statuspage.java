package aditya.cyfoes.com.dots1;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;

import static javax.xml.datatype.DatatypeConstants.DURATION;

public class statuspage extends AppCompatActivity implements OnMapReadyCallback{

    ProgressDialog pd;
    TextView idlable, datelable, timelable, servicelable, locationlable, commentlable, costlable;
    TextView etid, etdate, ettime, etservice, etlocation, etcomment, etcost, txtdistance;
    TextView servicename;
    Button btnaccept;
    ImageView btnback;
    String sid, scost, sservice, stime, sdate, slocation, scomment, sname, username="", qrcode="", filepath, filetype;
    double latitude, longitude;
    DatabaseReference dbrorder = FirebaseDatabase.getInstance().getReference("Orders");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    StorageReference sref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydots-554f6.appspot.com/");

    FirebaseAuth fauth = FirebaseAuth.getInstance();
    Boolean searchforprovider=false;
    String currentprovider="";
    FrameLayout frameLayout;
    GoogleMap gmap;
    Button btnsetmap;
    Uri fileuri;
    ArrayList<String> rejectedproviders = new ArrayList<>();
    ArrayList<MarkerOptions> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statuspage);

        pd = new ProgressDialog(this);
        idlable = (TextView)findViewById(R.id.idlable);
        datelable = (TextView)findViewById(R.id.datelable);
        timelable = (TextView)findViewById(R.id.timelable);
        servicelable = (TextView)findViewById(R.id.servicelable);
        locationlable = (TextView)findViewById(R.id.locationlable);
        commentlable = (TextView)findViewById(R.id.commentlable);
        costlable = (TextView)findViewById(R.id.costlable);
        servicename = (TextView)findViewById(R.id.servicename);
        btnaccept = (Button) findViewById(R.id.btnaccept);
        btnback = (ImageView)findViewById(R.id.btnback);
        btnsetmap = (Button)findViewById(R.id.btnsetmap);
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);

        etid=(TextView)findViewById(R.id.tvid);
        etdate = (TextView)findViewById(R.id.tvdate);
        ettime = (TextView)findViewById(R.id.tvtime);
        etservice = (TextView)findViewById(R.id.tvservice);
        etlocation = (TextView)findViewById(R.id.tvlocation);
        etcomment = (TextView)findViewById(R.id.tvcomment);
        etcost = (TextView)findViewById(R.id.tvcost);
        txtdistance = (TextView)findViewById(R.id.txtdistance);

        sid = getIntent().getExtras().getString("orderid");
        stime = getIntent().getExtras().getString("time");
        sdate = getIntent().getExtras().getString("date");
        scomment = getIntent().getExtras().getString("comment");
        slocation = getIntent().getExtras().getString("address");
        sservice = getIntent().getExtras().getString("servicetype");
        sname = getIntent().getExtras().getString("service");
        latitude = getIntent().getExtras().getDouble("latitude");
        longitude = getIntent().getExtras().getDouble("longitude");
        scost = "350.00";
        qrcode = getIntent().getExtras().getString("qrcode");
        filepath = getIntent().getExtras().getString("filepath");
        filetype = getIntent().getExtras().getString("filetype");

        //Bitmap b = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);

        //fileuri = getImageUri(this, b);
        fileuri = getIntent().getParcelableExtra("fileuri");

        MapFragment mapFragment=(MapFragment)getFragmentManager().findFragmentById(R.id.gmap);
        mapFragment.getMapAsync(this);

        getusername();

        setvalues();

        final ViewTreeObserver observer=etlocation.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height=etlocation.getHeight();
                LinearLayout.LayoutParams pp= (LinearLayout.LayoutParams) (locationlable).getLayoutParams();
                pp.height=height;
                locationlable.setLayoutParams(pp);
            }
        });

        final ViewTreeObserver observer1=etcomment.getViewTreeObserver();
        observer1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height=etcomment.getHeight();
                LinearLayout.LayoutParams pp= (LinearLayout.LayoutParams) (commentlable).getLayoutParams();
                pp.height=height;
                commentlable.setLayoutParams(pp);
            }
        });

        btnaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeorder();
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnaccept.getText().toString().equals("accept")){
                    finish();
                }else {
                    startActivity(new Intent(statuspage.this, newdrawer.class));
                }
            }
        });

        btnsetmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnsetmap.getText().toString().equals("Show on map")) {
                    btnsetmap.setText("Done");
                    ValueAnimator anim = ValueAnimator.ofInt(frameLayout.getMeasuredHeight(), frameLayout.getMeasuredHeight() * 2);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();
                            layoutParams.height = val;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    });
                    anim.setDuration(1000);
                    anim.start();
                }else {
                    btnsetmap.setText("Show on map");
                    ValueAnimator anim = ValueAnimator.ofInt(frameLayout.getMeasuredHeight(), frameLayout.getMeasuredHeight()/ 2);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();
                            layoutParams.height = val;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    });
                    anim.setDuration(1000);
                    anim.start();
                }
            }
        });

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /*Getting username*/

    private void getusername() {
        dbruser.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("fname").getValue().toString() + " "+ dataSnapshot.child("lname").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*placing order*/

    private void placeorder() {
        if(btnaccept.getText().toString().equals("accept")) {
            sref.child("order/").child(sid).putFile(fileuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            SimpleDateFormat forma=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                            Date cd= Calendar.getInstance().getTime();
                            final String dt=forma.format(cd);

                            PlaceOrder po = new PlaceOrder(sname, sservice, stime, sdate, slocation, username,
                                    latitude+"", longitude+"", scomment, scost, sid, qrcode, dt,filetype);
                            dbrorder.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(sid).setValue(po);
                            Toast.makeText(statuspage.this, "Order Placed", Toast.LENGTH_SHORT).show();
                            btnaccept.setText("cancel");
                            searchforprovider=true;
                            pd.dismiss();
                            searchprovider();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                    pd.setMessage("Placing order... "+(int)progress+"%");
                    pd.show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(statuspage.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            });

        }else if (btnaccept.getText().toString().equals("cancel")){
            dbrorder.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(sid).child("status").setValue("cancelled");
            searchforprovider=false;
            Toast.makeText(this, "Order Cancelled", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(statuspage.this, newdrawer.class));
        }
    }

    /*Setting values*/

    private void setvalues() {
        etservice.setText(sservice);
        ettime.setText(stime);
        etdate.setText(sdate);
        etcomment.setText(scomment);
        etlocation.setText(slocation);
        etcost.setText(scost);
        etid.setText(sid);
        servicename.setText(sname);
    }


    /*Provider finder*/

    private void searchprovider() {
        if (searchforprovider == true) {
            dbruser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    double distance = 50000;
                    double plati = 0, plongi=0;
                    currentprovider="";

                    for (DataSnapshot duser : dataSnapshot.getChildren()) {
                        if (!rejectedproviders.contains(duser.getKey().toString())) {
                            if (duser.hasChild("status")) {
                                String status = duser.child("status").getValue().toString();
                                if (status.equals("provider")) {
                                    if (duser.hasChild("info")) {
                                        if (duser.child("info").child("eservice").getValue().toString().equals(sname)) {
                                            double lati = Double.parseDouble(duser.child("info").child("lati").getValue().toString());
                                            double longi = Double.parseDouble(duser.child("info").child("longi").getValue().toString());

                                            Location provider = new Location("");
                                            provider.setLongitude(longi);
                                            provider.setLatitude(lati);

                                            Location customer = new Location("");
                                            customer.setLatitude(latitude);
                                            customer.setLongitude(longitude);

                                            double dis = customer.distanceTo(provider);

                                            if (dis < distance) {
                                                distance = dis;
                                                plati = lati;
                                                plongi = longi;
                                                currentprovider = duser.getKey().toString();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(!currentprovider.equals("")) {
                        SimpleDateFormat forma=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                        Date cd= Calendar.getInstance().getTime();
                        final String dt=forma.format(cd);

                        dbrorder.child(fauth.getCurrentUser().getUid()).child(sid).child(currentprovider).child("status").setValue("pending");
                        dbrorder.child(fauth.getCurrentUser().getUid()).child(sid).child(currentprovider).child("time").setValue(dt);
                        double roundOff = (double) Math.round((distance/1000) * 100) / 100;
                        txtdistance.setText((roundOff) + " Km");
                        setprovidermarker(plati, plongi);
                        checkstatus();
                    }else {
                        btnaccept.setClickable(false);
                        txtdistance.setText("No provider was found");
                        searchforprovider=false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    /*Creating marker for provider*/

    private void setprovidermarker(double plati, double plongi) {
        LatLng plocation = new LatLng(plati, plongi);

        //markers.get(1).visible(false);

        MarkerOptions markerOptions  = new MarkerOptions();
        markerOptions.position(plocation);
        markerOptions.title("Provider");
        markerOptions.icon(bitmatdescriptorfromVector(getApplicationContext(), R.drawable.ic_iconfinder_pin_provider));
        gmap.addMarker(markerOptions);

        markers.add(markerOptions);

        //markers.remove(1);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(MarkerOptions marker : markers){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        gmap.animateCamera(cu);
    }

    /*Check Status of order acceptance*/

    private void checkstatus(){
        dbrorder.child(fauth.getCurrentUser().getUid()).child(sid).child(currentprovider).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("status").getValue().toString().equals("accepted")){
                    txtdistance.setText(txtdistance.getText().toString()+" (ACCEPTED)");
                    searchforprovider=false;
                }else if(dataSnapshot.child("status").getValue().toString().equals("rejected")){
                    rejectedproviders.add(currentprovider);
                    searchprovider();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        LatLng currentlocation = new LatLng(latitude, longitude);

        MarkerOptions markerOptions  = new MarkerOptions();
        markerOptions.position(currentlocation);
        markerOptions.title("I'm here");
        markerOptions.icon(bitmatdescriptorfromVector(getApplicationContext(), R.drawable.ic_iconfinder_pin));
        gmap.addMarker(markerOptions);
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentlocation)
                .tilt(45)
                .zoom(16)
                .build();

        markers.add(markerOptions);

        gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(btnsetmap.getText().toString().equals("Done")){
                    gmap.clear();

                    latitude = latLng.latitude;
                    longitude = latLng.longitude;

                    MarkerOptions markerOptions  = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("I'm here");
                    markerOptions.icon(bitmatdescriptorfromVector(getApplicationContext(), R.drawable.ic_iconfinder_pin));
                    gmap.addMarker(markerOptions);
                    gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                            .tilt(45)
                            .zoom(16)
                            .build();

                    markers.remove(0);
                    markers.add(markerOptions);

                    gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    Location location = new Location("");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);
                    SmartLocation.with(statuspage.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                        @Override
                        public void onAddressResolved(Location location, List<Address> list) {
                            if(list.size() > 0){
                                etlocation.setText(list.get(0).getAddressLine(0));
                            }
                        }
                    });

                }
            }
        });
    }

    private BitmapDescriptor bitmatdescriptorfromVector(Context applicationContext, int vector_res_id) {
        Drawable vectorDrawable = ContextCompat.getDrawable(applicationContext, vector_res_id);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
