package com.nicorp.crypto_test.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.nicorp.crypto_test.activities.FirstTabActivity;
import com.nicorp.crypto_test.adapters.WalletsTransactionAdapter;
import com.nicorp.crypto_test.adapters.BanksAdapter;
import com.nicorp.crypto_test.helpers.NavigationHelper;
import com.nicorp.crypto_test.objects.Wallet;
import com.nicorp.crypto_test.objects.Bank;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TransactionFragment extends Fragment {

    private RecyclerView walletRecyclerView;
    private WalletsTransactionAdapter walletAdapter;
    private List<Wallet> walletList = new ArrayList<>();
    private static final int INITIAL_LOAD_COUNT = 10;
    private View phoneNumberSection;
    private View cardNumberSection;
    private View walletAddressSection;
    private EditText amount;
    private EditText phoneNumber;
    private ConstraintLayout transactButton;
    private RecyclerView bankRecyclerView;
    private BanksAdapter bankAdapter;
    private List<Bank> bankList = new ArrayList<>();

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
        bankRecyclerView = view.findViewById(R.id.bank_recycler_view);
        phoneNumber = view.findViewById(R.id.phone_number);

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

        // Set up bank RecyclerView
        bankRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        bankAdapter = new BanksAdapter(getContext(), bankList);
        bankRecyclerView.setAdapter(bankAdapter);
        bankRecyclerView.addItemDecoration(new ItemOffsetDecoration(calculateItemWidth(bankRecyclerView, 2), 20));

        // Load wallets and show loading indicator
        loadWallets();

        // Load banks and show loading indicator
//        loadBanks();

        // Add TextWatcher to phoneNumber EditText
        phoneNumber.addTextChangedListener(new TextWatcher() {
            private final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{10,14}$");

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (PHONE_PATTERN.matcher(s).matches()) {
                    getBanksFromPhoneNumber(s.toString());
                } else if (s.length() <= 0) {
                    hideBankRecyclerViewWithAnimation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void hideBankRecyclerViewWithAnimation() {
        if (bankRecyclerView.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
            bankRecyclerView.startAnimation(animation);
            // Hide bankRecyclerView after animation ends
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    bankRecyclerView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
    }

    private void getBanksFromPhoneNumber(String phoneNumber) {
        // Simulate fetching banks based on phone number
        List<Bank> fetchedBanks = new ArrayList<>();
        fetchedBanks.add(new Bank("T-Bank", R.drawable.t_bank));
        fetchedBanks.add(new Bank("Alpha Bank", R.drawable.alpha));
        fetchedBanks.add(new Bank("Sber", R.drawable.sber));

        // Update bank list and notify adapter
        bankList.clear();
        bankList.addAll(fetchedBanks);
        bankAdapter.notifyDataSetChanged();

        // Show bankRecyclerView with animation
        showBankRecyclerViewWithAnimation();
    }

    private void showBankRecyclerViewWithAnimation() {
        if (bankRecyclerView.getVisibility() == View.GONE) {
            bankRecyclerView.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down);
            bankRecyclerView.startAnimation(animation);
        }
    }

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

    private int calculateItemWidth(RecyclerView recyclerView, int itemCount) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int spaceBetweenItems = dpToPx(20); // Adjust this as needed
        return (screenWidth - spaceBetweenItems * (itemCount - 1)) / itemCount;
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
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
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

    private int getLogoResource(String currency) {
        switch (currency.toUpperCase()) {
            case "BTC":
                return R.drawable.bitcoin;
            case "ETH":
                return R.drawable.ethereum;
            case "USDT":
                return R.drawable.tether;
            default:
                return R.drawable.qcoin;
        }
    }

    private void loadBanks() {
        // Add test banks
        bankList.clear();
        bankList.add(new Bank("T-Bank", R.drawable.t_bank));
        bankList.add(new Bank("Alpha Bank", R.drawable.alpha));
        bankList.add(new Bank("Sber", R.drawable.sber));
        bankAdapter.notifyDataSetChanged();
    }
}
