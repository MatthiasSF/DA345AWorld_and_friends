package com.example.matth.p2;

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Activity that handles the map used in the application
 *
 * @author Matthias Falk
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Location location;
    private ArrayList<MarkerOptions> markers = new ArrayList<>();
    private ServerController serverController;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_PERMISSION_LOCATION = 255;
    private static final int REQUEST_PERMISSION_FINELOCATION = 155;
    private String ID;

    /**
     * onCreate method. Gives the Singleton class an reference. Asks the user for the permissions needed to display the location
     * if not already given
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Singleton singleton = Singleton.getReference();
        singleton.setMapsActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ID = getIntent().getStringExtra("ID");
        serverController = new ServerController(this);
        initializeLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FINELOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Initializes the LocationManager and the LocationListener
     */
    private void initializeLocation() {
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        locationListener = new LocList();
    }

    /**
     * Clears all markers on the map
     */
    public void clearAllMarkers() {
        markers.clear();
        mMap.clear();
    }

    /**
     * Clears the user's marker
     *
     * @param name - the title set by the user
     */
    public void clearMyMarker(String name) {
        for (int i = 0; i < markers.size(); i++) {
            if (markers.get(i).equals(name)) {
                markers.remove(i);
            }
        }
        mMap.clear();
    }

    /**
     * Listener that listens to changes in the location and sends them to the ServerController
     */
    private class LocList implements LocationListener {
        @Override
        public void onLocationChanged(Location newLocation) {
            location = newLocation;
            serverController.setLocation(location);
            serverController.setID(ID);
            serverController.setTimers();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latlng = new LatLng(latitude, longitude);
            if (latlng != null) {
                addMarker(latlng, "My marker");
            }

        }

        /**
         * Not used
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        /**
         * Not used
         */
        @Override
        public void onProviderEnabled(String provider) {
        }

        /**
         * Not used
         */
        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    /**
     * Adds an new marker to the map
     *
     * @param latlng      - The position of the user.
     * @param markerTitle - The title of the user.
     */
    public void addMarker(LatLng latlng, String markerTitle) {
        MarkerOptions mo;
        mo = new MarkerOptions().position(latlng).title(markerTitle);
        markers.add(mo);
        for (int i = 0; i < markers.size(); i++) {
            mMap.addMarker(markers.get(i));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        for (MarkerOptions mo : markers) {
            mMap.addMarker(mo);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mo.getPosition()));
        }
    }

    /**
     * Called when the user exits the map.
     * Unregisters the user from the server.
     */
    public void onPause() {
        super.onPause();
        serverController.unregister();
    }
}
