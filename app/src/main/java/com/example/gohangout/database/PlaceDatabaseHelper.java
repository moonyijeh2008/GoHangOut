package com.example.gohangout.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaceDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "places.db";
    private static final int DATABASE_VERSION = 2; // 버전을 증가시킵니다.

    public static final String TABLE_PLACES = "places";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_DETAILS = "details";
    public static final String COLUMN_CAMERA_IMAGE_PATH = "camera_image_path"; // 카메라 이미지 경로
    public static final String COLUMN_USER_IMAGE_PATH = "user_image_path"; // 사용자 이미지 경로

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_PLACES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DESC + " TEXT, "
            + COLUMN_LOCATION + " TEXT, "
            + COLUMN_LATITUDE + " REAL, "
            + COLUMN_LONGITUDE + " REAL, "
            + COLUMN_DETAILS + " TEXT, "
            + COLUMN_CAMERA_IMAGE_PATH + " TEXT, "
            + COLUMN_USER_IMAGE_PATH + " TEXT);";

    public PlaceDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 기존 테이블을 삭제하고 새로 생성하여 새로운 컬럼을 추가합니다.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        onCreate(db);
    }
}