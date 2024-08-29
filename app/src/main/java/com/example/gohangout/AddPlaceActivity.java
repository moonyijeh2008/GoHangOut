package com.example.gohangout;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPlaceActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private EditText editTitle, editDesc, editLocation, editDetails;
    private PlaceDataSource placeDataSource;
    private Button btnSave;
    private ImageView imageCamera, imageGallery;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        editTitle = findViewById(R.id.editTitle);
        editDesc = findViewById(R.id.editDesc);
        editLocation = findViewById(R.id.editLocation);
        editDetails = findViewById(R.id.editDetails);
        btnSave = findViewById(R.id.btnSave);
        imageCamera = findViewById(R.id.imageCamera);
        imageGallery = findViewById(R.id.imageGallery);

        placeDataSource = new PlaceDataSource(this);
        placeDataSource.open();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlace();
                finish(); // 저장 후 액티비티 종료
            }
        });

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermissionAndOpenCamera();
            }
        });

        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPickFromGalleryIntent();
            }
        });
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Permission already granted, open the camera
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the camera
                dispatchTakePictureIntent();
            } else {
                // Permission denied, inform the user
                Toast.makeText(this, "Camera permission is required to take pictures.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("AddPlaceActivity", "Error creating image file", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.gohangout.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void dispatchPickFromGalleryIntent() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Handle camera image
                File file = new File(currentPhotoPath);
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    bitmap = rotateImageIfRequired(bitmap, currentPhotoPath); // Correct the orientation
                    imageCamera.setImageBitmap(bitmap); // Replace camera icon with the captured image
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // Handle gallery image
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    imageGallery.setImageURI(selectedImage); // Replace gallery icon with the selected image
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Bitmap rotateImageIfRequired(Bitmap img, String path) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            return img;
        }

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
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

    private void savePlace() {
        String title = editTitle.getText().toString();
        String desc = editDesc.getText().toString();
        String location = editLocation.getText().toString();
        String details = editDetails.getText().toString();

        // Log the user-entered location
        Log.d(TAG, "User-entered location: " + location);

        double latitude = 0.0;
        double longitude = 0.0;

        // Use Geocoder to get latitude and longitude from the location string
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                latitude = address.getLatitude();
                longitude = address.getLongitude();

                // Log the full address details
                Log.d(TAG, "Address found: " + address.getAddressLine(0));
                Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
                Log.d(TAG, "Full address details: "
                        + "\nCountry: " + address.getCountryName()
                        + "\nLocality: " + address.getLocality()
                        + "\nPostal Code: " + address.getPostalCode()
                        + "\nThoroughfare: " + address.getThoroughfare()
                        + "\nSubThoroughfare: " + address.getSubThoroughfare());
            } else {
                Log.e(TAG, "No location found for: " + location);
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoding failed", e);
        }

        // Save the place with the obtained coordinates
        placeDataSource.savePlace(title, desc, location, latitude, longitude, details);
    }

    @Override
    protected void onDestroy() {
        placeDataSource.close();
        super.onDestroy();
    }
}