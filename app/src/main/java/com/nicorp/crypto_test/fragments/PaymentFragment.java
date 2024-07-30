package com.nicorp.crypto_test.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transauth.TransAuth;
import com.example.transauth.TransAuthUser;
import com.example.transauth.TransAuthWallet;
import com.nicorp.crypto_test.objects.Wallet;
import com.nicorp.crypto_test.objects.PaymentRecipient;
import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.adapters.PaymentWalletAdapter;
import com.nicorp.crypto_test.adapters.PaymentRecipientAdapter;
import com.nicorp.crypto_test.helpers.NavigationHelper;

import java.util.ArrayList;
import java.util.List;

public class PaymentFragment extends Fragment {

    private RecyclerView recipientRecyclerView;
    private RecyclerView walletRecyclerView;
    private PaymentRecipientAdapter recipientAdapter;
    private PaymentWalletAdapter walletAdapter;
    private List<PaymentRecipient> recipientList = new ArrayList<>();
    private List<Wallet> walletList = new ArrayList<>();
    private static final int INITIAL_LOAD_COUNT = 10;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        Log.d("PaymentFragment", "onCreateView");
        recipientRecyclerView = view.findViewById(R.id.recipient_recycler_view);
        walletRecyclerView = view.findViewById(R.id.wallet_recycler_view);

        // Set up recipient RecyclerView
        recipientRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recipientAdapter = new PaymentRecipientAdapter(getContext(), recipientList);
        recipientRecyclerView.setAdapter(recipientAdapter);

        // Set up wallet RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        walletRecyclerView.setLayoutManager(layoutManager);
        walletAdapter = new PaymentWalletAdapter(getContext(), walletList);
        walletRecyclerView.setAdapter(walletAdapter);

        // Init pay button
        view.findViewById(R.id.pay_button).setOnClickListener(v -> {
            // Go to PaymentSuccessFragment using NavigationHelper
            NavigationHelper.navigateToFragment(getActivity(), new PaymentSuccessFragment());
        });

        // Attach PagerSnapHelper to the walletRecyclerView for snapping effect
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(walletRecyclerView);

        if (getArguments() != null) {
            String name = getArguments().getString("name");
            String address = getArguments().getString("address");
            int amount = getArguments().getInt("amount");
            String currency = getArguments().getString("currency");

            Log.d("PaymentFragment", "Arguments: " + getArguments().toString());
            // Используйте полученные данные для настройки UI или логики
            Log.d("PaymentFragment", "Received data: " + name + ", " + address + ", " + amount + ", " + currency);
        } else {
            Log.e("PaymentFragment", "No arguments found");
        }

        // Retrieve and set data for recipients
        if (getArguments() != null) {
            String name = getArguments().getString("name");
            String address = getArguments().getString("address");
            int amount = getArguments().getInt("amount");
            String currency = getArguments().getString("currency");

            EditText amountEditText = view.findViewById(R.id.amount);
            amountEditText.setText(amount + " " + currency);
            amountEditText.setEnabled(false);

            Log.d("PaymentFragment", "Name: " + name);
            Log.d("PaymentFragment", "Address: " + address);
            Log.d("PaymentFragment", "Amount: " + amount);
            Log.d("PaymentFragment", "Currency: " + currency);

            PaymentRecipient recipient = new PaymentRecipient(name, address, amount, currency);
            recipientList.add(recipient);
            recipientAdapter.notifyDataSetChanged();
        }

        // Load wallets and show loading indicator
        loadWallets();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PaymentFragment", "onResume");
    }

    private void loadWallets() {
        new Thread(() -> {
            TransAuthUser currentUser = TransAuth.getUser();
            List<Wallet> updatedWalletList = new ArrayList<>();
            List<TransAuthWallet> transAuthWallets = currentUser.getWallets();

            // Load initial batch of wallets
            for (int i = 0; i < INITIAL_LOAD_COUNT && i < transAuthWallets.size(); i++) {
                TransAuthWallet transAuthWallet = transAuthWallets.get(i);

                Wallet wallet = new Wallet(
                        getLogoResource(transAuthWallet.getCurrency()),
                        transAuthWallet.getName(),
                        transAuthWallet.getBalance() + " " + transAuthWallet.getCurrency(),
                        "~ " + 100 + " USDT"
                );

                updatedWalletList.add(wallet);
            }

            // Update the UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                walletList.clear();
                walletList.addAll(updatedWalletList);
                walletAdapter.notifyDataSetChanged();
            });
        }).start();
    }

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