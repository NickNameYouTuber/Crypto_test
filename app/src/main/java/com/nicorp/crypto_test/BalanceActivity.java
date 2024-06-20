package com.nicorp.crypto_test;

import android.os.AsyncTask;
import android.os.Bundle;
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
    private TextView btcRateTextView;
    private TextView ethRateTextView;
    private TextView usdtRateTextView;
    private String walletAddress;
    private RecyclerView cryptoRecyclerView;
    private CryptoAdapter cryptoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        balanceTextView = findViewById(R.id.balanceTextView);
        btcRateTextView = findViewById(R.id.btcRateTextView);
        ethRateTextView = findViewById(R.id.ethRateTextView);
        usdtRateTextView = findViewById(R.id.usdtRateTextView);
        cryptoRecyclerView = findViewById(R.id.cryptoRecyclerView);

        // Установка LayoutManager'а для RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cryptoRecyclerView.setLayoutManager(layoutManager);

        // Установка адаптера для RecyclerView
        cryptoAdapter = new CryptoAdapter(new ArrayList<>());
        cryptoRecyclerView.setAdapter(cryptoAdapter);

        web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/YOUR_INFURA_PROJECT_ID"));

        walletAddress = getIntent().getStringExtra("walletAddress");

        new FetchBalanceTask().execute(walletAddress);
        new GetPriceTask().execute();
        new FetchCryptoDataTask().execute();
    }

    private class FetchBalanceTask extends AsyncTask<String, Void, BigDecimal> {
        @Override
        protected BigDecimal doInBackground(String... addresses) {
            try {
                EthGetBalance ethGetBalance = web3j.ethGetBalance(addresses[0], DefaultBlockParameterName.LATEST).send();
                BigInteger wei = ethGetBalance.getBalance();
                return Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER);
            } catch (Exception e) {
                e.printStackTrace();
                return BigDecimal.ZERO;
            }
        }

        @Override
        protected void onPostExecute(BigDecimal balance) {
            balanceTextView.setText("Balance: \n" + balance.toString() + " $    ("  + (balance.divide(BigDecimal.TEN.pow(100))) +" QC)");
        }
    }
    private class GetPriceTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,tether&vs_currencies=usd");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                Scanner scanner = new Scanner(url.openStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                return new JSONObject(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    JSONObject bitcoin = jsonObject.getJSONObject("bitcoin");
                    JSONObject ethereum = jsonObject.getJSONObject("ethereum");
                    JSONObject tether = jsonObject.getJSONObject("tether");

                    btcRateTextView.setText("BTC Price: $" + bitcoin.getString("usd"));
                    ethRateTextView.setText("ETH Price: $" + ethereum.getString("usd"));
                    usdtRateTextView.setText("USDT Price: $" + tether.getString("usd"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

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