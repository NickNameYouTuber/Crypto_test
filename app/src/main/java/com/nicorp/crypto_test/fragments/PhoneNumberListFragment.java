package com.nicorp.crypto_test.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.adapters.PhoneNumberAdapter;
import com.nicorp.crypto_test.objects.Contact;

import java.util.ArrayList;
import java.util.List;

public class PhoneNumberListFragment extends Fragment implements PhoneNumberAdapter.OnPhoneNumberClickListener {

    private RecyclerView phoneNumberRecyclerView;
    private PhoneNumberAdapter phoneNumberAdapter;
    private List<Contact> contactList = new ArrayList<>();
    private EditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_number_list, container, false);
        Log.d("PhoneNumberListFragment", "onCreateView");

        phoneNumberRecyclerView = view.findViewById(R.id.phone_number_recycler_view);
        searchEditText = view.findViewById(R.id.search_edit_text);

        phoneNumberRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        phoneNumberAdapter = new PhoneNumberAdapter(contactList, this);
        phoneNumberRecyclerView.setAdapter(phoneNumberAdapter);

        loadContacts();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void loadContacts() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contactList.add(new Contact(name, phoneNumber));
            }
            cursor.close();
        }

        phoneNumberAdapter.notifyDataSetChanged();
    }

    private void searchContacts(String query) {
        List<Contact> filteredContacts = new ArrayList<>();
        for (Contact contact : contactList) {
            if (contact.getPhoneNumber().contains(query)) {
                filteredContacts.add(contact);
            }
        }

        // If no contacts found, add a placeholder for the unknown number
        if (filteredContacts.isEmpty() && !query.isEmpty()) {
            filteredContacts.add(new Contact("Неизвестный номер", query));
        }

        phoneNumberAdapter.updateList(filteredContacts);
    }

    @Override
    public void onPhoneNumberClick(String phoneNumber) {
        // Pass the selected phone number back to TransactionFragment
        TransactionFragment transactionFragment = (TransactionFragment) getParentFragmentManager().findFragmentByTag("TransactionFragment");
        if (transactionFragment != null) {
            transactionFragment.setPhoneNumber(phoneNumber);
        }
        getParentFragmentManager().popBackStack();
    }
}
