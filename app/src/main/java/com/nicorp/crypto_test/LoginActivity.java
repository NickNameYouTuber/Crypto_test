package com.nicorp.crypto_test;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText walletAddressEditText;
    private EditText passwordEditText;
    private Button connectWalletButton;
    private Button createWalletButton;
    private TextView addressTextView;
    private Button copyAddressButton;
    private ImageView logoImageView;
    private Button importBTCWalletButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_login);

        setupBouncyCastle();

        walletAddressEditText = findViewById(R.id.walletAddressEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        connectWalletButton = findViewById(R.id.connectWalletButton);
        createWalletButton = findViewById(R.id.createWalletButton);
        addressTextView = findViewById(R.id.addressTextView);
        copyAddressButton = findViewById(R.id.copyAddressButton);
        logoImageView = findViewById(R.id.logoImageView);

        // Определение логотипа в зависимости от текущей темы
        int logoResId;
        if (isDarkThemeSelected()) {
            logoResId = R.drawable.test_logo_b; // Логотип для темной темы
        } else {
            logoResId = R.drawable.test_logo_w; // Логотип для светлой темы
        }

        logoImageView.setImageResource(logoResId);

        connectWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String walletAddress = walletAddressEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (!walletAddress.isEmpty() && !password.isEmpty()) {
                    saveMainAccount(walletAddress);
                    new LoadWalletTask(walletAddress, password).execute();
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter wallet address and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        createWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateWalletTask().execute();
            }
        });

        copyAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressTextView.getText().toString();
                if (!address.isEmpty()) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Wallet Address", address);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(LoginActivity.this, "Address copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        importBTCWalletButton = findViewById(R.id.importBTCWalletButton);

        importBTCWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ImportBTCWalletActivity.class);
                startActivityForResult(intent, 1);
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

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Добавляем Bouncy Castle как провайдер безопасности, если он ещё не добавлен
            Security.addProvider(new BouncyCastleProvider());
        } else {
            // Обновляем существующий провайдер
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private class CreateWalletTask extends AsyncTask<Void, Void, String> {

        private String walletAddress;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Создаем кошелек и получаем адрес
                File destinationDirectory = getFilesDir();
                    String walletFileName = generateLightWalletFile("your_password", destinationDirectory);
                walletAddress = getAddressFromWalletFile(new File(destinationDirectory, walletFileName));
                return walletFileName;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                addressTextView.setText(walletAddress); // Отображаем адрес в TextView
                Toast.makeText(LoginActivity.this, "Wallet created: " + result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Error creating wallet", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class LoadWalletTask extends AsyncTask<Void, Void, Credentials> {

        private String walletAddress;
        private String password;

        LoadWalletTask(String walletAddress, String password) {
            this.walletAddress = walletAddress;
            this.password = password;
        }

        @Override
        protected Credentials doInBackground(Void... voids) {
            try {
                // Загружаем файл кошелька
                File walletFile = new File(getFilesDir(), "wallet.json");
                Credentials credentials = WalletUtils.loadCredentials(password, walletFile);
                return credentials;
            } catch (IOException | CipherException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Credentials credentials) {
            if (credentials != null) {
                Toast.makeText(LoginActivity.this, "Wallet loaded successfully", Toast.LENGTH_LONG).show();
                saveLoginState();
                Intent intent = new Intent(LoginActivity.this, BalanceActivity.class);
                intent.putExtra("walletAddress", credentials.getAddress());
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Error loading wallet", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String generateLightWalletFile(String password, File destinationDirectory) throws Exception {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();

        // Уменьшаем параметры Scrypt
        int n = 1 << 10; // уменьшенный параметр N (1024)
        int r = 8;
        int p = 1;

        WalletFile walletFile = Wallet.create(password, ecKeyPair, n, p);

        // Сохраняем кошелек в файл
        File walletFilePath = new File(destinationDirectory, "wallet.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(walletFilePath, walletFile);

        return walletFilePath.getName(); // Возвращаем имя файла
    }

    private String getAddressFromWalletFile(File walletFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        WalletFile walletFileObject = objectMapper.readValue(walletFile, WalletFile.class);
        return "0x" + walletFileObject.getAddress();
    }

    private void saveLoginState() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private boolean isDarkThemeSelected() {
        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String walletAddress = data.getStringExtra("walletAddress");
            Intent intent = new Intent(LoginActivity.this, BalanceActivity.class);
            intent.putExtra("walletAddress", walletAddress);
            intent.putExtra("currency", "BTC");
            startActivity(intent);
        }
    }
}
