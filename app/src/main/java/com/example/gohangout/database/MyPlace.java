package com.example.gohangout.database;

import android.os.Parcel;
import android.os.Parcelable;

public class MyPlace implements Parcelable {
    private long id;
    private String title;
    private String desc;
    private String location;
    private double latitude;
    private double longitude;
    private String details;
    private String cameraImagePath; // 카메라로 찍은 이미지 경로
    private String userImagePath;   // 사용자 선택 이미지 경로
    private boolean isCameraImage;  // 카메라 이미지 여부를 구분하기 위한 필드

    // 기본 생성자
    public MyPlace() {}

    // Parcelable 생성자
    protected MyPlace(Parcel in) {
        id = in.readLong();
        title = in.readString();
        desc = in.readString();
        location = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        details = in.readString();
        cameraImagePath = in.readString();
        userImagePath = in.readString();
        isCameraImage = in.readByte() != 0; // boolean 값을 읽음
    }

    // Parcelable 구현에 필요한 메서드
    public static final Creator<MyPlace> CREATOR = new Creator<MyPlace>() {
        @Override
        public MyPlace createFromParcel(Parcel in) {
            return new MyPlace(in);
        }

        @Override
        public MyPlace[] newArray(int size) {
            return new MyPlace[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(desc);
        parcel.writeString(location);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(details);
        parcel.writeString(cameraImagePath);
        parcel.writeString(userImagePath);
        parcel.writeByte((byte) (isCameraImage ? 1 : 0)); // boolean 값을 저장
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCameraImagePath() {
        return cameraImagePath;
    }

    public void setCameraImagePath(String cameraImagePath) {
        this.cameraImagePath = cameraImagePath;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
    }

    public boolean isCameraImage() {
        return isCameraImage;
    }

    public void setCameraImage(boolean cameraImage) {
        isCameraImage = cameraImage;
    }
}