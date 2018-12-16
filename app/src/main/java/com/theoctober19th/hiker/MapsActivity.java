package com.theoctober19th.hiker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private String mInformation = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng current;
        if(!getIntent().getExtras().get("location").equals(null)){
            Location location = (Location) getIntent().getExtras().get("location");
            current = new LatLng(location.getLatitude(), location.getLongitude());
            if(!getIntent().getExtras().get("info").equals(null)) {
                mInformation = getIntent().getExtras().get("info").toString();
            }else{
                mInformation = "Sydney";
            }
        }else{
            current = new LatLng(-34, 151);
            mInformation = "Sydney";
        }
        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(current).title(mInformation));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));


        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setLocationMarker(location);
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(listAddress != null && listAddress.size() > 0){
                        String address = "";

                        if(listAddress.get(0).getThoroughfare() != null){
                            address += listAddress.get(0).getThoroughfare() + " ";
                        }
                        if(listAddress.get(0).getLocality() != null){
                            address += listAddress.get(0).getLocality() + " ";
                        }
                        if(listAddress.get(0).getPostalCode() != null){
                            address += listAddress.get(0).getPostalCode() + " ";
                        }
                        if(listAddress.get(0).getAdminArea() != null){
                            address += listAddress.get(0).getAdminArea();
                        }
                        Toast.makeText(MapsActivity.this, address, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        if(Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        } else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastLocation != null) {
                    setLocationMarker(lastLocation);
                }
            }
        }
    }

    public void setLocationMarker(Location location){
        mMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(mInformation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
    }
}
