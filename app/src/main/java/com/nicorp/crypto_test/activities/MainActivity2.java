package com.nicorp.crypto_test.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.nicorp.crypto_test.R;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        // Expose a Java object to JavaScript
        webView.addJavascriptInterface(new WebAppInterface(), "android");

        webView.loadUrl("file:///android_asset/metamask_connect.html");
    }

    public class WebAppInterface {
        @android.webkit.JavascriptInterface
        public void onAccountConnected(String account) {
            runOnUiThread(() -> Toast.makeText(MainActivity2.this, "Connected account: " + account, Toast.LENGTH_SHORT).show());
        }
    }
}