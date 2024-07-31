package com.nicorp.crypto_test.fragments;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transauth.TransAuth;
import com.example.transauth.TransAuthUser;
import com.example.transauth.TransAuthWallet;
import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.adapters.WalletsTransactionAdapter;
import com.nicorp.crypto_test.helpers.NavigationHelper;
import com.nicorp.crypto_test.objects.Wallet;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {

    private RecyclerView walletRecyclerView;
    private WalletsTransactionAdapter walletAdapter;
    private List<Wallet> walletList = new ArrayList<>();
    private static final int INITIAL_LOAD_COUNT = 10;
    private View phoneNumberSection;
    private View cardNumberSection;
    private View walletAddressSection;
    private EditText amount;
    private ConstraintLayout transactButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        Log.d("TransactionFragment", "onCreateView");

        walletRecyclerView = view.findViewById(R.id.wallet_recycler_view);
        phoneNumberSection = view.findViewById(R.id.phone_number_section);
        cardNumberSection = view.findViewById(R.id.card_number_section);
        walletAddressSection = view.findViewById(R.id.wallet_address_section);
        amount = view.findViewById(R.id.amount);
        transactButton = view.findViewById(R.id.transact_button);

        transactButton.setOnClickListener(v -> {
            NavigationHelper.navigateToFragment(getActivity(), new PaymentSuccessFragment());
        });

        // Set up wallet RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        walletRecyclerView.setLayoutManager(layoutManager);
        walletAdapter = new WalletsTransactionAdapter(getContext(), walletList);
        walletRecyclerView.setAdapter(walletAdapter);

        // Attach PagerSnapHelper to the walletRecyclerView for snapping effect
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(walletRecyclerView);

        // Get the transaction type from arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            String transactionType = bundle.getString("transactionType");
            updateVisibility(transactionType);
        }

        // Load wallets and show loading indicator
        loadWallets();

        return view;
    }

    private void updateVisibility(String transactionType) {
        phoneNumberSection.setVisibility(View.GONE);
        cardNumberSection.setVisibility(View.GONE);
        walletAddressSection.setVisibility(View.GONE);

        switch (transactionType) {
            case "phone_number":
                phoneNumberSection.setVisibility(View.VISIBLE);
                amount.setLayoutParams(createLayoutParamsBelow(R.id.phone_number_section));
                break;
            case "card_number":
                cardNumberSection.setVisibility(View.VISIBLE);
                amount.setLayoutParams(createLayoutParamsBelow(R.id.card_number_section));
                break;
            case "wallet_address":
                walletAddressSection.setVisibility(View.VISIBLE);
                amount.setLayoutParams(createLayoutParamsBelow(R.id.wallet_address_section));
                break;
        }
    }

    private ConstraintLayout.LayoutParams createLayoutParamsBelow(int id) {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(50)
        );
        layoutParams.topToBottom = id;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topMargin = dpToPx(50);
        layoutParams.leftMargin = dpToPx(30);
        layoutParams.rightMargin = dpToPx(30);
        return layoutParams;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TransactionFragment", "onResume");
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
