package com.example.ppe_project;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    protected LocationManager locationManager;
    //protected LocationListener locationListener;
    //protected Context context;

    Button buttonAlertCrash, buttonAlertConstruction, buttonAlertRoadBlock, buttonAlertTrafic;
    Button buttonSend;
    Button buttonRain, buttonSnow, buttonIce;
    TextView preview, dateField, locationField;
    Boolean isUrgent = false;
    Geocoder geocoder;
    List<Address> addresses;
    String address;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            },10);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        buttonAlertCrash = findViewById(R.id.buttonAccident);
        buttonAlertCrash.setOnClickListener(this);
        buttonAlertConstruction = findViewById(R.id.buttonConstruction);
        buttonAlertConstruction.setOnClickListener(this);
        buttonAlertRoadBlock = findViewById(R.id.buttonRoadBlock);
        buttonAlertRoadBlock.setOnClickListener(this);
        buttonAlertTrafic = findViewById(R.id.buttonTraffic);
        buttonAlertTrafic.setOnClickListener(this);

        buttonRain = findViewById(R.id.buttonRain);
        buttonRain.setOnClickListener(this);
        buttonSnow = findViewById(R.id.buttonSnow);
        buttonSnow.setOnClickListener(this);
        buttonIce = findViewById(R.id.buttonIce);
        buttonIce.setOnClickListener(this);

        buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(this);

        preview = findViewById(R.id.textViewMessagePreview);
        preview.setText("Preview");
        dateField =findViewById(R.id.textViewDate);
        locationField=findViewById(R.id.textViewAddress);

    }

    @Override
    public void onClick(View v) {

        String message="Something Went Wrong";


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy G 'at' HH:mm:ss z");
        String formattedDate = df.format(c.getTime());



        switch (v.getId()){
            //Bellow determines what message should be forwarded to display
            //Traffic Messages
            case R.id.buttonAccident:

                message="Accident Ahead";
                preview.setText(message);
                dateField.setText(formattedDate);
                isUrgent = true;
                break;

            case R.id.buttonConstruction:
                message="Construction Ahead";
                preview.setText(message);
                dateField.setText(formattedDate);
                isUrgent = true;
                break;

            case R.id.buttonRoadBlock:
                message="Road Block Ahead";
                preview.setText(message);
                dateField.setText(formattedDate);
                isUrgent = true;
                break;

            case R.id.buttonTraffic:
                message="Traffic Ahead";
                preview.setText(message);
                dateField.setText(formattedDate);
                isUrgent = false;
                break;


            //Weather Messages
            case R.id.buttonRain:
                message="Rainy Weather";
                preview.setText(message);
                dateField.setText(formattedDate);
                isUrgent = false;
                break;

            case R.id.buttonSnow:
                message="Snowy Weather";
                preview.setText(message);
                dateField.setText(formattedDate);
                isUrgent = false;
                break;

            case R.id.buttonIce:
                message="Icy Weather";
                preview.setText(message);
                dateField.setText(formattedDate);
                isUrgent = true;
                break;

            //To send the previed Message to the server
            case R.id.buttonSend:



                if((String) preview.getText()=="Preview"){
                    System.out.println("No Message to be Transfered");

                    isUrgent=false;
                    break;
                }else{
                    System.out.println("Message Transfered for Sending Server");
                    ServerActivity sA = new ServerActivity((String) preview.getText(),df,(String) locationField.getText(),isUrgent);
                    try {
                        sA.execute().get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    isUrgent = false;
                    break;
                }




            default:
                preview.setText(message);
                isUrgent = false;

                break;

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
         */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        address = (String) addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        //System.out.println("Executed");
        locationField.setText(address);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
}
