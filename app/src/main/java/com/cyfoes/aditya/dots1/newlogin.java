package com.cyfoes.aditya.dots1;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class newlogin extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9999;
    TextInputEditText etemail, etpass;
    Button btnsignup, btnlogin;
    DatabaseReference dbruser= FirebaseDatabase.getInstance().getReference("Users");
    ImageButton fbimagebutton, gimagebutton;
    LoginButton fbloginButton;
    FirebaseAuth fauth;
    ProgressDialog pd;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager mcallbackManager;
    Button btnreset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_newlogin);

        fauth = FirebaseAuth.getInstance();
        mcallbackManager = CallbackManager.Factory.create();

        pd = new ProgressDialog(this);

        btnreset = (Button)findViewById(R.id.btnreset);
        fbloginButton = (LoginButton)findViewById(R.id.fsignin);
        fbimagebutton= (ImageButton)findViewById(R.id.ibfb);
        gimagebutton = (ImageButton)findViewById(R.id.ibgoogle);
        btnsignup = (Button)findViewById(R.id.btnsignup);
        btnlogin = (Button)findViewById(R.id.btnsubmit);
        etemail = (TextInputEditText)findViewById(R.id.etmail);
        etpass = (TextInputEditText)findViewById(R.id.etpass);

        fbimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbloginButton.performClick();
            }
        });

        setfblogin();

        /*for testing */
        if(fauth.getCurrentUser() != null){
            sendhome();
        }

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(newlogin.this, newsignup.class));
            }
        });

        setgooglelogin();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginuser();
            }
        });

        gimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(newlogin.this, password_reset.class));
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
                            Toast.makeText(newlogin.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
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
                        startActivity(new Intent(newlogin.this, select.class));
                    }
                    String st = dataSnapshot.child(fauth.getCurrentUser().getUid()).child("status").getValue().toString();
                    if(st.equals("customer")){
                        startActivity(new Intent(newlogin.this, newdrawer.class));
                    }else {
                        startActivity(new Intent(newlogin.this, provider_home.class));
                    }
                }else {
                    FirebaseUser user = fauth.getCurrentUser();
                    String fn = user.getDisplayName().toString();
                    String pp = user.getPhotoUrl().toString()+"?height=500";
                    gsignin gs = new gsignin(fn, "", pp);
                    dbruser.child(fauth.getCurrentUser().getUid()).setValue(gs);
                    startActivity(new Intent(newlogin.this, select.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String[] splitnames(String name){
        String[] spname = name.split(" ");
        return spname;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(newlogin.this, "Signed in", Toast.LENGTH_SHORT).show();
                            sendfarword(account.getPhotoUrl().toString());
                            FirebaseUser user = fauth.getCurrentUser();
                            dbruser.child(fauth.getCurrentUser().getUid()).child("profilepic").setValue(account.getPhotoUrl().toString());
                            startActivity(new Intent(newlogin.this, select.class));
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
                        startActivity(new Intent(newlogin.this, select.class));
                    }else {
                        String st = dataSnapshot.child(fauth.getCurrentUser().getUid()).child("status").getValue().toString();
                        if(st.equals("customer")){
                            startActivity(new Intent(newlogin.this, newdrawer.class));
                        }else {
                            startActivity(new Intent(newlogin.this, provider_home.class));
                        }
                    }
                }else {
                    FirebaseUser user = fauth.getCurrentUser();
                    String fn = user.getDisplayName().toString();
                    String pp = profilepicture;
                    gsignin gs = new gsignin(fn, "", pp);
                    dbruser.child(fauth.getCurrentUser().getUid()).setValue(gs);
                    startActivity(new Intent(newlogin.this, select.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*Send Home*/

    private void sendhome() {
        dbruser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(fauth.getCurrentUser().getUid())){
                    if(dataSnapshot.child(fauth.getCurrentUser().getUid()).hasChild("status")){
                        String status = dataSnapshot.child(fauth.getCurrentUser().getUid()).child("status").getValue().toString();
                        if(status.equals("customer")){
                            startActivity(new Intent(newlogin.this, newdrawer.class));
                        }else if(status.equals("provider")){
                            startActivity(new Intent(newlogin.this, provider_home.class));
                        }
                    }else {
                        startActivity(new Intent(newlogin.this, select.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*Login user*/

    private void loginuser() {
        String email, pass;
        email = etemail.getText().toString().trim();
        pass = etpass.getText().toString();

        pd.setMessage("Logging in...");
        pd.show();

        if(!email.isEmpty() && !pass.isEmpty()){
            fauth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    pd.dismiss();
                    if(task.isSuccessful()){
                        sendhome();
                    }else {
                        Toast.makeText(newlogin.this, "Failed to login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            pd.dismiss();
            Toast.makeText(this, "Email id and Passoword are required", Toast.LENGTH_SHORT).show();
        }
    }
}
