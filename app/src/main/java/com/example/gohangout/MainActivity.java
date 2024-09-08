package com.example.gohangout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gohangout.add.AddFragment;
import com.example.gohangout.map.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 초기화면을 홈 프래그먼트로 설정
        replaceFragment(new HomeFragment());

        // BottomNavigationView의 아이템 선택 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    replaceFragment(new HomeFragment());
                    Log.d("MainActivity", "Home selected");
                    return true;
                } else if (itemId == R.id.navigation_schedule) {
                    replaceFragment(new ScheduleFragment());
                    Log.d("MainActivity", "Schedule selected");
                    return true;
                } else if (itemId == R.id.navigation_map) {
                    replaceFragment(new MapFragment());
                    Log.d("MainActivity", "Map selected");
                    return true;
                } else if (itemId == R.id.navigation_add) {
                    replaceFragment(new AddFragment());
                    Log.d("MainActivity", "Add selected");
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    replaceFragment(new ProfileFragment());
                    Log.d("MainActivity", "Profile selected");
                    return true;
                }

                return false;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}