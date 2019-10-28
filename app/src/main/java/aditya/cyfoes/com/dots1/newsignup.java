package aditya.cyfoes.com.dots1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.nlopez.smartlocation.OnGeocodingListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geocoding.utils.LocationAddress;

public class newsignup extends AppCompatActivity{

    TextInputEditText etfname, etlname, etemail, etpass, etpass2, etphone,etaddress;
    CountryCodePicker ccp;
    Boolean number_valid;
    FirebaseAuth fauth;
    Button btnsignup,btneditaddress, btngps;
    TextView btnlogin;
    ProgressDialog pd;
    ImageView btnplus;
    Uri filePath;
    CircleImageView profilepic;
    PermissionManager permissionManager;
    double latitude=0.0;
    double longitude=0.0;
    Boolean gpsaddress = false;
    //creating reference to firebase storage
    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydots-554f6.appspot.com/");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsignup);

        permissionManager = new PermissionManager(){};
        pd= new ProgressDialog(this);
        fauth = FirebaseAuth.getInstance();
        profilepic = (CircleImageView)findViewById(R.id.profilepic);
        btnplus = (ImageView)findViewById(R.id.btnplus);
        etfname = (TextInputEditText)findViewById(R.id.etfname);
        etlname = (TextInputEditText)findViewById(R.id.etlname);
        etemail = (TextInputEditText)findViewById(R.id.etmail);
        etpass = (TextInputEditText)findViewById(R.id.etpass);
        etpass2 = (TextInputEditText)findViewById(R.id.etpass2);
        etphone = (TextInputEditText)findViewById(R.id.etphone);
        etaddress = (TextInputEditText)findViewById(R.id.etaddress);
        btnsignup = (Button)findViewById(R.id.btnreg);
        btnlogin = (TextView)findViewById(R.id.btnsubmit);
        ccp = (CountryCodePicker)findViewById(R.id.code_picker);
        btneditaddress = (Button)findViewById(R.id.btnchangeaddress);
        btngps = (Button)findViewById(R.id.btnloc);

        ccp.registerCarrierNumberEditText(etphone);
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                number_valid = isValidNumber;
            }
        });

        if(fauth.getCurrentUser() != null){
            Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
            checkstatus();
        }else {
            Toast.makeText(this, "Not Signed in", Toast.LENGTH_SHORT).show();
        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(newsignup.this, newlogin.class));
            }
        });

        btneditaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(newsignup.this);
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
                        Toast.makeText(newsignup.this, "Okay", Toast.LENGTH_SHORT).show();
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
                        permissionManager.checkAndRequestPermissions(newsignup.this);
                    }
                } else {
                    btneditaddress.setVisibility(View.VISIBLE);
                    gpsaddress = true;
                    setgpslocation();
                }
            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(number_valid){
                    register_user();
                }
                else {
                    Toast.makeText(newsignup.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        takepicture();
                    }else {
                        permissionManager.checkAndRequestPermissions(newsignup.this);
                    }
                }else {
                    takepicture();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                profilepic.setImageURI(filePath);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(newsignup.this,"Failed"+error,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takepicture() {
        CropImage.activity()
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setRequestedSize(500,500)
                .start(newsignup.this);
    }

    private void checkstatus() {
        dbruser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(fauth.getCurrentUser().getUid())){
                    if(dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("status")) {
                        String status = dataSnapshot.child(fauth.getCurrentUser().getUid()).child("status").getValue().toString();
                        if(status == "provider"){
                            startActivity(new Intent(newsignup.this, provider_home.class));
                        }else {
                            startActivity(new Intent(newsignup.this, newdrawer.class));
                        }
                    }else {
                        startActivity(new Intent(newsignup.this, select.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void register_user() {
        final String fname, lname, pass, pass2, email, phone, address;
        fname = etfname.getText().toString().trim();
        lname = etlname.getText().toString().trim();
        email = etemail.getText().toString().trim();
        pass= etpass.getText().toString().trim();
        pass2 = etpass2.getText().toString().trim();
        address = etaddress.getText().toString().trim();
        phone = etphone.getText().toString().trim();

        if(!fname.isEmpty() && !lname.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !pass2.isEmpty() && !address.isEmpty()){
            if(pass.equals(pass2)) {
                if(!gpsaddress) {
                    if(filePath != null) {
                        pd.setMessage("Registering...");
                        pd.show();
                        fauth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    storageRef.child("images/").child(fauth.getCurrentUser().getUid()).putFile(filePath)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    SimpleDateFormat forma=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                                                    Date cd= Calendar.getInstance().getTime();
                                                    final String dt=forma.format(cd);

                                                    newuser nu = new newuser(fname, lname, address, phone,dt ,latitude, longitude);
                                                    dbruser.child(fauth.getCurrentUser().getUid()).setValue(nu);
                                                    pd.dismiss();
                                                    startActivity(new Intent(newsignup.this, select.class));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(newsignup.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                            startActivity(new Intent(newsignup.this, select.class));
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(newsignup.this, "Failed to signup", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(this, "Please add a picture of yours", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    decodeaddress(etaddress.getText().toString());
                }
            }else {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    private void setgpslocation() {
        SmartLocation.with(newsignup.this).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                SmartLocation.with(newsignup.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                    @Override
                    public void onAddressResolved(Location location, List<Address> list) {
                        if(list.size() > 0){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            etaddress.setText(list.get(0).getAddressLine(0));
                        }
                    }
                });
            }
        });
    }

    private void decodeaddress(final String address) {
        SmartLocation.with(newsignup.this).geocoding().direct(address, new OnGeocodingListener() {
            @Override
            public void onLocationResolved(String s, List<LocationAddress> list) {
                if(list.size() > 0){
                    final String fname, lname, pass, pass2, email, phone, address;
                    fname = etfname.getText().toString().trim();
                    lname = etlname.getText().toString().trim();
                    email = etemail.getText().toString().trim();
                    pass= etpass.getText().toString().trim();
                    address = etaddress.getText().toString().trim();
                    phone = etphone.getText().toString().trim();

                    Location mylocation = list.get(0).getLocation();
                    latitude = mylocation.getLatitude();
                    longitude = mylocation.getLongitude();

                    SimpleDateFormat forma=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                    Date cd= Calendar.getInstance().getTime();
                    final String dt=forma.format(cd);

                    if(filePath != null) {
                        pd.setMessage("Registering...");
                        pd.show();
                        fauth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    storageRef.child("images/").child(fauth.getCurrentUser().getUid()).putFile(filePath)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    newuser nu = new newuser(fname, lname, address, phone,dt ,latitude, longitude);
                                                    dbruser.child(fauth.getCurrentUser().getUid()).setValue(nu);
                                                    pd.dismiss();
                                                    startActivity(new Intent(newsignup.this, select.class));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(newsignup.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                            startActivity(new Intent(newsignup.this, select.class));
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(newsignup.this, "Failed to signup", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(newsignup.this, "Please add a picture of yours", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
}
