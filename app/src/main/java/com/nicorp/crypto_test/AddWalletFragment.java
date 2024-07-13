package com.nicorp.crypto_test;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.transauth.TransAuth;
import com.example.transauth.TransAuthUser;
import com.example.transauth.TransAuthUserDatabaseHelper;
import com.example.transauth.Wallet;

public class AddWalletFragment extends Fragment {

    private EditText editTextAddress, editTextName, editTextCurrency;
    private TextView textViewAddress;
    private Button buttonSave;
    private TransAuthUserDatabaseHelper db;
    private TransAuthUser currentUser;
    private String selectedPlatform;

    public AddWalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new TransAuthUserDatabaseHelper(getActivity());

        // Assuming you have some way to get the current user's login, e.g., from shared preferences
        String currentUserLogin = "nicktaser";  // Replace with actual login retrieval
        currentUser = db.getUser(currentUserLogin);

        if (getArguments() != null) {
            selectedPlatform = getArguments().getString("selectedPlatform");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_wallet, container, false);

        // Initialize views
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextName = view.findViewById(R.id.editTextName);
        editTextCurrency = view.findViewById(R.id.editTextCurrency);
        textViewAddress = view.findViewById(R.id.textView8);
        buttonSave = view.findViewById(R.id.buttonSave);

        // Hide address input if platform is Qrypt
        if ("Qrypt".equals(selectedPlatform)) {
            editTextAddress.setVisibility(View.GONE);
            textViewAddress.setVisibility(View.GONE);
        }

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
        String address = "0";
        if (!"Qrypt".equals(selectedPlatform)) {
            address = editTextAddress.getText().toString();
        }
        String name = editTextName.getText().toString();
        String currency = editTextCurrency.getText().toString();

        if ((!TextUtils.isEmpty(address) || "Qrypt".equals(selectedPlatform)) &&
                !TextUtils.isEmpty(name) && !TextUtils.isEmpty(currency)) {

            Wallet wallet = new Wallet(address, selectedPlatform, name);
            wallet.setBalance(0.0);
            wallet.setCurrency(currency);

            currentUser.addWallet(wallet);
            db.updateUser(currentUser);
            TransAuth.setUser(currentUser);

            Toast.makeText(getActivity(), "Wallet added successfully", Toast.LENGTH_SHORT).show();

            // Return to previous fragment
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        } else {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
