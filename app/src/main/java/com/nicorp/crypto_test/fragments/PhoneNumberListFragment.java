package com.nicorp.crypto_test.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.adapters.PhoneNumberAdapter;
import com.nicorp.crypto_test.objects.Contact;

import java.util.ArrayList;
import java.util.List;

public class PhoneNumberListFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView phoneNumberRecyclerView;
    private PhoneNumberAdapter phoneNumberAdapter;
    private List<Contact> phoneNumberList = new ArrayList<>();
    private TransactionFragment transactionFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_number_list, container, false);

        searchEditText = view.findViewById(R.id.search_edit_text);
        phoneNumberRecyclerView = view.findViewById(R.id.phone_number_recycler_view);

        phoneNumberRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        phoneNumberAdapter = new PhoneNumberAdapter(phoneNumberList, this::onPhoneNumberSelected);
        phoneNumberRecyclerView.setAdapter(phoneNumberAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchPhoneNumbers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void searchPhoneNumbers(String query) {
        phoneNumberList.clear();
        if (!query.isEmpty()) {
            List<Contact> contacts = getContacts(query);
            phoneNumberList.addAll(contacts);
        }
        phoneNumberAdapter.notifyDataSetChanged();
    }

    private List<Contact> getContacts(String query) {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = requireContext().getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1",
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Cursor phones = requireContext().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id},
                        null
                );

                if (phones != null) {
                    while (phones.moveToNext()) {
                        @SuppressLint("Range") String Contact = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (Contact.contains(query)) {
                            contacts.add(new Contact(name, Contact));
                        }
                    }
                    phones.close();
                }
            }
            cursor.close();
        }

        if (contacts.isEmpty()) {
            contacts.add(new Contact("Неизвестный номер", query));
        }

        return contacts;
    }

    private void onPhoneNumberSelected(Contact Contact) {
        if (transactionFragment != null) {
            transactionFragment.setPhoneNumber(Contact.getPhoneNumber());
        }
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public void setTransactionFragment(TransactionFragment transactionFragment) {
        this.transactionFragment = transactionFragment;
    }
}
