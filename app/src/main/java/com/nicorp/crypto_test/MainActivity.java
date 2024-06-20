package com.nicorp.crypto_test;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.DefaultBlockParameterName;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    private Web3j web3j;
    private TextView balanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Go to login activity
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);

        balanceTextView = findViewById(R.id.balanceTextView);

        // Инициализация Web3j
        web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/89a9b4cb3f984f06855aeb49aafb0c57"));

        // Получение баланса
        new GetBalanceTask().execute("0xAEAe214B55DB8b124a72E07bd017566a12E26df7");
    }

    private class GetBalanceTask extends AsyncTask<String, Void, BigDecimal> {
        @Override
        protected BigDecimal doInBackground(String... strings) {
            String address = strings[0];
            try {
                EthGetBalance ethGetBalance = web3j
                        .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                        .send();
                BigInteger wei = ethGetBalance.getBalance();
                return new BigDecimal(wei).divide(BigDecimal.TEN.pow(18));
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
}