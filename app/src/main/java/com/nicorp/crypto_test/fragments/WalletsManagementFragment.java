package com.nicorp.crypto_test.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.transauth.TransAuthWallet;
import com.nicorp.crypto_test.objects.Wallet;
import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.adapters.WalletsManagementAdapter;
import com.nicorp.crypto_test.helpers.NavigationHelper;

import java.util.ArrayList;
import java.util.List;

public class WalletsManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private WalletsManagementAdapter adapter;
    private ImageView backButton;
    private ConstraintLayout addWalletButton;
    private View loadingLayout;
    private TransAuthUserDatabaseHelper db;
    private TransAuthUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallets_management, container, false);

        recyclerView = view.findViewById(R.id.rvWallets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        backButton = view.findViewById(R.id.backButton);
        addWalletButton = view.findViewById(R.id.addWalletButton);
        loadingLayout = view.findViewById(R.id.loadingLayout);

        db = new TransAuthUserDatabaseHelper(getContext());
        String currentUserLogin = "nicktaser";  // Replace with actual login retrieval
        currentUser = db.getUser(currentUserLogin);

        // Show the loading view
        showLoading();

        // Load wallets from user's wallets
        new Handler(Looper.getMainLooper()).postDelayed(this::loadWallets, 500);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click using NavigationHelper
                NavigationHelper.handleBackButton(getActivity(), new ProfileFragment(), -1);
            }
        });

        addWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SelectPlatformFragment using NavigationHelper
                NavigationHelper.navigateToFragment(getActivity(), new SelectPlatformFragment());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Show the loading view
        showLoading();
        // Load wallets
        new Handler(Looper.getMainLooper()).postDelayed(this::loadWallets, 500);
    }

    private void loadWallets() {
        List<Wallet> wallets = new ArrayList<>();

        // Add wallets based on user's wallets
        for (TransAuthWallet transAuthWallet : currentUser.getWallets()) {
            Log.d("Wallet", transAuthWallet.getName());
            wallets.add(new Wallet(getLogoResource(transAuthWallet.getCurrency()), transAuthWallet.getName(), transAuthWallet.getBalance() + " " + transAuthWallet.getCurrency(), "~ " + 100 + " " + transAuthWallet.getCurrency() + " USD"));
        }

        adapter = new WalletsManagementAdapter(wallets, getContext(), currentUser);
        recyclerView.setAdapter(adapter);

        // Hide the loading view with fade out animation
        hideLoading();
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

    private void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
        loadingLayout.setAlpha(0f);
        loadingLayout.animate().alpha(1f).setDuration(200).setListener(null);
    }

    private void hideLoading() {
        loadingLayout.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loadingLayout.setVisibility(View.GONE);
            }
        });
    }
}
