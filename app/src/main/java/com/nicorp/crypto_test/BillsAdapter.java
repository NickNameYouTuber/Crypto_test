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
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillViewHolder> {

    private Context context;
    private List<Bill> bills;
    private int parentWidth;

    public BillsAdapter(Context context, List<Bill> bills) {
        this.context = context;
        this.bills = bills;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bill, parent, false);
        parentWidth = parent.getMeasuredWidth();
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.ivBillLogo.setImageResource(bill.getLogo());
        setCardViewBackground(holder.cardView, holder.ivBillLogo, holder.tvBillTitle, holder.tvBillAmount, holder.tvBillUsdAmount); // Set background color and text color based on image
        holder.tvBillTitle.setText(bill.getTitle());
        holder.tvBillAmount.setText(bill.getAmount());
        holder.tvBillUsdAmount.setText(bill.getUsdAmount());

        // Устанавливаем ширину и высоту для соотношения 5/9
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = (parentWidth - dpToPx(20) * (2 - 1)) / 2; // Adjust this calculation if needed
        layoutParams.height = (int) (layoutParams.width * (5.0 / 9.0));
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBillLogo;
        TextView tvBillTitle, tvBillAmount, tvBillUsdAmount;
        CardView cardView;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBillLogo = itemView.findViewById(R.id.ivBillLogo);
            tvBillTitle = itemView.findViewById(R.id.tvBillTitle);
            tvBillAmount = itemView.findViewById(R.id.tvBillAmount);
            tvBillUsdAmount = itemView.findViewById(R.id.tvBillUsdAmount);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView BillTitle, TextView BillAmount, TextView BillUsdAmount) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@NonNull Palette palette) {
                int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, android.R.color.white));
                cardView.setCardBackgroundColor(dominantColor);

                // Determine text color based on background brightness
                int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, android.R.color.white) : ContextCompat.getColor(context, R.color.text_color_dark);
                BillTitle.setTextColor(textColor);
                BillAmount.setTextColor(textColor);
                BillUsdAmount.setTextColor(textColor);
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

