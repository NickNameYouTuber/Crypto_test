package com.nicorp.crypto_test.adapters;

import android.graphics.Bitmap;
import android.content.Context;
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
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.nicorp.crypto_test.objects.PaymentRecipient;
import com.nicorp.crypto_test.R;

import java.util.List;

public class PaymentRecipientAdapter extends RecyclerView.Adapter<PaymentRecipientAdapter.ViewHolder> {

    private final List<PaymentRecipient> recipientList;
    private final Context context;

    public PaymentRecipientAdapter(Context context, List<PaymentRecipient> recipientList) {
        this.context = context;
        this.recipientList = recipientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_recipient_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentRecipient recipient = recipientList.get(position);
        holder.toLogoImageView.setImageResource(R.drawable.pyaterochka); // Set logo image here
        holder.toLabelTextView.setText(recipient.getName());

        // Assuming that the drawable you are using is a bitmap and not a vector drawable
        Bitmap bitmap = ((BitmapDrawable) holder.toLogoImageView.getDrawable()).getBitmap();
        setCardViewBackground(holder.cardView, holder.toLogoImageView, holder.toLabelTextView, holder.toLabelTextView);
    }

    @Override
    public int getItemCount() {
        return recipientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView toLogoImageView;
        private final TextView toLabelTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view); // Ensure this ID matches the CardView ID in payment_recipient_item.xml
            toLogoImageView = itemView.findViewById(R.id.to_logo);
            toLabelTextView = itemView.findViewById(R.id.to_label);
        }
    }

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView currencyName, TextView exchangeRate) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@NonNull Palette palette) {
                int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, android.R.color.white));
                cardView.setCardBackgroundColor(dominantColor);

                // Determine text color based on background brightness
                int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, android.R.color.white) : ContextCompat.getColor(context, R.color.text_color_dark);
                currencyName.setTextColor(textColor);
                exchangeRate.setTextColor(textColor);
            }
        });
    }

    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5; // Adjust this threshold as needed
    }
}
