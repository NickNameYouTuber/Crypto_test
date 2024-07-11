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

import com.example.transauth.TransAuth;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills_management, container, false);

        recyclerView = view.findViewById(R.id.rvBills);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        backButton = view.findViewById(R.id.backButton);
        addBillButton = view.findViewById(R.id.addBillButton);

        List<Bill> bills = new ArrayList<>();
        bills.add(new Bill(R.drawable.qcoin, "First bill", "1000 QC", "~ 100$"));
        bills.add(new Bill(R.drawable.bitcoin, "Second bill", "0,001 BTC", "~ 63,79$"));
        bills.add(new Bill(R.drawable.ethereum, "Third bill", "500 ETH", "~ 1500$"));

        adapter = new BillsManagementAdapter(bills, getContext());
        recyclerView.setAdapter(adapter);

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
                            .replace(R.id.fragmentContainerView, new AddBillFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        TransAuthUserDatabaseHelper db = new TransAuthUserDatabaseHelper(getContext());
        TransAuthUser currentUser = db.getUser("nicktaser");

        // if user has wallet
        for (Wallet wallet : currentUser.getWallets()) {
            Log.d("Wallet", wallet.getName());
        }

        return view;
    }
}
