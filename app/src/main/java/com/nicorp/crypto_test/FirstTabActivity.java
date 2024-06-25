package com.nicorp.crypto_test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;

public class FirstTabActivity extends AppCompatActivity {

    private RecyclerView rvBills, rvTransactions, rvExchangeRates;
    private BillsAdapter billsAdapter;
    private TransactionsAdapter transactionsAdapter;
    private ExchangeRatesAdapter exchangeRatesAdapter;
    private ArrayList<Bill> billList = new ArrayList<>();
    private ArrayList<Transaction> transactionList = new ArrayList<>();
    private ArrayList<ExchangeRate> exchangeRateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_tab);

        rvBills = findViewById(R.id.rvBills);
        rvTransactions = findViewById(R.id.rvTransactions);
        rvExchangeRates = findViewById(R.id.rvExchangeRates);

        // Устанавливаем горизонтальный LinearLayoutManager
        rvBills.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTransactions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvExchangeRates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Устанавливаем адаптеры
        billsAdapter = new BillsAdapter(this, billList);
        rvBills.setAdapter(billsAdapter);

        transactionsAdapter = new TransactionsAdapter(this, transactionList);
        rvTransactions.setAdapter(transactionsAdapter);

        exchangeRatesAdapter = new ExchangeRatesAdapter(this, exchangeRateList);
        rvExchangeRates.setAdapter(exchangeRatesAdapter);

        // Добавляем ItemDecoration для расстояний между элементами
        rvBills.addItemDecoration(new ItemOffsetDecoration(16));
        rvTransactions.addItemDecoration(new ItemOffsetDecoration(16));
        rvExchangeRates.addItemDecoration(new ItemOffsetDecoration(16));

        addTestData();
    }

    private void addTestData() {
        // Добавляем тестовые счета
        billList.add(new Bill(R.drawable.qcoin, "First Bill", "50 QC", "5 USD"));
        billList.add(new Bill(R.drawable.ethereum, "Second Bill", "30 QC", "3 USD"));
        billsAdapter.notifyDataSetChanged();

        // Добавляем тестовые транзакции
        transactionList.add(new Transaction(R.drawable.bitcoin, "Payment to John", "-50 QC"));
        transactionList.add(new Transaction(R.drawable.bitcoin, "Payment from Mary", "+50 QC"));
        transactionsAdapter.notifyDataSetChanged();

        // Добавляем тестовые курсы обмена
        exchangeRateList.add(new ExchangeRate(R.drawable.qcoin, "USD/QC", "1:10"));
        exchangeRateList.add(new ExchangeRate(R.drawable.bitcoin, "EUR/QC", "1:12"));
        exchangeRateList.add(new ExchangeRate(R.drawable.ethereum, "GBP/QC", "1:14"));
        exchangeRateList.add(new ExchangeRate(R.drawable.ethereum, "GBP/QC", "1:14"));
        exchangeRateList.add(new ExchangeRate(R.drawable.ethereum, "GBP/QC", "1:14"));
        exchangeRatesAdapter.notifyDataSetChanged();
    }

    // Класс для установки расстояний между элементами RecyclerView
    private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
        private int offset;

        public ItemOffsetDecoration(int offset) {
            this.offset = offset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = offset;
            outRect.right = offset;
        }
    }
}
