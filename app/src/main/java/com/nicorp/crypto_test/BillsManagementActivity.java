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

    }
}