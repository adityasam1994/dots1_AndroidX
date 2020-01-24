package com.cyfoes.aditya.dots1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.List;

import io.nlopez.smartlocation.OnGeocodingListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geocoding.utils.LocationAddress;

public class provider_detail extends AppCompatActivity {

    EditText etservice, ettime,etage, etcomment,etlocation;
    Button btnsave, btneditaddress, btngps;
    DatabaseReference dbrservice = FirebaseDatabase.getInstance().getReference("services");
    DatabaseReference dbrservicetime = FirebaseDatabase.getInstance().getReference("servicetime");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseAuth fauth = FirebaseAuth.getInstance();
    ProgressDialog pd;
    double latitude=0.0;
    double longitude=0.0;
    Boolean gpsaddress = false;
    PermissionManager permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_detail);

        pd = new ProgressDialog(this);

        btneditaddress = (Button)findViewById(R.id.btneditaddress);
        etservice = (EditText)findViewById(R.id.etservice);
        ettime = (EditText)findViewById(R.id.ettime);
        etage = (EditText)findViewById(R.id.etage);
        etcomment = (EditText)findViewById(R.id.etcomment);
        etlocation = (EditText)findViewById(R.id.etlocation);
        btnsave = (Button)findViewById(R.id.btnsave);
        btngps = (Button)findViewById(R.id.btngetloc);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedetails();
            }
        });

        btneditaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(provider_detail.this);
                builder.setTitle("Changing address");
                builder.setMessage("Are you sure you want ot enter address manually?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        btneditaddress.setVisibility(View.INVISIBLE);
                        gpsaddress = false;
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(provider_detail.this, "Okay", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        btngps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        btneditaddress.setVisibility(View.VISIBLE);
                        gpsaddress = true;
                        setgpslocation();
                    } else {
                        permission.checkAndRequestPermissions(provider_detail.this);
                    }
                } else {
                    btneditaddress.setVisibility(View.VISIBLE);
                    gpsaddress = true;
                    setgpslocation();
                }
            }
        });

        etservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showservicelist();
            }
        });

        ettime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settimelist();
            }
        });
    }

    private void setgpslocation() {
        SmartLocation.with(provider_detail.this).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                SmartLocation.with(provider_detail.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                    @Override
                    public void onAddressResolved(Location location, List<Address> list) {
                        if(list.size() > 0){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            etlocation.setText(list.get(0).getAddressLine(0));
                        }
                    }
                });
            }
        });
    }

    /*Save details*/

    private void savedetails() {
        pd.setMessage("Saving details...");
        pd.show();
        String service, time, age, comment, address;
        service = etservice.getText().toString();
        time = ettime.getText().toString();
        age = etage.getText().toString().trim();
        comment = etcomment.getText().toString().trim();
        address = etlocation.getText().toString().trim();

        if(!service.isEmpty() && !time.isEmpty() && !age.isEmpty() && !address.isEmpty()) {
            if (gpsaddress) {
                save_provider_detail spd = new save_provider_detail(service, age, time, comment, address, latitude, longitude);
                dbruser.child(fauth.getCurrentUser().getUid()).child("info").setValue(spd);

                pd.dismiss();
                Toast.makeText(this, "Details Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(provider_detail.this, provider_home.class));
            }else {
                decodeaddress(etlocation.getText().toString());
            }
        }
    }

    private void decodeaddress(final String address) {
        SmartLocation.with(provider_detail.this).geocoding().direct(address, new OnGeocodingListener() {
            @Override
            public void onLocationResolved(String s, List<LocationAddress> list) {
                if(list.size() > 0){
                    String service, time, age, comment, address;
                    service = etservice.getText().toString();
                    time = ettime.getText().toString();
                    age = etage.getText().toString().trim();
                    comment = etcomment.getText().toString().trim();
                    address = etlocation.getText().toString().trim();

                    save_provider_detail spd = new save_provider_detail(service, age, time, comment, address, latitude, longitude);
                    dbruser.child(fauth.getCurrentUser().getUid()).child("info").setValue(spd);

                    pd.dismiss();
                    Toast.makeText(provider_detail.this, "Details Saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(provider_detail.this, provider_home.class));
                }else  {
                    pd.dismiss();
                    Toast.makeText(provider_detail.this, "Entered address was not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*Time list*/

    private void settimelist() {
        final Dialog dialog = new Dialog(provider_detail.this);
        dialog.setContentView(R.layout.list_dialog);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout listlayout = dialog.findViewById(R.id.listlayout);
        dbrservicetime.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot d : dataSnapshot.getChildren()){
                    LayoutInflater inflater = LayoutInflater.from(provider_detail.this);
                    LinearLayout lay = (LinearLayout) inflater.inflate(R.layout.service_list_text, null, false);
                    final TextView txt = lay.findViewById(R.id.text);
                    txt.setText(d.getValue().toString());
                    listlayout.addView(lay);

                    lay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ettime.setText(d.getValue().toString());
                            dialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*Service list*/

    private void showservicelist() {
        final Dialog dialog = new Dialog(provider_detail.this);
        dialog.setContentView(R.layout.list_dialog);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout listlayout = dialog.findViewById(R.id.listlayout);
        dbrservice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot d : dataSnapshot.getChildren()){
                    LayoutInflater inflater = LayoutInflater.from(provider_detail.this);
                    LinearLayout lay = (LinearLayout) inflater.inflate(R.layout.service_list_text, null, false);
                    final TextView txt = lay.findViewById(R.id.text);
                    txt.setText(d.getKey().toString());
                    listlayout.addView(lay);

                    lay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etservice.setText(d.getKey().toString());
                            dialog.dismiss();
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
