package com.example.gohangout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class ExampleBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = "ExampleBottomSheetDialog";

    private List<MyPlace> placeItems;  // List<PlaceItem> 대신 List<MyPlace>로 변경
    private RecyclerView poiListRecyclerView;
    private GoogleMap googleMap;

    public ExampleBottomSheetDialog(List<MyPlace> placeItems, GoogleMap googleMap) {
        this.placeItems = placeItems;
        this.googleMap = googleMap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        TextView bottomSheetTitle = view.findViewById(R.id.bottom_sheet_title);
        poiListRecyclerView = view.findViewById(R.id.poi_list);

        poiListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // MyPlace에 맞게 PlaceAdapter 사용
        PlaceAdapter adapter = new PlaceAdapter(placeItems, googleMap);
        poiListRecyclerView.setAdapter(adapter);

        logPlaceItems();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logPlaceItems();
    }

    private void logPlaceItems() {
        Log.d(TAG, "Place items in bottom sheet:");
        for (MyPlace item : placeItems) {  // PlaceItem 대신 MyPlace 사용
            Log.d(TAG, "Name: " + item.getTitle() + ", Location: " + item.getLocation());
        }
    }
}