package com.nicorp.crypto_test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PaymentRecipientAdapter extends RecyclerView.Adapter<PaymentRecipientAdapter.ViewHolder> {

    private final List<PaymentRecipient> recipientList;

    public PaymentRecipientAdapter(List<PaymentRecipient> recipientList) {
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
    }

    @Override
    public int getItemCount() {
        return recipientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView toLogoImageView;
        private final TextView toLabelTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            toLogoImageView = itemView.findViewById(R.id.to_logo);
            toLabelTextView = itemView.findViewById(R.id.to_label);
        }
    }
}
