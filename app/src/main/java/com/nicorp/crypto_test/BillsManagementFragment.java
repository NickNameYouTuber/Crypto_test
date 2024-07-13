package com.nicorp.crypto_test;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transauth.TransAuthUser;
import com.example.transauth.TransAuthUserDatabaseHelper;
import com.example.transauth.Wallet;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transauth.TransAuthUser;
import com.example.transauth.TransAuthUserDatabaseHelper;
import com.example.transauth.Wallet;

import java.util.ArrayList;
import java.util.List;

public class BillsManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private BillsManagementAdapter adapter;
    private ImageView backButton;
    private ConstraintLayout addBillButton;
    private TransAuthUserDatabaseHelper db;
    private TransAuthUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills_management, container, false);

        recyclerView = view.findViewById(R.id.rvBills);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        backButton = view.findViewById(R.id.backButton);
        addBillButton = view.findViewById(R.id.addBillButton);

        db = new TransAuthUserDatabaseHelper(getContext());
        String currentUserLogin = "nicktaser";  // Replace with actual login retrieval
        currentUser = db.getUser(currentUserLogin);

        // Load bills from user's wallets
        loadBills();

        // Set the appropriate drawable for the back button based on the theme
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            backButton.setImageResource(R.drawable.arrow_left_w);
        } else {
            backButton.setImageResource(R.drawable.arrow_left_b);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click, for example, pop the back stack
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        addBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to the AddBillFragment
                if (getFragmentManager() != null) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, new SelectPlatformFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBills();
    }

    private void loadBills() {
        List<Bill> bills = new ArrayList<>();

        // Add bills based on user's wallets
        for (Wallet wallet : currentUser.getWallets()) {
            Log.d("Wallet", wallet.getName());
            bills.add(new Bill(getLogoResource(wallet.getCurrency()), wallet.getName(), wallet.getBalance() + " " + wallet.getCurrency(), "~ " + 100 + " " + wallet.getCurrency() + " USD"));
        }

        adapter = new BillsManagementAdapter(bills, getContext(), currentUser);
        recyclerView.setAdapter(adapter);
    }

    // Assuming you have a method to get the logo resource based on the platform name
    private int getLogoResource(String platform) {
        switch (platform.toLowerCase()) {
            case "btc":
                return R.drawable.bitcoin;
            case "eth":
                return R.drawable.ethereum;
            case "usdt":
                return R.drawable.tether;
            case "qcoin":
                return R.drawable.qcoin;
            default:
                return R.drawable.bitcoin; // Fallback logo if platform is unknown
        }
    }
}
