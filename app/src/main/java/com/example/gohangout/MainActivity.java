package com.example.gohangout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker userLocationMarker;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location currentLocation;

    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private List<Marker> poiMarkers = new ArrayList<>();
    private RecyclerView poiListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // BottomSheetBehavior 초기화
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // 처음에 BottomSheet를 숨겨진 상태로 설정
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        poiListRecyclerView = findViewById(R.id.poi_list);
        poiListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // RecyclerView 터치 리스너 추가
        poiListRecyclerView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        // SupportMapFragment를 사용하여 맵을 초기화합니다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 위치 제공자 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 위치 업데이트 콜백 설정
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "위도: " + location.getLatitude() + ", 경도: " + location.getLongitude());
                    currentLocation = location;
                    updateLocationMarker(location);
                }
            }
        };

        // BottomSheet의 상태 변경 리스너 설정
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.d(TAG, "BottomSheet 상태 변경: " + newState);
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    poiListRecyclerView.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    poiListRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d(TAG, "BottomSheet 슬라이드 중, 오프셋: " + slideOffset);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = location;
                        updateLocationMarker(location);
                    }
                });

        startLocationUpdates();

        mMap.setOnMapClickListener(latLng -> {
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            poiMarkers.add(marker);
            updatePOIList();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mMap.setOnMarkerClickListener(marker -> {
            if ("userLocation".equals(marker.getTag())) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                return true;
            }
            return false;
        });
    }

    private void updatePOIList() {
        PoiAdapter adapter = new PoiAdapter(poiMarkers);
        poiListRecyclerView.setAdapter(adapter);
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void updateLocationMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            userLocationMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("현재 위치"));
            userLocationMarker.setTag("userLocation");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        } else {
            userLocationMarker.setPosition(userLocation);
            userLocationMarker.setVisible(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}