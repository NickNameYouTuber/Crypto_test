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
import com.nicorp.crypto_test.objects.Wallet;

import java.util.List;

public class WalletsAdapter extends RecyclerView.Adapter<WalletsAdapter.WalletViewHolder> {

    private Context context;
    private List<Wallet> wallets;
    private int parentWidth;

    public WalletsAdapter(Context context, List<Wallet> wallets) {
        this.context = context;
        this.wallets = wallets;
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wallet, parent, false);
        parentWidth = parent.getMeasuredWidth();
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, int position) {
        Wallet wallet = wallets.get(position);
        holder.ivWalletLogo.setImageResource(wallet.getLogo());
        setCardViewBackground(holder.cardView, holder.ivWalletLogo, holder.tvWalletTitle, holder.tvWalletAmount, holder.tvWalletUsdAmount);
        holder.tvWalletTitle.setText(wallet.getTitle());
        holder.tvWalletAmount.setText(wallet.getAmount());
        holder.tvWalletUsdAmount.setText(wallet.getUsdAmount());

        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = (parentWidth - dpToPx(20) * (2 - 1)) / 2;
        layoutParams.height = (int) (layoutParams.width * (5.0 / 9.0));
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    public static class WalletViewHolder extends RecyclerView.ViewHolder {
        ImageView ivWalletLogo;
        TextView tvWalletTitle, tvWalletAmount, tvWalletUsdAmount;
        CardView cardView;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWalletLogo = itemView.findViewById(R.id.ivWalletLogo);
            tvWalletTitle = itemView.findViewById(R.id.tvWalletTitle);
            tvWalletAmount = itemView.findViewById(R.id.tvWalletAmount);
            tvWalletUsdAmount = itemView.findViewById(R.id.tvWalletUsdAmount);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView titleWallet, TextView amountWallet, TextView equivalentWallet) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.background_color_light));
                cardView.setCardBackgroundColor(dominantColor);

                int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, R.color.text_color_dark) : ContextCompat.getColor(context, R.color.text_color_light);
                titleWallet.setTextColor(textColor);
                amountWallet.setTextColor(textColor);
                equivalentWallet.setTextColor(textColor);
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