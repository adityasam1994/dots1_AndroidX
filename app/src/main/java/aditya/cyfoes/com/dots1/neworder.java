package aditya.cyfoes.com.dots1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class neworder extends AppCompatActivity{

    String ordercode, service, qrcode;
    Button btnsubmit;
    ImageView btnback;
    EditText etservice, ettime, etdate, etcomment, etaddress;
    TextView servicename;
    DatabaseReference dbrservice = FirebaseDatabase.getInstance().getReference("services");
    DatabaseReference dbrservicetime = FirebaseDatabase.getInstance().getReference("servicetime");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neworder);

        etservice = (EditText)findViewById(R.id.etservice);
        ettime = (EditText)findViewById(R.id.ettime);
        etdate = (EditText)findViewById(R.id.etdate);
        etcomment = (EditText)findViewById(R.id.etcomment);
        etaddress = (EditText)findViewById(R.id.etaddress);
        servicename = (TextView)findViewById(R.id.servicename);
        btnsubmit = (Button)findViewById(R.id.btnsubmit);
        btnback = (ImageView)findViewById(R.id.btnback);

        service = getIntent().getExtras().getString("service");

        generatecode();
        QRcode();

        servicename.setText(service);

        etservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createservicelist();
            }
        });

        ettime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settimelist();
            }
        });

        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setdate();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveorder();
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /*Setting date*/

    private void setdate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(neworder.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String newdate = ""+i2;
                String newmonth = ""+i1;

                if(i2 < 10){
                    newdate = "0"+i2;
                }
                if(i1 < 10){
                    newmonth = "0"+i1;
                }

                etdate.setText(newdate+"/"+newmonth+"/"+i);
            }
        },year,month,dayOfMonth);
        dialog.show();
    }

    /*Time list*/

    private void settimelist() {
        final Dialog dialog = new Dialog(neworder.this);
        dialog.setContentView(R.layout.list_dialog);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout listlayout = dialog.findViewById(R.id.listlayout);
        dbrservicetime.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot d : dataSnapshot.getChildren()){
                    LayoutInflater inflater = LayoutInflater.from(neworder.this);
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

    private void createservicelist() {
        final Dialog dialog = new Dialog(neworder.this);
        dialog.setContentView(R.layout.list_dialog);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout listlayout = dialog.findViewById(R.id.listlayout);

        dbrservice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot dservice: dataSnapshot.child(service).getChildren()){
                    if(!dservice.getKey().toString().equals("imagerequired") && !dservice.getKey().toString().equals("logolink")){
                        LayoutInflater inflater = LayoutInflater.from(neworder.this);
                        LinearLayout lay = (LinearLayout) inflater.inflate(R.layout.service_list_text, null, false);

                        TextView text = lay.findViewById(R.id.text);
                        text.setText(dservice.getKey().toString());

                        listlayout.addView(lay);

                        lay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                etservice.setText(dservice.getKey().toString());
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*Saving order*/

    private void saveorder() {
        String time, date, comment, address, servicetype;
        servicetype = etservice.getText().toString().trim();
        time = ettime.getText().toString().trim();
        date =etdate.getText().toString().trim();
        comment = etcomment.getText().toString().trim();
        address = etaddress.getText().toString().trim();

        if(!time.isEmpty() && !date.isEmpty() && !comment.isEmpty() && !address.isEmpty()){
            Intent intent = new Intent(neworder.this, statuspage.class);

            intent.putExtra("service", service);
            intent.putExtra("servicetype",servicetype);
            intent.putExtra("time", time);
            intent.putExtra("date", date);
            intent.putExtra("comment", comment);
            intent.putExtra("address", address);
            intent.putExtra("orderid", ordercode);
            intent.putExtra("qrcode",qrcode);

            startActivity(intent);
        }
    }

    /*Order Code Generator*/

    private void generatecode() {
        final String ALPHA_NUM="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder=new StringBuilder();
        int count=7;
        while (count-- !=0){
            int charecter=(int)(Math.random()*ALPHA_NUM.length());
            builder.append(ALPHA_NUM.charAt(charecter));
            ordercode=builder.toString();
        }
    }

    /*QR code*/

    private void QRcode() {
        final String ALPHA_NUM="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder=new StringBuilder();
        int count=10;
        while (count-- !=0){
            int charecter=(int)(Math.random()*ALPHA_NUM.length());
            builder.append(ALPHA_NUM.charAt(charecter));
            qrcode=builder.toString();
        }
    }

}
