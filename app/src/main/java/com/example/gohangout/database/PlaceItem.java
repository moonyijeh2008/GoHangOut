package com.example.gohangout.database;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaceItem implements Parcelable {
    private String name;
    private String address;

    public PlaceItem(String name, String address) {
        this.name = name;
        this.address = address;
    }

    protected PlaceItem(Parcel in) {
        name = in.readString();
        address = in.readString();
    }

    public static final Creator<PlaceItem> CREATOR = new Creator<PlaceItem>() {
        @Override
        public PlaceItem createFromParcel(Parcel in) {
            return new PlaceItem(in);
        }

        @Override
        public PlaceItem[] newArray(int size) {
            return new PlaceItem[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}