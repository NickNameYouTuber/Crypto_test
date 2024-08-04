package com.nicorp.crypto_test.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.fragments.PhoneNumberListFragment;
import com.nicorp.crypto_test.objects.Contact;

import java.util.List;

public class PhoneNumberAdapter extends RecyclerView.Adapter<PhoneNumberAdapter.ViewHolder> {

    private final List<Contact> phoneNumberList;
    private final OnPhoneNumberClickListener listener;

    public PhoneNumberAdapter(List<Contact> phoneNumberList, OnPhoneNumberClickListener listener) {
        this.phoneNumberList = phoneNumberList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone_number, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact Contact = phoneNumberList.get(position);
        holder.nameTextView.setText(Contact.getName());
        holder.numberTextView.setText(Contact.getPhoneNumber());
        holder.itemView.setOnClickListener(v -> listener.onPhoneNumberClick(Contact));
    }

    @Override
    public int getItemCount() {
        return phoneNumberList.size();
    }

    public interface OnPhoneNumberClickListener {
        void onPhoneNumberClick(Contact Contact);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView numberTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            numberTextView = itemView.findViewById(R.id.number_text_view);
        }
    }
}
