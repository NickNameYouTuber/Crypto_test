package com.nicorp.crypto_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

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
        setCardViewBackground(holder.cardView, holder.ivTransactionLogo, holder.tvTransactionName, holder.tvTransactionAmount); // Set background color and text color based on image
        holder.tvTransactionName.setText(transaction.getName());
        holder.tvTransactionAmount.setText(transaction.getAmount());

        // Устанавливаем ширину и высоту для соотношения 1/3
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = (parentWidth - dpToPx(20) * (2 - 1)) / 2; // Adjust this calculation if needed
        layoutParams.height = (int) (layoutParams.width * (11.0 / 30.0));
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

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView TransactionName, TextView TransactionAmount) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@NonNull Palette palette) {
                int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, android.R.color.white));
                cardView.setCardBackgroundColor(dominantColor);

                // Determine text color based on background brightness
                int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, android.R.color.white) : ContextCompat.getColor(context, R.color.text_color_dark);
                TransactionName.setTextColor(textColor);
                TransactionAmount.setTextColor(textColor);
            }
        });
    }

    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5; // Adjust this threshold as needed
    }

    private int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
