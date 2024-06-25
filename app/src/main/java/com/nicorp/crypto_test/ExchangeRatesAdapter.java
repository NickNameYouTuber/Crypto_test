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
        holder.tvCurrencyName.setText(exchangeRate.getCurrencyName());
        holder.tvExchangeRate.setText(exchangeRate.getRate());

        // Устанавливаем ширину и высоту для соотношения 2/3
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = (parentWidth - dpToPx(20) * 2) / 3; // Вычисляем ширину с учетом отступов (20dp между элементами)
        layoutParams.height = (int) (layoutParams.width * (2.0 / 3.0));
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

    // Метод для перевода dp в пиксели
    private int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
