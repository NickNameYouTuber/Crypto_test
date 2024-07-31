package com.nicorp.crypto_test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.adapters.TransactionByAdapter;
import com.nicorp.crypto_test.objects.TransactionItem;

import java.util.ArrayList;
import java.util.List;


public class TransactionSelectFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionByAdapter adapter;

    public TransactionSelectFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_select, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBy);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<TransactionItem> transactionItems = new ArrayList<>();
        transactionItems.add(new TransactionItem("Phone Number", R.drawable.ic_phone, "phone_number"));
        transactionItems.add(new TransactionItem("Card Number", R.drawable.ic_card, "card_number"));
        transactionItems.add(new TransactionItem("Wallet Address", R.drawable.ic_wallet, "wallet_address"));

        adapter = new TransactionByAdapter(transactionItems, this::navigateToTransactionFragment);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void navigateToTransactionFragment(String transactionType) {
        Bundle bundle = new Bundle();
        bundle.putString("transactionType", transactionType);
        TransactionFragment transactionFragment = new TransactionFragment();
        transactionFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, transactionFragment)
                .addToBackStack(null)
                .commit();
    }
}
