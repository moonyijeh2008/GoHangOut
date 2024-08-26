package com.example.gohangout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private PlaceDataSource placeDataSource;
    private FusedLocationProviderClient fusedLocationClient;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView textCurrentLocation;
    private RecyclerView placesRecyclerView;
    private PlaceAdapter placeAdapter;
    private TextView textPlaceTitle;
    private TextView textPlaceLocation;

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

        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        textCurrentLocation = findViewById(R.id.text_current_location);
        textPlaceTitle = findViewById(R.id.text_place_title);
        textPlaceLocation = findViewById(R.id.text_place_location);

        placesRecyclerView = findViewById(R.id.places_recycler_view);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        List<MyPlace> myPlaces = placeDataSource.getAllPlaces();
        placeAdapter = new PlaceAdapter(myPlaces, mMap);
        placesRecyclerView.setAdapter(placeAdapter);

        for (MyPlace myPlace : myPlaces) {
            addMarkerForPlace(myPlace);
        }

        mMap.setOnMarkerClickListener(marker -> {
            showBottomSheet(marker);
            return false;
        });

        mMap.setOnCameraMoveListener(() -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
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

                        textCurrentLocation.setText(String.format(Locale.getDefault(),
                                "현재 위치: (%.5f, %.5f)", location.getLatitude(), location.getLongitude()));
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
                mMap.addMarker(new MarkerOptions().position(location).title(myPlace.getTitle()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showBottomSheet(Marker marker) {
        textPlaceTitle.setText(marker.getTitle());
        textPlaceLocation.setText(marker.getPosition().toString());

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        marker.showInfoWindow();

        textPlaceTitle.setOnClickListener(v -> {
            Log.d(TAG, "Title clicked: " + marker.getTitle());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        textPlaceLocation.setOnClickListener(v -> {
            Log.d(TAG, "Location clicked: " + marker.getPosition().toString());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
    }

    @Override
    protected void onDestroy() {
        placeDataSource.close();
        super.onDestroy();
    }
}