package com.nicorp.crypto_test;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.transauth.TransAuthUser;
import com.example.transauth.TransAuthUserDatabaseHelper;
import com.example.transauth.Wallet;

public class AddBillFragment extends Fragment {

    private EditText editTextAddress, editTextPlatform, editTextName, editTextBalance, editTextCurrency;
    private Button buttonSave;
    private TransAuthUserDatabaseHelper db;
    private TransAuthUser currentUser;

    public AddBillFragment() {
        // Required empty public constructor
    }

    public static AddBillFragment newInstance(String param1, String param2) {
        AddBillFragment fragment = new AddBillFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // handle arguments if needed
        }

        Log.d("Wallet", "onCreate");

        db = new TransAuthUserDatabaseHelper(getActivity());

        // Assuming you have some way to get the current user's login, e.g., from shared preferences
        String currentUserLogin = "login";  // Replace with actual login retrieval
        currentUser = db.getUser("nicktaser");

        Log.d("Wallet", currentUser.getUsername());

        // if user has wallet
        for (Wallet wallet : currentUser.getWallets()) {
            Log.d("Wallet", wallet.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_bill, container, false);

        // Initialize views
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextPlatform = view.findViewById(R.id.editTextPlatform);
        editTextName = view.findViewById(R.id.editTextName);
        editTextBalance = view.findViewById(R.id.editTextBalance);
        editTextCurrency = view.findViewById(R.id.editTextCurrency);
        buttonSave = view.findViewById(R.id.buttonSave);

        // Set button click listener
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWallet();
            }
        });

        return view;
    }

    private void saveWallet() {
        String address = editTextAddress.getText().toString();
        String platform = editTextPlatform.getText().toString();
        String name = editTextName.getText().toString();
        String balanceStr = editTextBalance.getText().toString();
        String currency = editTextCurrency.getText().toString();

        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(platform) || TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(balanceStr) || TextUtils.isEmpty(currency)) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double balance = Double.parseDouble(balanceStr);

        Wallet wallet = new Wallet(address, platform, name);
        wallet.setBalance(balance);
        wallet.setCurrency(currency);

        currentUser.addWallet(wallet);
        db.addUser(currentUser);

        Toast.makeText(getActivity(), "Wallet added successfully", Toast.LENGTH_SHORT).show();
    }
}
