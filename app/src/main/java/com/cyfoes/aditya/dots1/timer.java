package com.cyfoes.aditya.dots1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class timer extends AppCompatActivity {

    DatabaseReference dbrorder = FirebaseDatabase.getInstance().getReference("Orders");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    int start_time;
    String pid,oid;
    SharedPreferences spref;
    TextView tvtimer;
    Button btncomplete;
    FirebaseAuth fauth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        spref = getSharedPreferences("timer",MODE_PRIVATE);
        start_time = spref.getInt("start_time", (int) Calendar.getInstance().getTimeInMillis());
        oid = getIntent().getExtras().getString("oid", "");
        pid = getIntent().getExtras().getString("pid", "");
        tvtimer = (TextView)findViewById(R.id.tvTimer);
        btncomplete = (Button)findViewById(R.id.btncomplete);

        btncomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(Calendar.getInstance().getTime());
                spref.edit().remove("start_time").commit();
                dbrorder.child(pid).child(oid).child(fauth.getCurrentUser().getUid()).child("status").setValue("completed");
                dbrorder.child(pid).child(oid).child(fauth.getCurrentUser().getUid()).child("end_time").setValue(stime).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(timer.this, "Order completed", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(timer.this, provider_home.class));
                    }
                });
            }
        });

        starttimer();
    }

    private void starttimer() {
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                String formatted = formattime((int) (Calendar.getInstance().getTimeInMillis() - start_time));
                tvtimer.setText(formatted);
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private String formattime(int millis){
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

        return hms;
    }
}
