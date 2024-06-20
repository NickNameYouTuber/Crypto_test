package com.nicorp.crypto_test;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Scanner;

public class BalanceActivity extends AppCompatActivity {

    private Web3j web3j;
    private TextView balanceTextView;
    private TextView btcRateTextView;
    private TextView ethRateTextView;
    private TextView usdtRateTextView;
    private String walletAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        balanceTextView = findViewById(R.id.balanceTextView);
        btcRateTextView = findViewById(R.id.btcRateTextView);
        ethRateTextView = findViewById(R.id.ethRateTextView);
        usdtRateTextView = findViewById(R.id.usdtRateTextView);

        web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/YOUR_INFURA_PROJECT_ID"));

        walletAddress = getIntent().getStringExtra("walletAddress");

        new FetchBalanceTask().execute(walletAddress);
        new GetPriceTask().execute();
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
            balanceTextView.setText("Balance: " + balance.toString() + " ETH");
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
}