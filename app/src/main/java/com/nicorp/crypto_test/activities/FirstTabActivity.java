package com.nicorp.crypto_test.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import com.nicorp.crypto_test.AllHelpersSetup;
import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.adapters.BillsAdapter;
import com.nicorp.crypto_test.adapters.ExchangeRatesAdapter;
import com.nicorp.crypto_test.adapters.TransactionsAdapter;
import com.nicorp.crypto_test.objects.Bill;
import com.nicorp.crypto_test.objects.ExchangeRate;
import com.nicorp.crypto_test.objects.Transaction;

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
        AllHelpersSetup.setup(this, R.layout.activity_first_tab);

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


        addTestData();
    }

    private void addTestData() {
        // Добавляем тестовые счета


        // Добавляем тестовые транзакции
        transactionList.add(new Transaction(R.drawable.tether, "Ivan I.I.", "+ 10 USDT"));
        transactionList.add(new Transaction(R.drawable.pyaterochka, "Pyaterochka", "- 1488 RUB"));
        transactionList.add(new Transaction(R.drawable.mvideo, "Mvideo", "- 9000 RUB"));
        transactionsAdapter.notifyDataSetChanged();

        // Добавляем тестовые курсы обмена
        exchangeRateList.add(new ExchangeRate(R.drawable.qcoin, "QCoin", "0.1 USDT"));
        exchangeRateList.add(new ExchangeRate(R.drawable.bitcoin, "Bitcoin", "63 532 USDT"));
        exchangeRateList.add(new ExchangeRate(R.drawable.ethereum, "Etherium", "3 489 USDT"));
        exchangeRateList.add(new ExchangeRate(R.drawable.ton, "TON", "60 USDT"));
        exchangeRatesAdapter.notifyDataSetChanged();

        // Добавляем ItemDecoration для расстояний между элементами
        rvBills.addItemDecoration(new ItemOffsetDecoration(calculateItemWidth(rvBills, 2), 20));
        rvTransactions.addItemDecoration(new ItemOffsetDecoration(calculateItemWidth(rvTransactions, 2), 20));
        rvExchangeRates.addItemDecoration(new ItemOffsetDecoration(calculateItemWidth(rvExchangeRates, 3), 20));

    }

    // Класс для установки расстояний между элементами RecyclerView
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
            if (position / (itemCount-1) != 1) {
                // Not the last item in each row
                outRect.right = dpToPx(spaceBetweenItems);
            }
        }
    }


    private int dpToPx(int dp) {
        return (int) (dp * getBaseContext().getResources().getDisplayMetrics().density);
    }

    private int calculateItemWidth(RecyclerView recyclerView, int itemCount) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int spaceBetweenItems = dpToPx(20); // Adjust this as needed
        return (screenWidth - spaceBetweenItems * (itemCount - 1)) / itemCount;
    }


}
