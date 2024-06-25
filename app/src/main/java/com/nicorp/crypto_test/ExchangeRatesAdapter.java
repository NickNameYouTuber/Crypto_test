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

public class ExchangeRatesAdapter extends RecyclerView.Adapter<ExchangeRatesAdapter.ExchangeRateViewHolder> {

    private Context context;
    private List<ExchangeRate> exchangeRates;
    private int parentWidth;

    public ExchangeRatesAdapter(Context context, List<ExchangeRate> exchangeRates) {
        this.context = context;
        this.exchangeRates = exchangeRates;
    }

    @NonNull
    @Override
    public ExchangeRateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exchange_rate, parent, false);
        parentWidth = parent.getMeasuredWidth();
        return new ExchangeRateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExchangeRateViewHolder holder, int position) {
        ExchangeRate exchangeRate = exchangeRates.get(position);
        holder.ivCurrencyLogo.setImageResource(exchangeRate.getLogo());
        setCardViewBackground(holder.cardView, holder.ivCurrencyLogo, holder.tvCurrencyName, holder.tvExchangeRate  ); // Set background color and text color based on image
        holder.tvCurrencyName.setText(exchangeRate.getCurrencyName());
        holder.tvExchangeRate.setText(exchangeRate.getRate());

        // Устанавливаем ширину и высоту для соотношения 2/3
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = (parentWidth - dpToPx(20) * (3 - 1)) / 3; // Adjust this calculation if needed
        layoutParams.height = (int) (layoutParams.width * (9.0 / 12.0));
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return exchangeRates.size();
    }

    public static class ExchangeRateViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCurrencyLogo;
        TextView tvCurrencyName, tvExchangeRate;
        CardView cardView;

        public ExchangeRateViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCurrencyLogo = itemView.findViewById(R.id.ivCurrencyLogo);
            tvCurrencyName = itemView.findViewById(R.id.tvCurrencyName);
            tvExchangeRate = itemView.findViewById(R.id.tvExchangeRate);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView CurrencyName, TextView ExchangeRate) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@NonNull Palette palette) {
                int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, android.R.color.white));
                cardView.setCardBackgroundColor(dominantColor);

                // Determine text color based on background brightness
                int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, android.R.color.white) : ContextCompat.getColor(context, R.color.text_color_dark);
                CurrencyName.setTextColor(textColor);
                ExchangeRate.setTextColor(textColor);
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
