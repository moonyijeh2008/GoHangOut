package com.example.gohangout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

public class ExampleBottomSheetDialog extends BottomSheetDialogFragment {

    private List<MyPlace> placeList;
    private GoogleMap map;
    private PlaceAdapter adapter;
    private PlaceDataSource placeDataSource;

    public ExampleBottomSheetDialog(List<MyPlace> placeList, GoogleMap map, PlaceDataSource placeDataSource) {
        this.placeList = placeList;
        this.map = map;
        this.placeDataSource = placeDataSource;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize PlaceAdapter
        adapter = new PlaceAdapter(placeList, map, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void showDeleteConfirmationDialog(MyPlace place, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("삭제 확인")
                .setMessage("이 항목을 삭제하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> {
                    // Remove item from list and notify adapter
                    placeList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                    // Delete from data source
                    placeDataSource.deletePlace(place.getId());
                })
                .setNegativeButton("취소", null)
                .show();
    }
}