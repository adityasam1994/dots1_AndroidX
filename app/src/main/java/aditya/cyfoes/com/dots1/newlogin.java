package aditya.cyfoes.com.dots1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class newlogin extends AppCompatActivity {

    TextInputEditText etemail, etpass;
    Button btnsignup, btnlogin;
    DatabaseReference dbruser= FirebaseDatabase.getInstance().getReference("Users");
    ImageButton fbimagebutton, gimagebutton;
    FirebaseAuth fauth;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_newlogin);

        fauth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);

        btnsignup = (Button)findViewById(R.id.btnsignup);
        btnlogin = (Button)findViewById(R.id.btnsubmit);
        etemail = (TextInputEditText)findViewById(R.id.etmail);
        etpass = (TextInputEditText)findViewById(R.id.etpass);

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

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginuser();
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
