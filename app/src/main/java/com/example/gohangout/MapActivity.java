package com.example.gohangout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private PlaceDataSource placeDataSource;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        placeDataSource = new PlaceDataSource(this);
        placeDataSource.open();

        // Initialize BottomSheet handle touch listener
        TextView bottomSheetHandle = findViewById(R.id.bottom_sheet_handle);
        bottomSheetHandle.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float startY;
            private boolean dragging;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        dragging = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float dx = Math.abs(event.getX() - startX);
                        float dy = Math.abs(event.getY() - startY);
                        if (dx > 10 || dy > 10) {
                            dragging = true;
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (dragging) {
                            showBottomDialog();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    public void showBottomDialog() {
        List<MyPlace> places = placeDataSource.getAllPlaces();
        ExampleBottomSheetDialog dialog = new ExampleBottomSheetDialog(places, mMap, placeDataSource);
        dialog.show(getSupportFragmentManager(), "exampleBottomSheetDialog");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the custom InfoWindowAdapter
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        List<MyPlace> myPlaces = placeDataSource.getAllPlaces();
        for (MyPlace myPlace : myPlaces) {
            addMarkerForPlace(myPlace);
        }

        mMap.setOnMarkerClickListener(marker -> {
            // Show custom info window
            marker.showInfoWindow(); // This triggers the InfoWindow to display
            return true;
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("현재 위치"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    }
                });
    }

    private void addMarkerForPlace(MyPlace myPlace) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName(myPlace.getLocation(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(myPlace.getTitle())); // Sets the title for the marker
                ((Marker) marker).setTag(myPlace); // Sets the MyPlace object as a tag to the marker
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        placeDataSource.close();
        super.onDestroy();
    }
}