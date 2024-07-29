package com.nicorp.crypto_test.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.objects.AccountItem;

import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.Wallet;
import org.web3j.crypto.Keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImportBTCWalletActivity extends AppCompatActivity {

    private EditText mnemonicEditText;
    private Button importButton;
    private NetworkParameters params = MainNetParams.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_btc_wallet);

        mnemonicEditText = findViewById(R.id.mnemonicEditText);
        importButton = findViewById(R.id.importButton);

        // В вашем OnClickListener для кнопки импорта
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mnemonic = mnemonicEditText.getText().toString().trim();
                try {
                    List<String> mnemonicCode = Arrays.asList(mnemonic.split(" "));
                    MnemonicCode.INSTANCE.check(mnemonicCode);
                    DeterministicSeed seed = new DeterministicSeed(mnemonicCode, null, "", 0);

                    // Создаем HD кошелек с помощью bitcoinj
                    Wallet wallet = Wallet.fromSeed(params, seed);

                    // Получаем мастер-ключ
                    DeterministicKey masterKey = wallet.getWatchingKey();

                    // Получаем первый внутренний ключ (например, m/0H/0)
                    DeterministicKey accountKey = wallet.freshReceiveKey();

                    // Получаем адрес Ethereum из публичного ключа
                    String ethereumAddress = Keys.toChecksumAddress(Keys.getAddress(accountKey.getPublicKeyAsHex()));
                    saveMainAccount(ethereumAddress);
                    // Передача адреса в другую активность
//                    Intent intent = new Intent(ImportBTCWalletActivity.this, BalanceActivity.class);
//                    intent.putExtra("walletAddress", ethereumAddress);
//                    System.out.println(ethereumAddress);
//                    startActivity(intent);
//                    finish();

                } catch (MnemonicException | IllegalArgumentException e) {
                    e.printStackTrace();
                    // Обработка ошибок при некорректной мнемонической фразе
                }
            }
        });
    }

    private void saveMainAccount(String walletAddress) {
        SharedPreferences sharedPreferences = getSharedPreferences("CryptoPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<AccountItem> accountList = new ArrayList<>();
        accountList.add(new AccountItem("Main Account", "USD", walletAddress));

        editor.putInt("account_count", accountList.size());
        for (int i = 0; i < accountList.size(); i++) {
            AccountItem account = accountList.get(i);
            editor.putString("account_" + i + "_name", account.getName());
            editor.putString("account_" + i + "_address", account.getAddress());
            editor.putString("account_" + i + "_currency", account.getCurrency());
        }
        editor.apply();
    }
}
