//package com.example.gohangout.map;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.gohangout.R;
//import com.example.gohangout.database.MyPlace;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.model.LatLng;
//
//import java.util.List;
//
//public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
//
//    private List<MyPlace> places;
//    private GoogleMap map;
//    private ExampleBottomSheetDialog bottomSheetDialog;
//
//    public PlaceAdapter(List<MyPlace> places, GoogleMap map, ExampleBottomSheetDialog bottomSheetDialog) {
//        this.places = places;
//        this.map = map;
//        this.bottomSheetDialog = bottomSheetDialog;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_place, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        MyPlace place = places.get(position);
//        holder.titleTextView.setText(place.getTitle());
//        holder.locationTextView.setText(place.getLocation());
//
//        holder.itemView.setOnClickListener(v -> {
//            LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
//        });
//
//        holder.deleteImageView.setOnClickListener(v -> {
//            bottomSheetDialog.showDeleteConfirmationDialog(place, position);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return places.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView titleTextView;
//        TextView locationTextView;
//        ImageView deleteImageView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            titleTextView = itemView.findViewById(R.id.text_place_title);
//            locationTextView = itemView.findViewById(R.id.text_place_location);
//            deleteImageView = itemView.findViewById(R.id.icon_delete);
//        }
//    }
//}

package com.example.gohangout.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gohangout.R;
import com.example.gohangout.database.MyPlace;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private List<MyPlace> places;
    private GoogleMap map;
    private ExampleBottomSheetDialog bottomSheetDialog;

    public PlaceAdapter(List<MyPlace> places, GoogleMap map, ExampleBottomSheetDialog bottomSheetDialog) {
        this.places = places;
        this.map = map;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyPlace place = places.get(position);
        holder.titleTextView.setText(place.getTitle());
        holder.locationTextView.setText(place.getLocation());

        // 아이템 클릭 시 지도 카메라 이동
        holder.itemView.setOnClickListener(v -> {
            LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        });

        // 삭제 버튼 클릭 시 삭제 다이얼로그 호출
        holder.deleteImageView.setOnClickListener(v -> {
            bottomSheetDialog.showDeleteConfirmationDialog(place, position);
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView locationTextView;
        ImageView deleteImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_place_title);
            locationTextView = itemView.findViewById(R.id.text_place_location);
            deleteImageView = itemView.findViewById(R.id.icon_delete);
        }
    }
}