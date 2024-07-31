package com.nicorp.crypto_test.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.objects.TransactionItem;

import java.util.List;

public class TransactionByAdapter extends RecyclerView.Adapter<TransactionByAdapter.TransactionViewHolder> {

    private List<TransactionItem> transactionItems;
    private OnItemClickListener onItemClickListener;

    public TransactionByAdapter(List<TransactionItem> transactionItems, OnItemClickListener onItemClickListener) {
        this.transactionItems = transactionItems;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_by_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionItem item = transactionItems.get(position);
        holder.itemText.setText(item.getText());
        holder.itemImage.setImageResource(item.getImageResId());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item.getTransactionType()));
    }

    @Override
    public int getItemCount() {
        return transactionItems.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemText;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemText = itemView.findViewById(R.id.itemText);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String transactionType);
    }
}
