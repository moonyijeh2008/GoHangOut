package com.example.gohangout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

public class ExampleBottomSheetDialog extends BottomSheetDialogFragment {

    private List<MyPlace> placeList;
    private GoogleMap map;

    public ExampleBottomSheetDialog(List<MyPlace> placeList, GoogleMap map) {
        this.placeList = placeList;
        this.map = map;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // PlaceAdapter를 사용하여 RecyclerView에 장소 목록을 표시
        PlaceAdapter adapter = new PlaceAdapter(placeList, map);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ensure BottomSheetBehavior is set correctly
//        View bottomSheet = getView().findViewById(R.id.bottom_sheet);
//        if (bottomSheet != null) {
//            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
//        }
    }
}