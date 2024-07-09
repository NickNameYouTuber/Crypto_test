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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_first);

        ImageView yandexSignInBtn = findViewById(R.id.yandexSignInBtn);
        yandexSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginFirstActivity.this, WebViewActivity.class);
                startActivityForResult(intent, YANDEX_OAUTH_REQUEST);
            }
        });

        // Existing code for theme setup...
        SharedPreferences sharedPreferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        int currentNightMode = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Светлая тема
                setTheme(this, R.style.Theme_QryptApp_Light);
                System.out.println("Theme applied: " + "R.style.Theme_QryptApp_Light");
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Темная тема
                setTheme(this, R.style.Theme_QryptApp_Dark);
                System.out.println("Theme applied: " + "R.style.Theme_QryptApp_Dark");
                break;
            default:
                // Тема по умолчанию
                setTheme(this, R.style.Theme_QryptApp_Light);
                System.out.println("Theme applied: " + "savedTheme");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YANDEX_OAUTH_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                String token = data.getStringExtra("token");

                Log.d("YANDEX_TOKEN","Token "+token);
                // Handle the token, for example, send it to your server for authentication
            } else {
                // Handle error
            }
        }
    }

    private static void setTheme(Activity activity, int theme) {
        activity.setTheme(theme);
    }
}
