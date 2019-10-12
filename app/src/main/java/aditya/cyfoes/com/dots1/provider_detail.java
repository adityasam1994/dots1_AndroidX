package aditya.cyfoes.com.dots1;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class provider_detail extends AppCompatActivity {

    EditText etservice, ettime,etage, etcomment,etlocation;
    Button btnsave;
    DatabaseReference dbrservice = FirebaseDatabase.getInstance().getReference("services");
    DatabaseReference dbrservicetime = FirebaseDatabase.getInstance().getReference("servicetime");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseAuth fauth = FirebaseAuth.getInstance();
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_detail);

        pd = new ProgressDialog(this);

        etservice = (EditText)findViewById(R.id.etservice);
        ettime = (EditText)findViewById(R.id.ettime);
        etage = (EditText)findViewById(R.id.etage);
        etcomment = (EditText)findViewById(R.id.etcomment);
        etlocation = (EditText)findViewById(R.id.etlocation);
        btnsave = (Button)findViewById(R.id.btnsave);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedetails();
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

        if(!service.isEmpty() && !time.isEmpty() && !age.isEmpty() && !comment.isEmpty() && !address.isEmpty()){
            save_provider_detail spd = new save_provider_detail(service, age, time, comment,address, 0.0,0.0);
            dbruser.child(fauth.getCurrentUser().getUid()).child("info").setValue(spd);

            pd.dismiss();
            Toast.makeText(this, "Details Saved", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(provider_detail.this, provider_home.class));
        }
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
