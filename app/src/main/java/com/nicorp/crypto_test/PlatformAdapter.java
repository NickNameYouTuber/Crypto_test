package com.nicorp.crypto_test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlatformAdapter extends RecyclerView.Adapter<PlatformAdapter.PlatformViewHolder> {

    private List<Platform> platformList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Platform platform);
    }

    public PlatformAdapter(List<Platform> platformList, OnItemClickListener listener) {
        this.platformList = platformList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlatformViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.platform_button, parent, false);
        return new PlatformViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatformViewHolder holder, int position) {
        Platform platform = platformList.get(position);
        holder.textViewPlatform.setText(platform.getName());
        holder.imageViewPlatform.setImageResource(platform.getImageResId());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(platform));
    }

    @Override
    public int getItemCount() {
        return platformList.size();
    }

    static class PlatformViewHolder extends RecyclerView.ViewHolder {

        TextView textViewPlatform;
        ImageView imageViewPlatform;

        public PlatformViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPlatform = itemView.findViewById(R.id.textViewPlatform);
            imageViewPlatform = itemView.findViewById(R.id.imageViewPlatform);
        }
    }
}
