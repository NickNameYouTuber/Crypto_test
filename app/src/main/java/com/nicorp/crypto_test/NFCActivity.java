package com.nicorp.crypto_test;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

public class NFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        nfcAdapter = nfcManager.getDefaultAdapter();

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC не поддерживается на этом устройстве", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Включите NFC в настройках", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
        intentFiltersArray = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)};
        techListsArray = new String[][]{new String[]{IsoDep.class.getName()}};

        Button addCardButton = findViewById(R.id.addCardButton);
        addCardButton.setOnClickListener(v -> addCard());

        Button makePaymentButton = findViewById(R.id.makePaymentButton);
        makePaymentButton.setOnClickListener(v -> makePayment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            IsoDep isoDep = IsoDep.get(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
            if (isoDep != null) {
                handleNfcTag(isoDep);
            }
        }
    }

    private void handleNfcTag(IsoDep isoDep) {
        try {
            isoDep.connect();
            byte[] command = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x07, (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x10, (byte) 0x10, (byte) 0x00};
            byte[] result = isoDep.transceive(command);
            Log.d("NFC", "Response: " + bytesToHex(result));
        } catch (Exception e) {
            Log.e("NFC", "Error handling NFC tag", e);
        } finally {
            try {
                isoDep.close();
            } catch (Exception e) {
                Log.e("NFC", "Error closing NFC tag", e);
            }
        }
    }

    private void addCard() {
        // Здесь должна быть логика для добавления карты
        Toast.makeText(this, "Добавление карты", Toast.LENGTH_SHORT).show();
    }

    private void makePayment() {
        // Здесь должна быть логика для осуществления платежа
        Toast.makeText(this, "Осуществление платежа", Toast.LENGTH_SHORT).show();
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
