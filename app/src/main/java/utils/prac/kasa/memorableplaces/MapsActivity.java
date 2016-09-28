package utils.prac.kasa.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.view.View;


//changed to AppCompatActivity because we need to use support library to use fragments and actionbar at the same time
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private int location;
    private LocationManager manager;
    private String provider;
    private static final int FINE_LOCATION = 0;

    private static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        //get the incoming intent
        Intent i = getIntent();

        location = i.getIntExtra("location", -1);

        //Log.i("map", ""+location);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = manager.getBestProvider(new Criteria(), false);

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location loc = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions,
            // and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, PERMISSIONS, FINE_LOCATION);
        }

        mMap.setMyLocationEnabled(true);
        manager.requestLocationUpdates(provider, 400, 1, this);
        loc = manager.getLastKnownLocation(provider);
        Log.i("location", loc.toString());

        String title = MainActivity.getList().get(location);
        LatLng point;

        if (title.equals("Click to add a new place") || location == -1) {
            if (loc != null) {
                point = new LatLng(loc.getLatitude(), loc.getLongitude());
                mMap.addMarker(new MarkerOptions().position(point).title(getLocationString(point)).snippet("Your Current Location"));
            } else {
                // Add a marker in Sydney and move the camera
                point = new LatLng(-33.8688, 151.2093);
                mMap.addMarker(new MarkerOptions().position(point).title(getLocationString(point)).snippet("Sydney, Australia"));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        } else {
            manager.removeUpdates(this);

            point = MainActivity.getLocations().get(location);
            mMap.addMarker(new MarkerOptions().position(point).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        }

        //setting up long click
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(this, PERMISSIONS, FINE_LOCATION);
                }
                manager.removeUpdates(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        String label = getLocationString(point);

        //dropping the new point on the map
        mMap.addMarker(new MarkerOptions().position(point).title(label).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        //adding the new location to the list;
        List<String> places = MainActivity.getList();
        if(places.get(location).equals("Click to add a new place")){
            places.clear();
        }
        places.add(label);
        MainActivity.getAdapter().notifyDataSetChanged();

        //saving this location
        MainActivity.getLocations().add(point);
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
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

    private String getLocationString(LatLng point){
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String label = new Date().toString();
        try {
            List<Address> list = geocoder.getFromLocation(point.latitude, point.longitude, 1);

            if(list != null && list.size() > 0){
                label = list.get(0).getAddressLine(0);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return label;
    }
}
