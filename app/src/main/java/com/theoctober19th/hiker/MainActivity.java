package com.theoctober19th.hiker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Objects required for acessing location and stuffs
    LocationManager locationManager;
    LocationListener locationListener;

    FusedLocationProviderClient mFusedLocationClient;

    Location mLocation;

    //The View elements (The Textviews) in the layout are defined here
    TextView mCoordinatesTextView;
    TextView mAccuracyTextView;
    TextView mAltitudeTextView;
    TextView mAddressTextView;

    Address currentAddress;

    public void onAddressTextViewClicked(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("location", mLocation);
        intent.putExtra("info", currentAddress.getLocale());
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            if (requestCode == 01 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 * 1000, 10, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Defining the view element objects
        mCoordinatesTextView = (TextView) findViewById(R.id.coordinatesTextView);
        mAccuracyTextView = (TextView) findViewById(R.id.accuracyTextView);
        mAltitudeTextView = (TextView) findViewById(R.id.altitudeTextView);
        mAddressTextView = (TextView) findViewById(R.id.addressTextView);

        //Creating the location Services client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(!location.equals(null)) refreshInformation(location);
                }
            });
        }


        //instantiating the location manager and location listeners
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                refreshInformation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 01);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10*1000, 10, locationListener);
        }
    }

    private void refreshInformation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            currentAddress = list.get(0);

            String address = "Address: ";
            if(!currentAddress.getFeatureName().equals(null)){
                address += currentAddress.getFeatureName().toString();
            }
            if(!currentAddress.getLocality().equals(null)){
                address += " | " + currentAddress.getLocality().toString();
            }
            if(!currentAddress.getSubAdminArea().equals(null)){
                address += " | " + currentAddress.getSubAdminArea().toString();
            }
            if(!currentAddress.getAdminArea().equals(null)) {
                address += " | " + currentAddress.getAdminArea().toString();
            }
            if(!currentAddress.getCountryName().equals(null)){
                address += " | " + currentAddress.getCountryName().toString();
            }
            mAddressTextView.setText(address);
            mAltitudeTextView.setText("Altitude: " + String.valueOf(location.getAltitude()) + "m.");
            mCoordinatesTextView.setText("Coordinates: " + String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
            mAccuracyTextView.setText("Accuracy: " + String.valueOf(location.getAccuracy()) + "%");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
