package aditya.cyfoes.com.dots1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;
import com.mindorks.paracamera.Camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;
import io.nlopez.smartlocation.OnGeocodingListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geocoding.utils.LocationAddress;

public class neworder extends AppCompatActivity {

    public static final int CAMERA_CODE=9090;
    public static final int VIDEO_CODE=8080;
    Context context;
    String ordercode, service, qrcode;
    Button btnsubmit, btneditaddress, btnlocation, btnplus;
    ImageView btnback;
    EditText etservice, ettime, etdate, etcomment, etaddress;
    TextView servicename;
    Boolean gpsaddress = false;
    double latitude, longitude;
    PermissionManager permission;
    String filePath;
    RoundedImageView btnimage;
    String filetype;
    Uri fileuri;
    ByteArrayOutputStream bs;
    DatabaseReference dbrservice = FirebaseDatabase.getInstance().getReference("services");
    DatabaseReference dbrservicetime = FirebaseDatabase.getInstance().getReference("servicetime");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neworder);

        this.context = context;
        permission = new PermissionManager() {};
        //permission.checkAndRequestPermissions(this);

        etservice = (EditText) findViewById(R.id.etservice);
        ettime = (EditText) findViewById(R.id.ettime);
        etdate = (EditText) findViewById(R.id.etdate);
        etcomment = (EditText) findViewById(R.id.etcomment);
        etaddress = (EditText) findViewById(R.id.etaddress);
        servicename = (TextView) findViewById(R.id.servicename);
        btnsubmit = (Button) findViewById(R.id.btnsubmit);
        btnback = (ImageView) findViewById(R.id.btnback);
        btneditaddress = (Button) findViewById(R.id.btneditaddress);
        btnlocation = (Button) findViewById(R.id.btnlocation);
        btnplus = (Button) findViewById(R.id.btnplus);
        btnimage = (RoundedImageView) findViewById(R.id.btnimage);

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

        btnlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        btneditaddress.setVisibility(View.VISIBLE);
                        gpsaddress = true;
                        setgpslocation();
                    } else {
                        permission.checkAndRequestPermissions(neworder.this);
                    }
                } else {
                    btneditaddress.setVisibility(View.VISIBLE);
                    gpsaddress = true;
                    setgpslocation();
                }
            }
        });

        btneditaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(neworder.this);
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
                        Toast.makeText(neworder.this, "Okay", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnncaConfiguration.Builder builder = new AnncaConfiguration.Builder(neworder.this, 2);
                if (ActivityCompat.checkSelfPermission(neworder.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    permission.checkAndRequestPermissions(neworder.this);
                    return;
                }
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, CAMERA_CODE);
                camorvideo();
            }
        });
    }

    /*filetype checker*/

    public String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        String[] msplit = mimeType.split("/");
        return msplit[0];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_CODE && resultCode == RESULT_OK){
            bs = new ByteArrayOutputStream();
            Bitmap bmp = (Bitmap)data.getExtras().get("data");
            bmp.compress(Bitmap.CompressFormat.PNG, 50, bs);
            filePath = data.getExtras().get("data").toString();
            fileuri = data.getData();
        }
    }


    /*Camera or video*/
    private void  camorvideo(){
        final Dialog dialog=new Dialog(neworder.this);
        dialog.setContentView(R.layout.camorvideo);
        dialog.show();
        Button cam=dialog.findViewById(R.id.cam);
        Button vid=dialog.findViewById(R.id.video);

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filetype = "image";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_CODE);
            }
        });

        vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filetype="video";
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent, VIDEO_CODE);
                dialog.dismiss();
            }
        });
    }

    /*Set GPS location*/

    private void setgpslocation() {
        SmartLocation.with(neworder.this).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                SmartLocation.with(neworder.this).geocoding().reverse(location, new OnReverseGeocodingListener() {
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
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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

        if(!gpsaddress){
            decodeaddress(address);
        }else {
            if (!time.isEmpty() && !date.isEmpty() && !address.isEmpty()) {
                Intent intent = new Intent(neworder.this, statuspage.class);

                intent.putExtra("service", service);
                intent.putExtra("servicetype", servicetype);
                intent.putExtra("time", time);
                intent.putExtra("date", date);
                intent.putExtra("comment", comment);
                intent.putExtra("address", address);
                intent.putExtra("orderid", ordercode);
                intent.putExtra("qrcode", qrcode);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("filepath", filePath);
                intent.putExtra("filetype", filetype);
                intent.putExtra("fileuri", fileuri);
                //intent.putExtra("byteArray", bs.toByteArray());

                startActivity(intent);
            }
        }
    }

    /*Address decoder*/

    private void decodeaddress(final String address) {
        SmartLocation.with(neworder.this).geocoding().direct(address, new OnGeocodingListener() {
            @Override
            public void onLocationResolved(String s, List<LocationAddress> list) {
                if(list.size() > 0){
                    String time, date, comment, servicetype;

                    servicetype = etservice.getText().toString().trim();
                    time = ettime.getText().toString().trim();
                    date =etdate.getText().toString().trim();
                    comment = etcomment.getText().toString().trim();

                    Location mylocation = list.get(0).getLocation();
                    latitude = mylocation.getLatitude();
                    longitude = mylocation.getLongitude();

                    if(!time.isEmpty() && !date.isEmpty() && !address.isEmpty()){
                        Intent intent = new Intent(neworder.this, statuspage.class);

                        intent.putExtra("service", service);
                        intent.putExtra("servicetype",servicetype);
                        intent.putExtra("time", time);
                        intent.putExtra("date", date);
                        intent.putExtra("comment", comment);
                        intent.putExtra("address", address);
                        intent.putExtra("orderid", ordercode);
                        intent.putExtra("qrcode",qrcode);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude", longitude);
                        intent.putExtra("filepath", filePath);
                        intent.putExtra("filetype", filetype);

                        Toast.makeText(neworder.this, latitude+"/"+longitude, Toast.LENGTH_SHORT).show();

                        startActivity(intent);
                    }
                }
            }
        });
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
