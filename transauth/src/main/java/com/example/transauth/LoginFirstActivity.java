package com.example.transauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginFirstActivity extends AppCompatActivity {

    private static final int YANDEX_OAUTH_REQUEST = 1;
    private static final int GOOGLE_SIGN_IN_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_first);

        ImageView yandexSignInBtn = findViewById(R.id.yandexSignInBtn);
        yandexSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginFirstActivity.this, WebViewActivity.class);
                intent.putExtra("provider", "YANDEX");
                startActivityForResult(intent, YANDEX_OAUTH_REQUEST);
            }
        });

        ImageView googleSignInBtn = findViewById(R.id.googleSignInBtn);
        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginFirstActivity.this, WebViewActivity.class);
                intent.putExtra("provider", "GOOGLE");
                startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        int currentNightMode = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(this, R.style.Theme_QryptApp_Light);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(this, R.style.Theme_QryptApp_Dark);
                break;
            default:
                setTheme(this, R.style.Theme_QryptApp_Light);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YANDEX_OAUTH_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                String token = data.getStringExtra("token");
                Log.d("YANDEX_TOKEN", "Token " + token);
            }
        } else if (requestCode == GOOGLE_SIGN_IN_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                String token = data.getStringExtra("token");
                Log.d("GOOGLE_TOKEN", "Token " + token);
            }
        }
    }

    private static void setTheme(Activity activity, int theme) {
        activity.setTheme(theme);
    }
}
