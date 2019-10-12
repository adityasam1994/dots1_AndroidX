package aditya.cyfoes.com.dots1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.hbb20.CountryCodePicker;

public class newsignup extends AppCompatActivity{

    TextInputEditText etfname, etlname, etemail, etpass, etpass2, etphone,etaddress;
    CountryCodePicker ccp;
    Boolean number_valid;
    FirebaseAuth fauth;
    Button btnsignup;
    TextView btnlogin;
    ProgressDialog pd;
    //creating reference to firebase storage
    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydots-554f6.appspot.com/");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsignup);

        pd= new ProgressDialog(this);
        fauth = FirebaseAuth.getInstance();
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
                pd.setMessage("Registering...");
                pd.show();

                fauth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            newuser nu = new newuser(fname, lname,address,phone,0.0,0.0);
                            dbruser.child(fauth.getCurrentUser().getUid()).setValue(nu);
                            pd.dismiss();
                            startActivity(new Intent(newsignup.this, select.class));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(newsignup.this, "Failed to signup", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
}
