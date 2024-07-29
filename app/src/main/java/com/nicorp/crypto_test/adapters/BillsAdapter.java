package com.nicorp.crypto_test.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.objects.Bill;

import java.util.List;

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
        setCardViewBackground(holder.cardView, holder.ivBillLogo, holder.tvBillTitle, holder.tvBillAmount, holder.tvBillUsdAmount);
        holder.tvBillTitle.setText(bill.getTitle());
        holder.tvBillAmount.setText(bill.getAmount());
        holder.tvBillUsdAmount.setText(bill.getUsdAmount());

        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = (parentWidth - dpToPx(20) * (2 - 1)) / 2;
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
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.background_color_light));
                cardView.setCardBackgroundColor(dominantColor);

                int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, R.color.text_color_dark) : ContextCompat.getColor(context, R.color.text_color_light);
                titleBill.setTextColor(textColor);
                amountBill.setTextColor(textColor);
                equivalentBill.setTextColor(textColor);
            }
        });
    }

    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color) + 0.587 * android.graphics.Color.green(color) + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness >= 0.3;
    }

    private int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}