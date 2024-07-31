package com.nicorp.crypto_test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.nicorp.crypto_test.R;

public class TransactionFragment extends Fragment {

    private View phoneNumberSection;
    private View cardNumberSection;
    private View walletAddressSection;
    private EditText amount;

    public TransactionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        phoneNumberSection = view.findViewById(R.id.phone_number_section);
        cardNumberSection = view.findViewById(R.id.card_number_section);
        walletAddressSection = view.findViewById(R.id.wallet_address_section);
        amount = view.findViewById(R.id.amount);

        // Get the transaction type from arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            String transactionType = bundle.getString("transactionType");
            updateVisibility(transactionType);
        }

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
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.topToBottom = id;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topMargin = 50;
//        layoutParams.marginStart = 30;
//        layoutParams.marginEnd = 30;
        return layoutParams;
    }
}
