package aditya.cyfoes.com.dots1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fevziomurtekin.customprogress.Dialog;
import com.fevziomurtekin.customprogress.Type;
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
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.karan.churi.PermissionManager.PermissionManager;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;

public class myaccount extends AppCompatActivity {

    TextView txtstatus;
    EditText etfname, etlname, etaddress, etaddress_work, etphone;
    Button btnsave, btnchangeadd, btnchangeadd_work;
    ImageView getloc, getlocwork;
    Boolean gpsloc, gpsloc_work, numbervalid;
    LinearLayout linearLayout_work;
    ImageView btnback, btnplus;
    CountryCodePicker ccp;
    double latitude, longitude, latitude_work, longitude_work;
    PermissionManager permission;
    ImageView profilepic;
    Uri imageuri;
    ProgressDialog pd;
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    StorageReference sref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydots-554f6.appspot.com/");
    FirebaseAuth fauth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);

        pd=new ProgressDialog(this);
        permission = new PermissionManager() {};

        txtstatus = (TextView) findViewById(R.id.txtstatus);
        etfname = (EditText) findViewById(R.id.etfname);
        etlname = (EditText) findViewById(R.id.etlname);
        etaddress = (EditText) findViewById(R.id.etaddress);
        etaddress_work = (EditText) findViewById(R.id.etaddress_work);
        etphone = (EditText) findViewById(R.id.etphone);

        btnsave = (Button) findViewById(R.id.saveaccount);
        btnchangeadd = (Button) findViewById(R.id.btnchangeadd);
        btnchangeadd_work = (Button) findViewById(R.id.btnchangeadd_work);

        btnback = (ImageView) findViewById(R.id.btnback);
        btnplus = (ImageView) findViewById(R.id.btnplus);
        getloc = (ImageView) findViewById(R.id.getloc);
        getlocwork = (ImageView) findViewById(R.id.getloc_work);
        profilepic = (ImageView) findViewById(R.id.profilepic);

        ccp = (CountryCodePicker) findViewById(R.id.code_picker);

        ccp.registerCarrierNumberEditText(etphone);
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                numbervalid = isValidNumber;
            }
        });

        linearLayout_work = (LinearLayout) findViewById(R.id.linearLayout_work);

        setuserdetails();

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnchangeadd.setVisibility(View.VISIBLE);
                gpsloc = true;
                checkgpsstate();
            }
        });

        getlocwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnchangeadd_work.setVisibility(View.VISIBLE);
                gpsloc_work = true;
                checkgpsstate_work();
            }
        });

        btnchangeadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(myaccount.this);
                builder.setTitle("Changing address");
                builder.setMessage("Are you sure you want ot enter manually?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        btnchangeadd.setVisibility(View.INVISIBLE);
                        gpsloc = false;
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(myaccount.this, "Okay", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        btnchangeadd_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(myaccount.this);
                builder.setTitle("Changing address");
                builder.setMessage("Are you sure you want ot enter manually?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        btnchangeadd_work.setVisibility(View.INVISIBLE);
                        gpsloc_work = false;
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(myaccount.this, "Okay", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numbervalid) {
                    saveuserdetails();
                } else {
                    Toast.makeText(myaccount.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(1, 1)
                        .start(myaccount.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageuri = result.getUri();
                profilepic.setImageURI(imageuri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    /*Saving user details*/

    private void saveuserdetails() {
        pd.setMessage("Uploading details...");
        pd.show();
        final String fname, lname, address, address_work, phone;

        fname = etfname.getText().toString().trim();
        lname = etlname.getText().toString().trim();
        address = etaddress.getText().toString().trim();
        address_work = etaddress_work.getText().toString().trim();
        phone = etphone.getText().toString().trim();

        if (imageuri != null) {
            sref.child("images").child(fauth.getCurrentUser().getUid()).putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            DatabaseReference dbrnew = dbruser.child(fauth.getCurrentUser().getUid());
                            dbrnew.child("address").setValue(address);
                            dbrnew.child("fname").setValue(fname);
                            dbrnew.child("lname").setValue(lname);
                            dbrnew.child("ph").setValue(phone);
                            dbrnew.child("lati").setValue(latitude);
                            dbrnew.child("longi").setValue(longitude);
                            dbrnew.child("info").child("lati").setValue(latitude_work);
                            dbrnew.child("info").child("longi").setValue(longitude_work);

                            if (linearLayout_work.getVisibility() != View.GONE) {
                                dbrnew.child("info").child("eaddress").setValue(address_work);
                            }
                            pd.dismiss();
                            Toast.makeText(myaccount.this, "Details Saved", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(myaccount.this, "Failed to update details", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            DatabaseReference dbrnew = dbruser.child(fauth.getCurrentUser().getUid());
            dbrnew.child("address").setValue(address);
            dbrnew.child("fname").setValue(fname);
            dbrnew.child("lname").setValue(lname);
            dbrnew.child("ph").setValue(phone);
            dbrnew.child("lati").setValue(latitude);
            dbrnew.child("longi").setValue(longitude);
            dbrnew.child("info").child("lati").setValue(latitude_work);
            dbrnew.child("info").child("longi").setValue(longitude_work);

            if (linearLayout_work.getVisibility() != View.GONE) {
                dbrnew.child("info").child("eaddress").setValue(address_work);
            }
            pd.dismiss();
            Toast.makeText(this, "Details Saved", Toast.LENGTH_SHORT).show();
        }
    }

    /*GPS state checker*/

    private void checkgpsstate() {
        if (!SmartLocation.with(myaccount.this).location().state().isGpsAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(myaccount.this);
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
                    Toast.makeText(myaccount.this, "Okay", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        } else {
            setlocation();
        }
    }

    /*set location*/

    private void setlocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(myaccount.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permission.checkAndRequestPermissions(myaccount.this);
                return;
            }
            SmartLocation.with(myaccount.this).location().oneFix().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    SmartLocation.with(myaccount.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                        @Override
                        public void onAddressResolved(Location location, List<Address> list) {
                            if (list.size() > 0) {
                                etaddress.setText(list.get(0).getAddressLine(0));
                            }
                        }
                    });
                }
            });
        } else {
            SmartLocation.with(myaccount.this).location().oneFix().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    SmartLocation.with(myaccount.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                        @Override
                        public void onAddressResolved(Location location, List<Address> list) {
                            if (list.size() > 0) {
                                etaddress.setText(list.get(0).getAddressLine(0));
                            }
                        }
                    });
                }
            });
        }
    }

    /*GPS state checker Work*/

    private void checkgpsstate_work() {
        if (!SmartLocation.with(myaccount.this).location().state().isGpsAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(myaccount.this);
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
                    Toast.makeText(myaccount.this, "Okay", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        } else {
            setlocation_work();
        }
    }

    /*set location Work*/

    private void setlocation_work() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(myaccount.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permission.checkAndRequestPermissions(myaccount.this);
                return;
            }
            SmartLocation.with(myaccount.this).location().oneFix().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    SmartLocation.with(myaccount.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                        @Override
                        public void onAddressResolved(Location location, List<Address> list) {
                            if (list.size() > 0) {
                                etaddress_work.setText(list.get(0).getAddressLine(0));
                            }
                        }
                    });
                }
            });
        } else {
            SmartLocation.with(myaccount.this).location().oneFix().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    SmartLocation.with(myaccount.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                        @Override
                        public void onAddressResolved(Location location, List<Address> list) {
                            if (list.size() > 0) {
                                etaddress_work.setText(list.get(0).getAddressLine(0));
                            }
                        }
                    });
                }
            });
        }
    }

    /*Setting user details*/

    private void setuserdetails() {
        setprofilepic();
        dbruser.child(fauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = "";
                if (dataSnapshot.hasChild("current_status")) {
                    status = dataSnapshot.child("current_status").getValue().toString();
                    txtstatus.setText(status);
                    if (status.equals("customer")) {
                        txtstatus.setVisibility(View.GONE);
                    }
                } else {
                    if (dataSnapshot.hasChild("status")) {
                        status = dataSnapshot.child("status").getValue().toString();
                        txtstatus.setText(status);
                        if (status.equals("customer")) {
                            txtstatus.setVisibility(View.GONE);
                        }
                    }
                }

                if (dataSnapshot.hasChild("fname")) {
                    etfname.setText(dataSnapshot.child("fname").getValue().toString());
                }
                if (dataSnapshot.hasChild("lname")) {
                    etlname.setText(dataSnapshot.child("lname").getValue().toString());
                }
                if (dataSnapshot.hasChild("address")) {
                    etaddress.setText(dataSnapshot.child("address").getValue().toString());
                }
                if (status.equals("customer")) {
                    linearLayout_work.setVisibility(View.GONE);
                } else {
                    if (dataSnapshot.child("status").getValue().toString().equals("provider")) {
                        if (dataSnapshot.hasChild("info")) {
                            if (dataSnapshot.child("info").hasChild("eaddress")) {
                                etaddress_work.setText(dataSnapshot.child("info").child("eaddress").getValue().toString());
                            }
                            if (dataSnapshot.child("info").hasChild("lati") && dataSnapshot.hasChild("longi")) {
                                latitude_work = Double.parseDouble(dataSnapshot.child("info").child("lati").getValue().toString());
                                longitude_work = Double.parseDouble(dataSnapshot.child("info").child("longi").getValue().toString());
                            }
                        }
                    }
                }
                if (dataSnapshot.hasChild("ph")) {
                    etphone.setText(dataSnapshot.child("ph").getValue().toString());
                    numbervalid = ccp.isValidFullNumber();
                }
                if (dataSnapshot.hasChild("lati") && dataSnapshot.hasChild("longi")) {
                    latitude = Double.parseDouble(dataSnapshot.child("lati").getValue().toString());
                    longitude = Double.parseDouble(dataSnapshot.child("longi").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*set profile pic*/

    private void setprofilepic() {
        sref.child("images").child(fauth.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resize(100,100).into(profilepic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(myaccount.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
