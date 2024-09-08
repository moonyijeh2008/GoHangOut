package com.example.gohangout.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gohangout.R;
import com.example.gohangout.database.MyPlace;

import java.io.File;
import java.io.IOException;

public class PlaceDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView locationTextView;
    private ImageView cameraImageView;
    private ImageView userImageView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        titleTextView = findViewById(R.id.detail_title);
        descriptionTextView = findViewById(R.id.detail_description);
        locationTextView = findViewById(R.id.detail_location);
        cameraImageView = findViewById(R.id.detail_camera_image);
        userImageView = findViewById(R.id.detail_user_image);

        // Intent를 통해 전달된 MyPlace 객체 받기
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("place")) {
            MyPlace place = intent.getParcelableExtra("place");

            // MyPlace 객체의 데이터 설정
            if (place != null) {
                titleTextView.setText(place.getTitle());
                descriptionTextView.setText(place.getDesc());
                locationTextView.setText(place.getLocation());

                // 카메라 이미지가 있는 경우 로드
                if (place.getCameraImagePath() != null) {
                    loadImageFromPath(place.getCameraImagePath(), cameraImageView);
                }

                // 사용자 이미지가 있는 경우 로드
                if (place.getUserImagePath() != null) {
                    loadImageFromPath(place.getUserImagePath(), userImageView);
                }
            }
        }
    }

    // 이미지 경로에서 이미지를 로드하는 메서드
    private void loadImageFromPath(String imagePath, ImageView imageView) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            bitmap = rotateImageIfRequired(bitmap, imgFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }

    // 이미지의 EXIF 데이터를 읽어 회전 각도에 따라 이미지를 회전시키는 메서드
    private Bitmap rotateImageIfRequired(Bitmap img, String path) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return img;
        }
    }

    // 이미지를 지정된 각도로 회전시키는 메서드
    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }
}