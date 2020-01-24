package com.cyfoes.aditya.dots1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class report_problem extends AppCompatActivity {

    EditText etmessage;
    Button btnsubmit;
    ImageView btnback;
    DatabaseReference dbrmes = FirebaseDatabase.getInstance().getReference("messages");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseAuth fauth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        btnback = (ImageView)findViewById(R.id.btnback);
        btnsubmit = (Button)findViewById(R.id.btnsubmit);
        etmessage = (EditText)findViewById(R.id.etmessage);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etmessage.getText().toString().isEmpty()){
                    String mes = etmessage.getText().toString().trim();
                    dbrmes.child(fauth.getCurrentUser().getUid()).child(System.currentTimeMillis()+"").setValue(mes).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            etmessage.setText("");
                            Toast.makeText(report_problem.this, "Your problem has been submitted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
