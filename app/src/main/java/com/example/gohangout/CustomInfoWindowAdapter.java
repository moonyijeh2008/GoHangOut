package com.example.gohangout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.File;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "CustomInfoWindowAdapter";
    private final View mWindow;
    private final MapActivity context;

    public CustomInfoWindowAdapter(MapActivity context) {
        this.context = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view) {
        ImageView cameraImageView = view.findViewById(R.id.info_camera_image);
        ImageView userImageView = view.findViewById(R.id.info_user_image);
        TextView infoTitle = view.findViewById(R.id.info_title);
        TextView infoDescription = view.findViewById(R.id.info_description);

        MyPlace place = (MyPlace) marker.getTag(); // Assuming marker has a MyPlace tag
        if (place != null) {
            Log.d(TAG, "MyPlace object found with title: " + place.getTitle());

            // Load the camera image
            String cameraImagePath = place.getCameraImagePath();
            if (cameraImagePath != null && !cameraImagePath.isEmpty()) {
                loadImageFromPath(cameraImagePath, cameraImageView, "Camera");
            } else {
                Log.w(TAG, "Camera image path is null or empty. Path: " + cameraImagePath);
                cameraImageView.setImageResource(R.drawable.ic_launcher_background);
            }

            // Load and set the user-selected image
            String userImagePath = place.getUserImagePath();
            if (userImagePath != null && !userImagePath.isEmpty()) {
                Log.d(TAG, "Loading user-selected image from path: " + userImagePath);
                loadImageFromPath(userImagePath, userImageView, "User");
            } else {
                Log.w(TAG, "User image path is null or empty. Path: " + userImagePath);
                userImageView.setImageResource(R.drawable.ic_launcher_background);
            }

            // Set title and description
            infoTitle.setText(place.getTitle());
            infoDescription.setText(place.getDesc());
        } else {
            Log.e(TAG, "MyPlace object not found or marker tag is null.");
        }
    }

    private void loadImageFromPath(String imagePath, ImageView imageView, String imageType) {
        Log.d(TAG, imageType + " image path: " + imagePath);
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Log.d(TAG, imageType + " image file exists: " + imgFile.getAbsolutePath());
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            if (bitmap != null) {
                Log.d(TAG, imageType + " Bitmap loaded successfully.");
                imageView.setImageBitmap(bitmap);
            } else {
                Log.e(TAG, "Failed to decode " + imageType + " Bitmap from file.");
                imageView.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            Log.e(TAG, imageType + " image file does not exist at path: " + imgFile.getAbsolutePath());
            imageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}