package aditya.cyfoes.com.dots1;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class statuspage extends AppCompatActivity {

    TextView idlable, datelable, timelable, servicelable, locationlable, commentlable, costlable;
    TextView etid, etdate, ettime, etservice, etlocation, etcomment, etcost, txtdistance;
    TextView servicename;
    Button btnaccept;
    ImageView btnback;
    String sid, scost, sservice, stime, sdate, slocation, scomment, sname, username="", qrcode="";
    double latitude, longitude;
    DatabaseReference dbrorder = FirebaseDatabase.getInstance().getReference("Orders");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseAuth fauth = FirebaseAuth.getInstance();
    Boolean searchforprovider=false;
    String currentprovider="";
    ArrayList<String> rejectedproviders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statuspage);

        latitude=26.7;
        longitude=83.3;

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
        scost = "350.00";
        qrcode = getIntent().getExtras().getString("qrcode");
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
            SimpleDateFormat forma=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            Date cd= Calendar.getInstance().getTime();
            final String dt=forma.format(cd);

            PlaceOrder po = new PlaceOrder(sname, sservice, stime, sdate, slocation, username,
                    0.0 + "", "" + 0.0, scomment, scost, sid, qrcode, dt);
            dbrorder.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(sid).setValue(po);
            Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show();
            btnaccept.setText("cancel");
            searchforprovider=true;
            searchprovider();
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
}
