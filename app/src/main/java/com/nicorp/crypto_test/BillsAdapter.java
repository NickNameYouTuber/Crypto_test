package com.nicorp.crypto_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView titleBill, TextView amountBill, TextView equivalentBill) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        int[] pixels = getBitmapPixels(bitmap);
        int dominantColor = getDominantColor(pixels);

        cardView.setCardBackgroundColor(dominantColor);

        Log.d("BillViewHolder", "Is dark color: " + isDarkColor(dominantColor));

        // Определяем цвет текста на основе яркости фона
        int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, R.color.text_color_dark) : ContextCompat.getColor(context, R.color.text_color_light);
        titleBill.setTextColor(textColor);
        amountBill.setTextColor(textColor);
        equivalentBill.setTextColor(textColor);
    }

    // Получаем массив пикселей изображения
    private int[] getBitmapPixels(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    // Находим наиболее часто встречающийся цвет среди пикселей
    private int getDominantColor(int[] pixels) {
        Map<Integer, Integer> colorCountMap = new HashMap<>();

        for (int color : pixels) {
            if (colorCountMap.containsKey(color)) {
                colorCountMap.put(color, colorCountMap.get(color) + 1);
            } else {
                colorCountMap.put(color, 1);
            }
        }

        int maxCount = 0;
        int dominantColor = Color.WHITE; // По умолчанию, если не найден другой цвет
        for (Map.Entry<Integer, Integer> entry : colorCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                dominantColor = entry.getKey();
            }
        }

        return dominantColor;
    }

    // Helper function to determine if a color is dark
    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    private int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}

