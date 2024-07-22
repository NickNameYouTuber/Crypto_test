package com.nicorp.crypto_test;

import android.content.Context;
import android.graphics.Bitmap;
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

import java.util.List;

public class PaymentBillAdapter extends RecyclerView.Adapter<PaymentBillAdapter.ViewHolder> {

    private final Context context;
    private final List<Bill> billList;
    private int parentWidth;

    public PaymentBillAdapter(Context context, List<Bill> billList) {
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_bill_item, parent, false);
        parentWidth = parent.getMeasuredWidth();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bill bill = billList.get(position);
        holder.billLogoImageView.setImageResource(bill.getLogo());
        holder.billTitleTextView.setText(bill.getTitle());
        holder.billAmountTextView.setText(bill.getAmount());
        holder.billUsdAmountTextView.setText(bill.getUsdAmount());

        setCardViewBackground(holder.cardView, holder.billLogoImageView, holder.billTitleTextView, holder.billAmountTextView, holder.billUsdAmountTextView);
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView billLogoImageView;
        TextView billTitleTextView, billAmountTextView, billUsdAmountTextView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            billLogoImageView = itemView.findViewById(R.id.bill_logo);
            billTitleTextView = itemView.findViewById(R.id.bill_title);
            billAmountTextView = itemView.findViewById(R.id.bill_amount);
            billUsdAmountTextView = itemView.findViewById(R.id.bill_usd_amount);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView titleBill, TextView amountBill, TextView usdAmountBill) {
        // Extract bitmap from the image view
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (bitmap != null) {
            Palette.from(bitmap).generate(palette -> {
                if (palette != null) {
                    int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.background_color_light));
                    cardView.setCardBackgroundColor(dominantColor);

                    int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, R.color.text_color_dark) : ContextCompat.getColor(context, R.color.text_color_light);
                    titleBill.setTextColor(textColor);
                    amountBill.setTextColor(textColor);
                    usdAmountBill.setTextColor(textColor);
                }
            });
        }
    }

    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color) + 0.587 * android.graphics.Color.green(color) + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness >= 0.3;
    }
}
