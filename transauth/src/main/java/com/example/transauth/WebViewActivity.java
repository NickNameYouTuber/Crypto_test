package com.example.transauth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {

    private static final String REDIRECT_URI = "https://oauth.yandex.ru/verification_code";
    private static final String CLIENT_ID = "f948e82c3ffe487a947ec15dc6006150";
    private static final String AUTH_URL = "https://oauth.yandex.com/authorize?response_type=token&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith(REDIRECT_URI)) {
                    handleRedirect(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        webView.loadUrl(AUTH_URL);
        setContentView(webView);
    }

    private void handleRedirect(String url) {
        // Extract the access token from the URL
        if (url.contains("access_token=")) {
            String token = url.split("access_token=")[1].split("&")[0];
            Intent resultIntent = new Intent();
            resultIntent.putExtra("token", token);

            TransAuthUser user = new TransAuthUser();
            user.setTokens(new HashMap<>());
            user.getTokens().put("yandex_token", token);

            MessageReceiver receiver = new MessageReceiver(null);
            receiver.writeFile(this, user);

            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            // Handle error
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
