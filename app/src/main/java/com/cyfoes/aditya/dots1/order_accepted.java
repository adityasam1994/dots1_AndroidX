package com.cyfoes.aditya.dots1;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

public class order_accepted extends AppCompatActivity {

    DatabaseReference dbrorder = FirebaseDatabase.getInstance().getReference("Orders");
    DatabaseReference dbruser = FirebaseDatabase.getInstance().getReference("Users");
    StorageReference sref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydots-554f6.appspot.com/");
    FirebaseAuth fauth=FirebaseAuth.getInstance();
    ImageView btnback;
    CircleImageView providerimage;
    RatingBar rating;
    TextView providername;
    TextView providerage;
    TextView providerphone;
    TextView qrcode;
    ImageView btncall;
    ImageView qrcodeimage;
    String pid, oid, lastpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_accepted);

        btnback = (ImageView)findViewById(R.id.btnback);
        rating = (RatingBar)findViewById(R.id.providerrating);
        providerimage = (CircleImageView)findViewById(R.id.providerpic);
        providername = (TextView)findViewById(R.id.tvname);
        providerage = (TextView)findViewById(R.id.tvage);
        providerphone = (TextView)findViewById(R.id.tvphone);
        qrcode = (TextView)findViewById(R.id.secretcode);
        btncall = (ImageView)findViewById(R.id.btncall);
        qrcodeimage = (ImageView)findViewById(R.id.qrcode);

        pid=getIntent().getExtras().getString("pid");
        oid=getIntent().getExtras().getString("oid");
        lastpage=getIntent().getExtras().getString("lastpage");

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settextvalues();

        setqrcode();
    }

    private void setqrcode() {
        dbrorder.child(fauth.getCurrentUser().getUid()).child(oid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                qrcode.setText(dataSnapshot.child("qrcode").getValue().toString());

                setqrcodeimage(dataSnapshot.child("qrcode").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setqrcodeimage(String code) {
        int wd = qrcodeimage.getLayoutParams().width;
        QRGEncoder qrgEncoder = new QRGEncoder(code, null, QRGContents.Type.TEXT, 500);

        try {
            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView
            qrcodeimage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to create QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void settextvalues(){
        dbruser.child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // set name

                String name = "";
                if(dataSnapshot.hasChild("fname")){
                    name=dataSnapshot.child("fname").getValue().toString();
                }
                if(dataSnapshot.hasChild("lname")){
                    name = name + " "+dataSnapshot.child("lname").getValue().toString();
                }

                providername.setText(name);

                // set age

                if(dataSnapshot.hasChild("info")){
                    if(dataSnapshot.child("info").hasChild("eage")){
                        providerage.setText(dataSnapshot.child("info").child("eage").getValue().toString());
                    }
                }

                // set phone number

                if(dataSnapshot.hasChild("ph")){
                    providerphone.setText(dataSnapshot.child("ph").getValue().toString());
                }

                // set picture

                if(dataSnapshot.hasChild("profilepic")){
                    Picasso.get().load(dataSnapshot.child("profilepic").getValue().toString()).resize(100,100).into(providerimage);
                }else {
                    setprofilepic();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setprofilepic() {
        sref.child("images").child(fauth.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resize(100,100).into(providerimage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(order_accepted.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
