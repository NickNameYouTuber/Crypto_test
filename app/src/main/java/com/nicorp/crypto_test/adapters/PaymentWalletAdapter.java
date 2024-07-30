package com.nicorp.crypto_test.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.nicorp.crypto_test.objects.Wallet;
import com.nicorp.crypto_test.R;

import java.util.List;

public class PaymentWalletAdapter extends RecyclerView.Adapter<PaymentWalletAdapter.ViewHolder> {

    private final Context context;
    private final List<Wallet> walletList;
    private int parentWidth;

    public PaymentWalletAdapter(Context context, List<Wallet> walletList) {
        this.context = context;
        this.walletList = walletList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_wallet_item, parent, false);
        parentWidth = parent.getMeasuredWidth();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wallet wallet = walletList.get(position);
        holder.walletLogoImageView.setImageResource(wallet.getLogo());
        holder.walletTitleTextView.setText(wallet.getTitle());
        holder.walletAmountTextView.setText(wallet.getAmount());
        holder.walletUsdAmountTextView.setText(wallet.getUsdAmount());

        setCardViewBackground(holder.cardView, holder.walletLogoImageView, holder.walletTitleTextView, holder.walletAmountTextView, holder.walletUsdAmountTextView);
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView walletLogoImageView;
        TextView walletTitleTextView, walletAmountTextView, walletUsdAmountTextView;
        CardView cardView;
        EditText amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            walletLogoImageView = itemView.findViewById(R.id.wallet_logo);
            walletTitleTextView = itemView.findViewById(R.id.wallet_title);
            walletAmountTextView = itemView.findViewById(R.id.wallet_amount);
            walletUsdAmountTextView = itemView.findViewById(R.id.wallet_usd_amount);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView titleWallet, TextView amountWallet, TextView usdAmountWallet) {
        // Extract bitmap from the image view
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (bitmap != null) {
            Palette.from(bitmap).generate(palette -> {
                if (palette != null) {
                    int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.background_color_light));
                    cardView.setCardBackgroundColor(dominantColor);

                    int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, R.color.text_color_dark) : ContextCompat.getColor(context, R.color.text_color_light);
                    titleWallet.setTextColor(textColor);
                    amountWallet.setTextColor(textColor);
                    usdAmountWallet.setTextColor(textColor);
                }
            });
        }
    }

    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color) + 0.587 * android.graphics.Color.green(color) + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness >= 0.3;
    }
}
