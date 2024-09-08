package com.example.gohangout.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.gohangout.CustomInfoWindowAdapter;
import com.example.gohangout.R;
import com.example.gohangout.database.MyPlace;
import com.example.gohangout.database.PlaceDataSource;
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
    private GoogleMap mMap;
    private PlaceDataSource placeDataSource;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 지도 프래그먼트 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 로컬 데이터베이스 초기화 및 연결
        placeDataSource = new PlaceDataSource(this);
        placeDataSource.open();

        // BottomSheet 핸들 초기화
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
        // 로컬 데이터베이스에서 장소 목록을 가져와 다이얼로그에 표시
        List<MyPlace> places = placeDataSource.getAllPlaces();
        ExampleBottomSheetDialog dialog = new ExampleBottomSheetDialog(places, mMap, placeDataSource);
        dialog.show(getSupportFragmentManager(), "exampleBottomSheetDialog");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

        // 위치 권한 확인 및 현재 위치 가져오기
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        // 로컬 데이터베이스에서 장소를 가져와 마커 추가
        List<MyPlace> myPlaces = placeDataSource.getAllPlaces();
        for (MyPlace myPlace : myPlaces) {
            addMarkerForPlace(myPlace);
        }

        // 마커 클릭 리스너 수정: 새 액티비티로 이동
        mMap.setOnMarkerClickListener(marker -> {
            MyPlace place = (MyPlace) marker.getTag();
            if (place != null) {
                Intent intent = new Intent(MapActivity.this, PlaceDetailActivity.class);
                intent.putExtra("place", place); // MyPlace 객체 전달
                startActivity(intent);
            }
            return true;
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // 현재 위치 가져오기
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
                        .title(myPlace.getTitle()));
                marker.setTag(myPlace); // MyPlace 객체를 태그로 설정하여 Intent로 전달 가능
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