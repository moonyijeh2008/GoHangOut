package com.example.gohangout;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import androidx.loader.content.CursorLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPlaceActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_PERMISSIONS = 101;

    private EditText editTitle, editDesc, editLocation, editDetails;
    private PlaceDataSource placeDataSource;
    private Button btnSave;
    private ImageView imageCamera, imageGallery;
    private String currentPhotoPath;
    private String userImagePath;

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

        btnSave.setOnClickListener(v -> {
            savePlace();
            finish();
        });

        imageCamera.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());

        imageGallery.setOnClickListener(v -> dispatchPickFromGalleryIntent());
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void checkStoragePermission() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        // 필요한 권한을 확인하고, 요청할 필요가 있는 경우
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            // 권한이 허용되지 않은 경우 요청
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission is required to take pictures.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.gohangout.fileprovider", photoFile);
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
                File file = new File(currentPhotoPath);
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    bitmap = rotateImageIfRequired(bitmap, currentPhotoPath);
                    imageCamera.setImageBitmap(bitmap);
                    Log.d(TAG, "Camera image saved at: " + currentPhotoPath);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    imageGallery.setImageURI(selectedImage);
                    userImagePath = getRealPathFromURI(selectedImage);
                    Log.d(TAG, "User-selected image path: " + userImagePath);
                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return null;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

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

        checkStoragePermission();

        String title = editTitle.getText().toString();
        String desc = editDesc.getText().toString();
        String location = editLocation.getText().toString();
        String details = editDetails.getText().toString();

        Log.d(TAG, "User-entered location: " + location);

        double latitude = 0.0;
        double longitude = 0.0;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                latitude = address.getLatitude();
                longitude = address.getLongitude();

                Log.d(TAG, "Address found: " + address.getAddressLine(0));
                Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
            } else {
                Log.e(TAG, "No location found for: " + location);
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoding failed", e);
        }

        MyPlace place = new MyPlace();
        place.setTitle(title);
        place.setDesc(desc);
        place.setLocation(location);
        place.setLatitude(latitude);
        place.setLongitude(longitude);
        place.setDetails(details);

        if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
            Log.d(TAG, "Setting camera image path: " + currentPhotoPath);
            place.setCameraImagePath(currentPhotoPath);
        } else {
            Log.w(TAG, "Camera image path is null or empty when saving MyPlace.");
        }

        if (userImagePath != null && !userImagePath.isEmpty()) {
            Log.d(TAG, "Setting user image path: " + userImagePath);
            place.setUserImagePath(userImagePath);
        } else {
            Log.w(TAG, "User image path is null or empty when saving MyPlace.");
        }

        // 수정된 savePlace 호출
        placeDataSource.savePlace(
                place.getTitle(),
                place.getDesc(),
                place.getLocation(),
                place.getLatitude(),
                place.getLongitude(),
                place.getDetails(),
                place.getCameraImagePath(), // 카메라 이미지 경로
                place.getUserImagePath()    // 사용자 이미지 경로
        );
    }

    @Override
    protected void onDestroy() {
        placeDataSource.close();
        super.onDestroy();
    }
}