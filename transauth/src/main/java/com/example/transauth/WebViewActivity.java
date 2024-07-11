//package com.example.transauth;
//
//import static com.example.transauth.TransAuthUserAdapter.getUserFromYandex;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.gson.Gson;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class WebViewActivity extends AppCompatActivity {
//
//    private static final String REDIRECT_URI = "https://oauth.yandex.ru/verification_code";
//    private static final String CLIENT_ID = "f948e82c3ffe487a947ec15dc6006150";
//    private static final String AUTH_URL = "https://oauth.yandex.com/authorize?response_type=token&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI;
//
//    @SuppressLint("SetJavaScriptEnabled")
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        WebView webView = new WebView(this);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                String url = request.getUrl().toString();
//                if (url.startsWith(REDIRECT_URI)) {
//                    handleRedirect(url);
//                    return true;
//                }
//                return super.shouldOverrideUrlLoading(view, request);
//            }
//        });
//        webView.loadUrl(AUTH_URL);
//        setContentView(webView);
//    }
//
//    private void handleRedirect(String url) {
//        // Extract the access token from the URL
//        if (url.contains("access_token=")) {
//            String token = url.split("access_token=")[1].split("&")[0];
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra("token", token);
//
//            getUserFromYandex(token, new TransAuthUserAdapter.UserCallback () {
//
//                @Override
//                public void onUserReceived(TransAuthUser user) {
//                    MessageReceiver receiver = new MessageReceiver(null);
//                    receiver.writeFile(getBaseContext(), user);
//                }
//            });
//
//            setResult(RESULT_OK, resultIntent);
//            finish();
//        } else {
//            // Handle error
//            setResult(RESULT_CANCELED);
//            finish();
//        }
//    }
//}

package com.example.transauth;

import static com.example.transauth.TransAuthUserAdapter.getUserFromYandex;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private static final String YANDEX_REDIRECT_URI = "https://oauth.yandex.ru/verification_code";
    private static final String YANDEX_CLIENT_ID = "f948e82c3ffe487a947ec15dc6006150";
    private static final String YANDEX_AUTH_URL = "https://oauth.yandex.com/authorize?response_type=token&client_id=" + YANDEX_CLIENT_ID + "&redirect_uri=" + YANDEX_REDIRECT_URI;

    private static final String GOOGLE_REDIRECT_URI = "https://accounts.google.com/o/oauth2/v2/auth";  // Replace with your Google Redirect URI
    private static final String GOOGLE_CLIENT_ID = "923460132579-b2a7fld1fftaeuh4uqdd6rvh1f8r1jsp.apps.googleusercontent.com";  // Replace with your Google Client ID
    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth?response_type=token&client_id=" + GOOGLE_CLIENT_ID + "&redirect_uri=" + GOOGLE_REDIRECT_URI + "&scope=email%20profile";

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
                if (url.startsWith(YANDEX_REDIRECT_URI)) {
                    handleRedirect(url, "YANDEX");
                    return true;
                } else if (url.startsWith(GOOGLE_REDIRECT_URI)) {
                    handleRedirect(url, "GOOGLE");
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        Intent intent = getIntent();
        String provider = intent.getStringExtra("provider");
        if (provider != null && provider.equals("GOOGLE")) {
            webView.loadUrl(GOOGLE_AUTH_URL);
        } else {
            webView.loadUrl(YANDEX_AUTH_URL);
        }

        setContentView(webView);
    }

    private void handleRedirect(String url, String provider) {
        if (url.contains("access_token=")) {
            String token = url.split("access_token=")[1].split("&")[0];
            Intent resultIntent = new Intent();
            resultIntent.putExtra("token", token);

            if (provider.equals("YANDEX")) {
                getUserFromYandex(token, new TransAuthUserAdapter.UserCallback() {
                    @Override
                    public void onUserReceived(TransAuthUser user) {
//                        // Сохраняем пользователя в базу данных или другой механизм хранения
//                        TransAuthUserDatabaseHelper db = new TransAuthUserDatabaseHelper(WebViewActivity.this);
//                        db.addUser(user);
                    }
                });
            }

            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

}
