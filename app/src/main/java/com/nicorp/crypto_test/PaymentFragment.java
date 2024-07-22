package com.nicorp.crypto_test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PaymentFragment extends Fragment {

    private RecyclerView recipientRecyclerView;
    private PaymentRecipientAdapter recipientAdapter;
    private List<PaymentRecipient> recipientList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        recipientRecyclerView = view.findViewById(R.id.recipient_recycler_view);
        recipientRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        recipientAdapter = new PaymentRecipientAdapter(recipientList);
        recipientRecyclerView.setAdapter(recipientAdapter);

        // Retrieve and set data
        if (getArguments() != null) {
            String name = getArguments().getString("name");
            String address = getArguments().getString("address");
            int amount = getArguments().getInt("amount");
            String currency = getArguments().getString("currency");

            PaymentRecipient recipient = new PaymentRecipient(name, address, amount, currency);
            recipientList.add(recipient);
            recipientAdapter.notifyDataSetChanged();
        }

        return view;
    }
}
