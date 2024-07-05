package com.example.transauth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);

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

    private static void setTheme(Activity activity, int theme) {
        activity.setTheme(theme);
    }
}