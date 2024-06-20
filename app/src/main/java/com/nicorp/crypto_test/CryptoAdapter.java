package com.nicorp.crypto_test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder> {

    private List<CryptoItem> cryptoItems;

    public void setCryptoItems(List<CryptoItem> cryptoItems) {
        this.cryptoItems = cryptoItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CryptoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crypto_item, parent, false);
        return new CryptoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoViewHolder holder, int position) {
        CryptoItem cryptoItem = cryptoItems.get(position);
        holder.cryptoImageView.setImageResource(cryptoItem.getImageResource());
        holder.cryptoNameTextView.setText(cryptoItem.getName());
        holder.cryptoPriceTextView.setText(cryptoItem.getPrice());
    }

    @Override
    public int getItemCount() {
        return cryptoItems != null ? cryptoItems.size() : 0;
    }

    static class CryptoViewHolder extends RecyclerView.ViewHolder {
        ImageView cryptoImageView;
        TextView cryptoNameTextView;
        TextView cryptoPriceTextView;

        CryptoViewHolder(@NonNull View itemView) {
            super(itemView);
            cryptoImageView = itemView.findViewById(R.id.cryptoImageView);
            cryptoNameTextView = itemView.findViewById(R.id.cryptoNameTextView);
            cryptoPriceTextView = itemView.findViewById(R.id.cryptoPriceTextView);
        }
    }
}