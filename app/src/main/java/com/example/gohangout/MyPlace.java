package com.example.gohangout;

public class MyPlace {
    private long id;
    private String title;
    private String desc;
    private String location;
    private double latitude;
    private double longitude;
    private String details;
    private String cameraImagePath; // 카메라로 찍은 이미지 경로
    private String userImagePath; // 사용자 선택 이미지 경로
    private boolean isCameraImage; // 카메라 이미지 여부를 구분하기 위한 필드

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