package com.nicorp.crypto_test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transauth.TransAuth;

import java.util.ArrayList;
import java.util.List;

public class BillsManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BillsManagementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AllHelpersSetup.setup(this, R.layout.activity_bills_management);

        recyclerView = findViewById(R.id.rvBills);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Bill> bills = new ArrayList<>();
        bills.add(new Bill(R.drawable.qcoin, "First bill", "1000 QC", "~ 100$"));
        bills.add(new Bill(R.drawable.bitcoin, "Second bill", "0,001 BTC", "~ 63,79$"));
        bills.add(new Bill(R.drawable.ethereum, "Third bill", "500 ETH", "~ 1500$"));

        adapter = new BillsManagementAdapter(bills, getBaseContext(), TransAuth.getUser());
        recyclerView.setAdapter(adapter);
    }
}