package com.nicorp.crypto_test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transauth.TransAuth;
import com.example.transauth.TransAuthUser;
import com.example.transauth.Wallet;

import java.util.ArrayList;
import java.util.List;

public class PaymentFragment extends Fragment {

    private RecyclerView recipientRecyclerView;
    private RecyclerView billRecyclerView;
    private PaymentRecipientAdapter recipientAdapter;
    private PaymentBillAdapter billAdapter;
    private List<PaymentRecipient> recipientList = new ArrayList<>();
    private List<Bill> billList = new ArrayList<>();
    private static final int INITIAL_LOAD_COUNT = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        recipientRecyclerView = view.findViewById(R.id.recipient_recycler_view);
        billRecyclerView = view.findViewById(R.id.bill_recycler_view);

        // Set up recipient RecyclerView
        recipientRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recipientAdapter = new PaymentRecipientAdapter(getContext(), recipientList);
        recipientRecyclerView.setAdapter(recipientAdapter);

        // Set up bill RecyclerView
        billRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        billAdapter = new PaymentBillAdapter(getContext(), billList);
        billRecyclerView.setAdapter(billAdapter);

        // Retrieve and set data for recipients
        if (getArguments() != null) {
            String name = getArguments().getString("name");
            String address = getArguments().getString("address");
            int amount = getArguments().getInt("amount");
            String currency = getArguments().getString("currency");

            PaymentRecipient recipient = new PaymentRecipient(name, address, amount, currency);
            recipientList.add(recipient);
            recipientAdapter.notifyDataSetChanged();
        }

        // Load bills and show loading indicator
        loadBills();

        return view;
    }

    private void loadBills() {
        new Thread(() -> {
            TransAuthUser currentUser = TransAuth.getUser();
            List<Bill> updatedBillList = new ArrayList<>();
            List<Wallet> wallets = currentUser.getWallets(); // Get wallets from TransAuthUser>

            // Load initial batch of bills
            for (int i = 0; i < INITIAL_LOAD_COUNT && i < wallets.size(); i++) {
                Wallet wallet = wallets.get(i);

                Bill bill = new Bill(
                        getLogoResource(wallet.getCurrency()),
                        wallet.getName(),
                        wallet.getBalance() + " " + wallet.getCurrency(),
                        "~ " + 100 + " USDT"
                );

                updatedBillList.add(bill);
            }

            // Update the UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                billList.clear();
                billList.addAll(updatedBillList);
                billAdapter.notifyDataSetChanged();
            });
        }).start();
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
