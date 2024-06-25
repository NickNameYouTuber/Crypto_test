package com.nicorp.crypto_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {

    private Context context;
    private List<Transaction> transactions;
    private int parentWidth;

    public TransactionsAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        parentWidth = parent.getMeasuredWidth();
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.ivTransactionLogo.setImageResource(transaction.getLogo());
        holder.tvTransactionName.setText(transaction.getName());
        holder.tvTransactionAmount.setText(transaction.getAmount());

        // Устанавливаем ширину и высоту для соотношения 1/3
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = parentWidth / 2;
        layoutParams.height = (int) (layoutParams.width * (1.0 / 3.0));
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTransactionLogo;
        TextView tvTransactionName, tvTransactionAmount;
        CardView cardView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTransactionLogo = itemView.findViewById(R.id.ivTransactionLogo);
            tvTransactionName = itemView.findViewById(R.id.tvTransactionName);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}
