package com.example.gohangout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddPlaceActivity extends AppCompatActivity {

    private EditText editTitle, editDesc, editLocation, editDetails;
    private PlaceDataSource placeDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        editTitle = findViewById(R.id.editTitle);
        editDesc = findViewById(R.id.editDesc);
        editLocation = findViewById(R.id.editLocation);
        editDetails = findViewById(R.id.editDetails);
        Button btnSave = findViewById(R.id.btnSave);

        placeDataSource = new PlaceDataSource(this);
        placeDataSource.open();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlace();
                finish(); // 저장 후 액티비티 종료
            }
        });
    }

    private void savePlace() {
        String title = editTitle.getText().toString();
        String desc = editDesc.getText().toString();
        String location = editLocation.getText().toString();
        String details = editDetails.getText().toString();

        // 위치를 지오코딩해서 위도와 경도를 구해야 합니다. 여기서는 임의의 값을 사용하겠습니다.
        // 실제로는 Geocoder를 사용해 위치를 변환하세요.
        double latitude = 37.7749; // 예시: 샌프란시스코의 위도
        double longitude = -122.4194; // 예시: 샌프란시스코의 경도

        placeDataSource.savePlace(title, desc, location, latitude, longitude, details);
    }

    @Override
    protected void onDestroy() {
        placeDataSource.close();
        super.onDestroy();
    }
}