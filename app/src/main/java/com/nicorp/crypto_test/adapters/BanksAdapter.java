package com.nicorp.crypto_test.adapters;

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

import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.objects.Bank;

import java.util.List;

public class BanksAdapter extends RecyclerView.Adapter<BanksAdapter.BankViewHolder> {

    private Context context;
    private List<Bank> bankList;
    private int parentWidth;

    public BanksAdapter(Context context, List<Bank> bankList) {
        this.context = context;
        this.bankList = bankList;
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bank_item, parent, false);
        parentWidth = parent.getMeasuredWidth();
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        Bank bank = bankList.get(position);
        holder.bankLogo.setImageResource(bank.getLogoResource());
        setCardViewBackground(holder.cardView, holder.bankLogo, holder.bankName); // Set background color and text color based on image
        holder.bankName.setText(bank.getName());

        // Устанавливаем ширину и высоту для соотношения 2/3
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = (parentWidth - dpToPx(20) * (2 - 1)) / 2; // Adjust this calculation if needed
        layoutParams.height = (int) (layoutParams.width * (7.0 / 11.0));
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }

    public static class BankViewHolder extends RecyclerView.ViewHolder {

        ImageView bankLogo;
        TextView bankName;
        CardView cardView;

        public BankViewHolder(@NonNull View itemView) {
            super(itemView);
            bankLogo = itemView.findViewById(R.id.bank_logo);
            bankName = itemView.findViewById(R.id.bank_name);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView bankName) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@NonNull Palette palette) {
                int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, android.R.color.white));
                cardView.setCardBackgroundColor(dominantColor);

                // Determine text color based on background brightness
                int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, android.R.color.white) : ContextCompat.getColor(context, R.color.text_color_dark);
                bankName.setTextColor(textColor);
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
