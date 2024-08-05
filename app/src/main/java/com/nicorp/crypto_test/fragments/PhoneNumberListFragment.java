package com.nicorp.crypto_test.fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.adapters.PhoneNumberAdapter;
import com.nicorp.crypto_test.helpers.NavigationHelper;
import com.nicorp.crypto_test.objects.Contact;

import java.util.ArrayList;
import java.util.List;

public class PhoneNumberListFragment extends Fragment {

    private RecyclerView contactRecyclerView;
    private PhoneNumberAdapter contactAdapter;
    private List<Contact> contactList = new ArrayList<>();
    private TransactionFragment transactionFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_number_list, container, false);
        contactRecyclerView = view.findViewById(R.id.phone_number_recycler_view);

        // Set up contact RecyclerView
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        contactAdapter = new PhoneNumberAdapter(contactList, this::onContactSelected);
        contactRecyclerView.setAdapter(contactAdapter);

        // Load contacts
        loadContacts();

        return view;
    }

    private void loadContacts() {
        new Thread(() -> {
            ContentResolver contentResolver = requireContext().getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    Contact contact = new Contact(name, phoneNumber);
                    contactList.add(contact);
                }
                cursor.close();
            }

            // Update the UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> contactAdapter.notifyDataSetChanged());
        }).start();
    }

    public void setTransactionFragment(TransactionFragment transactionFragment) {
        this.transactionFragment = transactionFragment;
    }

    private void onContactSelected(Contact contact) {
        Log.d("PhoneNumberListFragment", "Selected contact: " + contact.getPhoneNumber());
        if (transactionFragment != null) {
            TransactionFragment fragment = new TransactionFragment();
            fragment.setPhoneNumber(contact.getPhoneNumber());
            Bundle bundle = new Bundle();
            bundle.putString("phone_number", contact.getPhoneNumber());
            bundle.putString("transactionType", "phone_number");
            NavigationHelper.navigateToFragment(getActivity(), fragment, bundle);
        } else {
            Log.e("PhoneNumberListFragment", "TransactionFragment is null");
        }
    }
}
