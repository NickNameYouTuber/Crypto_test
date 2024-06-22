package com.nicorp.crypto_test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nicorp.crypto_test.CryptoAdapter;
import com.nicorp.crypto_test.CryptoItem;
import com.nicorp.crypto_test.R;

import org.json.JSONObject;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BalanceActivity extends AppCompatActivity {

    private Web3j web3j;
    private TextView balanceTextView;
    private TextView balanceValueTextView;
    private String walletAddress;
    private RecyclerView cryptoRecyclerView;
    private CryptoAdapter cryptoAdapter;
    private RecyclerView accountsRecyclerView;
    private AccountAdapter accountAdapter;
    private List<AccountItem> accountList;
    private Button addAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_balance);

        balanceTextView = findViewById(R.id.balanceTextView);
        balanceValueTextView = findViewById(R.id.balanceValueTextView);
        cryptoRecyclerView = findViewById(R.id.cryptoRecyclerView);
        accountsRecyclerView = findViewById(R.id.accountsRecyclerView);
        addAccountButton = findViewById(R.id.addAccountButton);

        // Установка LayoutManager'а для RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cryptoRecyclerView.setLayoutManager(layoutManager);

        // Установка адаптера для RecyclerView
        cryptoAdapter = new CryptoAdapter(new ArrayList<>());
        cryptoRecyclerView.setAdapter(cryptoAdapter);

        web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/89a9b4cb3f984f06855aeb49aafb0c57"));

        walletAddress = getIntent().getStringExtra("walletAddress");

        SharedPreferences sharedPreferences = getSharedPreferences("CryptoPrefs", Context.MODE_PRIVATE);
        int selectedAccountPosition = sharedPreferences.getInt("selected_account", -1);

        accountList = loadAccountsFromPreferences(sharedPreferences);
        if (accountList.isEmpty()) {
            // Добавляем основной счет при первом запуске
            walletAddress = getIntent().getStringExtra("walletAddress");
            accountList.add(new AccountItem("Main Account", "Balance: 0 $", "Main Currency"));
        }

        accountAdapter = new AccountAdapter(this, accountList);
        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        accountsRecyclerView.setAdapter(accountAdapter);

        if (selectedAccountPosition != -1) {
            accountAdapter.notifyItemChanged(selectedAccountPosition);
        }

        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика для добавления нового счета
                showAddAccountDialog();
            }
        });


        new FetchBalanceTask().execute(walletAddress);
//        new GetPriceTask().execute();
        new FetchCryptoDataTask().execute();
    }

    private void showAddAccountDialog() {
        AddAccountDialog dialog = new AddAccountDialog();
        dialog.setOnAccountAddedListener(new AddAccountDialog.OnAccountAddedListener() {
            @Override
            public void onAccountAdded(AccountItem accountItem) {
                accountList.add(accountItem);
                accountAdapter.notifyItemInserted(accountList.size() - 1);
                SharedPreferences sharedPreferences = getSharedPreferences("CryptoPrefs", Context.MODE_PRIVATE);
                saveAccountsToPreferences(sharedPreferences, accountList);
            }
        });
        dialog.show(getSupportFragmentManager(), "AddAccountDialog");
    }

    private List<AccountItem> loadAccountsFromPreferences(SharedPreferences sharedPreferences) {
        List<AccountItem> accounts = new ArrayList<>();
        int accountCount = sharedPreferences.getInt("account_count", 0);
        for (int i = 0; i < accountCount; i++) {
            String name = sharedPreferences.getString("account_" + i + "_name", "");
            String balance = sharedPreferences.getString("account_" + i + "_balance", "");
            String currency = sharedPreferences.getString("account_" + i + "_currency", "");
            accounts.add(new AccountItem(name, currency, balance ));
        }
        return accounts;
    }

    private void saveAccountsToPreferences(SharedPreferences sharedPreferences, List<AccountItem> accounts) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("account_count", accounts.size());
        for (int i = 0; i < accounts.size(); i++) {
            AccountItem account = accounts.get(i);
            editor.putString("account_" + i + "_name", account.getName());
            editor.putString("account_" + i + "_address", account.getAddress());
            editor.putString("account_" + i + "_currency", account.getCurrency());
        }
        editor.apply();
    }

    private void onAccountSelected(AccountItem accountItem) {
        updateSelectedAccount(accountItem);
    }

    public void updateSelectedAccount(AccountItem accountItem) {
        balanceTextView.setText("Balance:");
        new FetchBalanceTask().execute(accountItem.getAddress());
        new FetchCryptoDataTask().execute();
    }

    private class FetchBalanceTask extends AsyncTask<String, Void, BigDecimal> {
        private String currency = "USD";
        @Override
        protected BigDecimal doInBackground(String... addresses) {
            try {
                System.out.println(addresses[0]);
                EthGetBalance ethGetBalance = web3j.ethGetBalance(addresses[0], DefaultBlockParameterName.LATEST).send();
                BigInteger wei = ethGetBalance.getBalance();
                currency = getCurrencyFromAddress(addresses[0]);
                return Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER);
            } catch (Exception e) {
                e.printStackTrace();
                return BigDecimal.ZERO;
            }
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(BigDecimal balance) {
            balanceTextView.setText("Balance:");
            balanceValueTextView.setText(balance.toString() + " " + currency + "    ("  + (balance.divide(BigDecimal.TEN.pow(100))) +" QC)");
        }
    }

    private String getCurrencyFromAddress(String address) {
        // Пример логики для определения валюты на основе адреса
        if (address.startsWith("0x")) {
            return "ETH"; // Все Ethereum-адреса начинаются с '0x'
        } else if (address.startsWith("1") || address.startsWith("3")) {
            return "BTC"; // Bitcoin-адреса начинаются с '1' или '3'
        } else if (address.startsWith("t")) {
            return "USDT"; // Пример для Tether
        } else {
            return "Unknown"; // Неизвестная валюта
        }
    }
//    private class GetPriceTask extends AsyncTask<Void, Void, JSONObject> {
//        @Override
//        protected JSONObject doInBackground(Void... voids) {
//            try {
//                URL url = new URL("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,tether&vs_currencies=usd");
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                Scanner scanner = new Scanner(url.openStream());
//                StringBuilder response = new StringBuilder();
//                while (scanner.hasNext()) {
//                    response.append(scanner.nextLine());
//                }
//                scanner.close();
//                return new JSONObject(response.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(JSONObject jsonObject) {
//            if (jsonObject != null) {
//                try {
//                    JSONObject bitcoin = jsonObject.getJSONObject("bitcoin");
//                    JSONObject ethereum = jsonObject.getJSONObject("ethereum");
//                    JSONObject tether = jsonObject.getJSONObject("tether");
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private class FetchCryptoDataTask extends AsyncTask<Void, Void, List<CryptoItem>> {
        @Override
        protected List<CryptoItem> doInBackground(Void... voids) {
            try {
                // Создание объекта Retrofit
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.coingecko.com/api/v3/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                // Создание экземпляра сервиса
                CryptoService service = retrofit.create(CryptoService.class);

                // Выполнение запроса к API
                Call<CryptoData> call = service.getCryptoData();
                Response<CryptoData> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    CryptoData cryptoData = response.body();
                    List<CryptoItem> cryptoItems = new ArrayList<>();

                    cryptoItems.add(new CryptoItem(R.drawable.qcoin, "QCoin", "$0.10"));
                    cryptoItems.add(new CryptoItem(R.drawable.bitcoin, "Bitcoin", String.format("$%,.2f", cryptoData.getBitcoin().getPrice())));
                    cryptoItems.add(new CryptoItem(R.drawable.ethereum, "Ethereum", String.format("$%,.2f", cryptoData.getEthereum().getPrice())));
                    cryptoItems.add(new CryptoItem(R.drawable.tether, "Tether", String.format("$%,.2f", cryptoData.getTether().getPrice())));

                    return cryptoItems;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<CryptoItem> cryptoItems) {
            if (cryptoItems != null) {
                cryptoAdapter.setCryptoItems(cryptoItems);
            }
        }
    }
}