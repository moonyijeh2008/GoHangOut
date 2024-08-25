package com.example.gohangout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.model.Marker;
import java.util.List;

public class PoiAdapter extends RecyclerView.Adapter<PoiAdapter.PoiViewHolder> {

    private final List<Marker> poiList; // Marker 리스트로 변경

    // 생성자에서 Marker 리스트를 받아오도록 수정
    public PoiAdapter(List<Marker> poiList) {
        this.poiList = poiList;
    }

    @NonNull
    @Override
    public PoiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new PoiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PoiViewHolder holder, int position) {
        Marker marker = poiList.get(position);
        holder.textView.setText(marker.getTitle()); // Marker의 타이틀을 텍스트뷰에 설정
    }

    @Override
    public int getItemCount() {
        return poiList.size();
    }

    static class PoiViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        PoiViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}