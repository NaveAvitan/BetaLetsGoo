package com.example.betaletsgoo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MediaPlayer music;

    Button smss;
    final int SE=1;
    String num2= null;
    AlertDialog.Builder adb;
    Context mainContext;

    private static final int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted = false;
    private Location lastKnownLocation;
    String tvLat, tvLong;

     boolean hasclicked= false;
  



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = this;
        smss= findViewById(R.id.emergencysmsbutton);
        smss.setEnabled(false);
        if(checkper(Manifest.permission.SEND_SMS)){
            smss.setEnabled(true);
        }else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS},SE);


        }
        smss.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                adb = new AlertDialog.Builder(mainContext);
                adb.setCancelable(false);
                adb.setTitle("enter emergency numbers");
                final EditText eT= new EditText(mainContext);
                adb.setView(eT);
                adb.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //NUM1= eT1.getText().toString();
                        num2= eT.getText().toString();
                    }
                });
                AlertDialog ad= adb.create();
                ad.show();
                return true;
            }

        });

        smss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationPermission();
                getDeviceLocation();
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_LOCATION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                getDeviceLocation();
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                tvLat= (String.valueOf(lastKnownLocation.getLatitude()));
                                tvLong=(String.valueOf(lastKnownLocation.getLongitude()));
                            }
                        } else {

                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void startsiren(View view) {
        hasclicked= true;
        music= MediaPlayer.create(this,R.raw.sirensounf2);
        music.start();
        //hasclicked=false;
    }

    public void fakecall(View view) {
        hasclicked=true;
        music= MediaPlayer.create(this,R.raw.sirensound);
        music.start();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(5000);
        }
        //hasclicked=false;
    }

    public void soms(View view) {
        hasclicked= true;
        String phonenumber= num2;
        String textmassage = "i need u here its sos" + "http://maps.google.com/?q=<"+tvLat+">,<"+tvLong+">" ;

        if (phonenumber==null || phonenumber.length()==0 || textmassage==null || textmassage.length()==0){
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkper(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phonenumber, null, textmassage, null, null);
            Toast.makeText(this, "massage sent", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();

        }

    }
    public boolean checkper( String permission ){
        int check= ContextCompat.checkSelfPermission(this,permission);
        return (check== PackageManager.PERMISSION_GRANTED);
    }


    public void needMH(View view) {

        Intent si = new Intent(this, MentalHelpActivity.class);
        startActivity(si);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /**
     * switch activity
     * @param menu
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem menu) {
        String st = menu.getTitle().toString();
        if ((st.equals("Log In"))) {
             Intent si = new Intent(this, LogIn.class);
             startActivity(si);
        }
        if ((st.equals("SignIn"))) {
             Intent si = new Intent(this, SignIn.class);
             startActivity(si);
        }
        return  true;
    }

    public void gototest(View view) {
       hasclicked=true;
        Intent si = new Intent(this,Test.class);
        startActivity(si);

    }
}