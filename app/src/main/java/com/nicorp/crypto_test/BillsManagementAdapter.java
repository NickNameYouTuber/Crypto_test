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

public class BillsManagementAdapter extends RecyclerView.Adapter<BillsManagementAdapter.BillViewHolder> {

    private List<Bill> bills;
    private Context context;


    public BillsManagementAdapter(List<Bill> bills, Context context) {
        this.bills = bills;
        this.context = context;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_manage, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.iconBill.setImageResource(bill.getLogo());
        holder.titleBill.setText(bill.getTitle());
        holder.amountBill.setText(bill.getAmount());
        holder.equivalentBill.setText(bill.getUsdAmount());
        setCardViewBackground(holder.cardView, holder.iconBill, holder.titleBill, holder.amountBill, holder.equivalentBill); // Set background color and text color based on image

    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        ImageView iconBill;
        TextView titleBill, amountBill, equivalentBill;
        CardView cardView;

        BillViewHolder(View itemView) {
            super(itemView);
            iconBill = itemView.findViewById(R.id.iconBill);
            titleBill = itemView.findViewById(R.id.titleBill);
            amountBill = itemView.findViewById(R.id.amountBill);
            equivalentBill = itemView.findViewById(R.id.equivalentBill);
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

                // Add bottom margin to card view
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
                params.bottomMargin = dpToPx(20);
                cardView.setLayoutParams(params);

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