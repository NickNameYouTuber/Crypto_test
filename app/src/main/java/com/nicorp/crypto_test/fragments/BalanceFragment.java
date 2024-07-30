package com.nicorp.crypto_test.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transauth.TransAuth;
import com.example.transauth.TransAuthUser;
import com.example.transauth.TransAuthWallet;
import com.nicorp.crypto_test.objects.Wallet;
import com.nicorp.crypto_test.adapters.WalletsAdapter;
import com.nicorp.crypto_test.objects.ExchangeRate;
import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.objects.Transaction;
import com.nicorp.crypto_test.adapters.ExchangeRatesAdapter;
import com.nicorp.crypto_test.adapters.TransactionsAdapter;

import java.util.ArrayList;
import java.util.List;

public class BalanceFragment extends Fragment {

    private RecyclerView rvWallets, rvTransactions, rvExchangeRates;
    private WalletsAdapter walletsAdapter;
    private TransactionsAdapter transactionsAdapter;
    private ExchangeRatesAdapter exchangeRatesAdapter;
    private ArrayList<Wallet> walletList = new ArrayList<>();
    private ArrayList<Transaction> transactionList = new ArrayList<>();
    private ArrayList<ExchangeRate> exchangeRateList = new ArrayList<>();
    private View loadingLayout;
    private LruCache<String, Wallet> walletCache;
    private static final int INITIAL_LOAD_COUNT = 10;

    public BalanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BalanceFragment", "onCreate");

        // Initialize LruCache
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8; // Use 1/8th of the available memory for cache
        walletCache = new LruCache<>(cacheSize);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        rvWallets = view.findViewById(R.id.rvWallets);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        rvExchangeRates = view.findViewById(R.id.rvExchangeRates);
        loadingLayout = view.findViewById(R.id.loadingLayout);

        // Set LayoutManagers for RecyclerViews
        rvWallets.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvExchangeRates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Set adapters
        walletsAdapter = new WalletsAdapter(getContext(), walletList);
        rvWallets.setAdapter(walletsAdapter);

        transactionsAdapter = new TransactionsAdapter(getContext(), transactionList);
        rvTransactions.setAdapter(transactionsAdapter);

        exchangeRatesAdapter = new ExchangeRatesAdapter(getContext(), exchangeRateList);
        rvExchangeRates.setAdapter(exchangeRatesAdapter);

        showLoading();
        // Load wallets from user's wallets
        new Handler(Looper.getMainLooper()).postDelayed(this::updateWalletList, 500);
        addTestData(); // Загружаем тестовые данные

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // При каждом отображении фрагмента обновляем данные
        Log.d("BalanceFragment", "Resume");
    }

    private void addTestData() {
        // Load test transactions
        transactionList.add(new Transaction(R.drawable.tether, "Ivan I.I.", "+ 10 USDT"));
        transactionList.add(new Transaction(R.drawable.pyaterochka, "Pyaterochka", "- 1488 RUB"));
        transactionList.add(new Transaction(R.drawable.mvideo, "Mvideo", "- 9000 RUB"));
        transactionsAdapter.notifyDataSetChanged();

        // Load test exchange rates
        exchangeRateList.add(new ExchangeRate(R.drawable.qcoin, "QCoin", "0.1 USDT"));
        exchangeRateList.add(new ExchangeRate(R.drawable.bitcoin, "Bitcoin", "63 532 USDT"));
        exchangeRateList.add(new ExchangeRate(R.drawable.ethereum, "Etherium", "3 489 USDT"));
        exchangeRateList.add(new ExchangeRate(R.drawable.ton, "TON", "60 USDT"));
        exchangeRatesAdapter.notifyDataSetChanged();

        // Add ItemDecoration for spacing between items
        rvWallets.addItemDecoration(new ItemOffsetDecoration(calculateItemWidth(rvWallets, 2), 20));
        rvTransactions.addItemDecoration(new ItemOffsetDecoration(calculateItemWidth(rvTransactions, 2), 20));
        rvExchangeRates.addItemDecoration(new ItemOffsetDecoration(calculateItemWidth(rvExchangeRates, 3), 20));
    }

    private void updateWalletList() {
        showLoading();

        new Thread(() -> {
            TransAuthUser currentUser = TransAuth.getUser();
            List<Wallet> updatedWalletList = new ArrayList<>();
            List<TransAuthWallet> transAuthWallets = currentUser.getWallets(); // Get wallets from TransAuthUser>

            // Load initial batch of wallets
            for (int i = 0; i < INITIAL_LOAD_COUNT && i < currentUser.getWallets().size(); i++) {
                TransAuthWallet transAuthWallet = transAuthWallets.get(i);
//                String cacheKey = wallet.getCurrency() + wallet.getName();
//                Wallet wallet = walletCache.get(cacheKey);
//
//
//                if (wallet == null) {
//                    wallet = new Wallet(
//                            getLogoResource(wallet.getCurrency()),
//                            wallet.getName(),
//                            wallet.getBalance() + " " + wallet.getCurrency(),
//                            "~ " + 100 + " USDT"
//                    );
//                    walletCache.put(cacheKey, wallet);
//                }

                Wallet wallet = new Wallet(
                        getLogoResource(transAuthWallet.getCurrency()),
                        transAuthWallet.getName(),
                        transAuthWallet.getBalance() + " " + transAuthWallet.getCurrency(),
                        "~ " + 100 + " USDT"
                );
//                walletCache.put(cacheKey, wallet);

                updatedWalletList.add(wallet);
            }

            // Update the UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                walletList.clear();
                walletList.addAll(updatedWalletList);
                walletsAdapter.notifyDataSetChanged();
                hideLoading();
            });
        }).start();
    }

    private void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
        loadingLayout.setAlpha(1f);
    }

    private void hideLoading() {
        loadingLayout.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loadingLayout.setVisibility(View.GONE);
            }
        });
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

    // Class for setting spacing between RecyclerView items
    private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
        private int itemWidth;
        private int spaceBetweenItems;

        public ItemOffsetDecoration(int itemWidth, int spaceBetweenItems) {
            this.itemWidth = itemWidth;
            this.spaceBetweenItems = spaceBetweenItems;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int itemCount = parent.getAdapter().getItemCount();

            // Reset all offsets
            outRect.left = 0;
            outRect.right = 0;

            // Apply spacing logic
            if (position < itemCount - 1) {
                // Not the last item
                outRect.right = dpToPx(spaceBetweenItems);
            }
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density);
    }

    private int calculateItemWidth(RecyclerView recyclerView, int itemCount) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int spaceBetweenItems = dpToPx(20); // Adjust this as needed
        return (screenWidth - spaceBetweenItems * (itemCount - 1)) / itemCount;
    }
}