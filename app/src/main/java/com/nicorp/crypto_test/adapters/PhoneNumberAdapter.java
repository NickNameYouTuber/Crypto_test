package com.nicorp.crypto_test.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.objects.Contact;

import java.util.ArrayList;
import java.util.List;

public class PhoneNumberAdapter extends RecyclerView.Adapter<PhoneNumberAdapter.ViewHolder> {

    private List<Contact> contacts;
    private OnPhoneNumberClickListener listener;

    public PhoneNumberAdapter(List<Contact> contacts, OnPhoneNumberClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    public void updateList(List<Contact> newContacts) {
        contacts = new ArrayList<>(newContacts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());

        holder.itemView.setOnClickListener(v -> listener.onPhoneNumberClick(contact.getPhoneNumber()));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView phoneNumberTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneNumberTextView = itemView.findViewById(R.id.contact_phone_number);
        }
    }

    public interface OnPhoneNumberClickListener {
        void onPhoneNumberClick(String phoneNumber);
    }
}
