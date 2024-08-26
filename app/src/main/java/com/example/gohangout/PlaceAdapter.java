package com.example.gohangout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private List<MyPlace> places;
    private GoogleMap map;

    public PlaceAdapter(List<MyPlace> places, GoogleMap map) {
        this.places = places;
        this.map = map;
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

        holder.itemView.setOnClickListener(v -> {
            LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView locationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_place_title);
            locationTextView = itemView.findViewById(R.id.text_place_location);
        }
    }
}