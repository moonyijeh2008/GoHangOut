package com.example.gohangout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PlaceDataSource {

    private SQLiteDatabase database;
    private PlaceDatabaseHelper dbHelper;
    private String[] allColumns = {
            PlaceDatabaseHelper.COLUMN_ID,
            PlaceDatabaseHelper.COLUMN_TITLE,
            PlaceDatabaseHelper.COLUMN_DESC,
            PlaceDatabaseHelper.COLUMN_LOCATION,
            PlaceDatabaseHelper.COLUMN_LATITUDE,
            PlaceDatabaseHelper.COLUMN_LONGITUDE,
            PlaceDatabaseHelper.COLUMN_DETAILS,
            PlaceDatabaseHelper.COLUMN_CAMERA_IMAGE_PATH, // 카메라 이미지 경로 컬럼 추가
            PlaceDatabaseHelper.COLUMN_USER_IMAGE_PATH   // 사용자 이미지 경로 컬럼 추가
    };

    public PlaceDataSource(Context context) {
        dbHelper = new PlaceDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // 이미지 경로 추가된 savePlace 메서드
    public void savePlace(String title, String desc, String location, double latitude, double longitude, String details, String cameraImagePath, String userImagePath) {
        ContentValues values = new ContentValues();
        values.put(PlaceDatabaseHelper.COLUMN_TITLE, title);
        values.put(PlaceDatabaseHelper.COLUMN_DESC, desc);
        values.put(PlaceDatabaseHelper.COLUMN_LOCATION, location);
        values.put(PlaceDatabaseHelper.COLUMN_LATITUDE, latitude);
        values.put(PlaceDatabaseHelper.COLUMN_LONGITUDE, longitude);
        values.put(PlaceDatabaseHelper.COLUMN_DETAILS, details);
        values.put(PlaceDatabaseHelper.COLUMN_CAMERA_IMAGE_PATH, cameraImagePath); // 카메라 이미지 경로 추가
        values.put(PlaceDatabaseHelper.COLUMN_USER_IMAGE_PATH, userImagePath);     // 사용자 이미지 경로 추가

        database.insert(PlaceDatabaseHelper.TABLE_PLACES, null, values);
    }

    public List<MyPlace> getAllPlaces() {
        List<MyPlace> places = new ArrayList<>();

        Cursor cursor = database.query(PlaceDatabaseHelper.TABLE_PLACES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyPlace place = cursorToPlace(cursor);
            places.add(place);
            cursor.moveToNext();
        }
        cursor.close();
        return places;
    }

    private MyPlace cursorToPlace(Cursor cursor) {
        MyPlace place = new MyPlace();
        place.setId(cursor.getLong(0));
        place.setTitle(cursor.getString(1));
        place.setDesc(cursor.getString(2));
        place.setLocation(cursor.getString(3));
        place.setLatitude(cursor.getDouble(4));
        place.setLongitude(cursor.getDouble(5));
        place.setDetails(cursor.getString(6));
        place.setCameraImagePath(cursor.getString(7)); // 카메라 이미지 경로 설정
        place.setUserImagePath(cursor.getString(8));   // 사용자 이미지 경로 설정
        return place;
    }

    // 새로운 메서드: 항목 삭제
    public void deletePlace(long placeId) {
        database.delete(PlaceDatabaseHelper.TABLE_PLACES,
                PlaceDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(placeId)});
    }
}