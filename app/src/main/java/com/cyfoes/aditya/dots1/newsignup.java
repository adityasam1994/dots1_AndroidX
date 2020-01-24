package com.cyfoes.aditya.dots1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

    private static final int RC_SIGN_IN = 99990;
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
    ImageButton fbimagebutton, gimagebutton;
    LoginButton fbloginButton;
    Boolean gpsaddress = false;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager mcallbackManager;
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
        mcallbackManager = CallbackManager.Factory.create();
        fbloginButton = (LoginButton)findViewById(R.id.fsignin);
        fbimagebutton= (ImageButton)findViewById(R.id.fsigninbutton);
        gimagebutton = (ImageButton)findViewById(R.id.gsign);
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

        fbimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbloginButton.performClick();
            }
        });

        setfblogin();
        setgooglelogin();

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

        gimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


    }

    private void setgooglelogin() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut();
    }

    private void setfblogin() {
        LoginManager.getInstance().logOut();
        fbloginButton.setReadPermissions("email", "public_profile");
        fbloginButton.registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

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

        // Pass the activity result back to the Facebook SDK
        mcallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google signin failed", Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            setfarword();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(newsignup.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setfarword() {
        dbruser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(fauth.getCurrentUser().getUid().toString())){
                    if(!dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("fname")){
                        dbruser.child(fauth.getCurrentUser().getUid()).child("fname").setValue(fauth.getCurrentUser().getDisplayName());
                    }
                    if(!dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("lname")){
                        dbruser.child(fauth.getCurrentUser().getUid()).child("lname").setValue("");
                    }
                    if(!dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("profilepic")){
                        dbruser.child(fauth.getCurrentUser().getUid()).child("profilepic").setValue(fauth.getCurrentUser().getPhotoUrl().toString()+"?height=500");
                    }
                    if(!dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("status")){
                        startActivity(new Intent(newsignup.this, select.class));
                    }
                    String st = dataSnapshot.child(fauth.getCurrentUser().getUid()).child("status").getValue().toString();
                    if(st.equals("customer")){
                        startActivity(new Intent(newsignup.this, newdrawer.class));
                    }else {
                        startActivity(new Intent(newsignup.this, provider_home.class));
                    }
                }else {
                    FirebaseUser user = fauth.getCurrentUser();
                    String fn = user.getDisplayName().toString();
                    String pp = user.getPhotoUrl().toString()+"?height=500";
                    gsignin gs = new gsignin(fn, "", pp);
                    dbruser.child(fauth.getCurrentUser().getUid()).setValue(gs);
                    startActivity(new Intent(newsignup.this, select.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(newsignup.this, "Signed in", Toast.LENGTH_SHORT).show();
                            sendfarword(account.getPhotoUrl().toString());
                            FirebaseUser user = fauth.getCurrentUser();
                            dbruser.child(fauth.getCurrentUser().getUid()).child("profilepic").setValue(account.getPhotoUrl().toString());
                            startActivity(new Intent(newsignup.this, select.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void sendfarword(final String profilepicture) {
        dbruser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(fauth.getCurrentUser().getUid().toString())){
                    if(!dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("fname")){
                        dbruser.child(fauth.getCurrentUser().getUid()).child("fname").setValue(fauth.getCurrentUser().getDisplayName());
                    }
                    if(!dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("lname")){
                        dbruser.child(fauth.getCurrentUser().getUid()).child("lname").setValue("");
                    }
                    if(!dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("profilepic")){
                        dbruser.child(fauth.getCurrentUser().getUid()).child("profilepic").setValue(profilepicture);
                    }
                    if(!dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("status")){
                        startActivity(new Intent(newsignup.this, select.class));
                    }else {
                        String st = dataSnapshot.child(fauth.getCurrentUser().getUid()).child("status").getValue().toString();
                        if(st.equals("customer")){
                            startActivity(new Intent(newsignup.this, newdrawer.class));
                        }else {
                            startActivity(new Intent(newsignup.this, provider_home.class));
                        }
                    }
                }else {
                    FirebaseUser user = fauth.getCurrentUser();
                    String fn = user.getDisplayName().toString();
                    String pp = profilepicture;
                    gsignin gs = new gsignin(fn, "", pp);
                    dbruser.child(fauth.getCurrentUser().getUid()).setValue(gs);
                    startActivity(new Intent(newsignup.this, select.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
