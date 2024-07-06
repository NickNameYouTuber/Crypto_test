package com.example.transauth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class LoginInfoActivity extends AppCompatActivity {
    private TransAuthLoginButton transAuthLoginButton;
    private TextView permissionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);

        transAuthLoginButton = findViewById(R.id.transAuthLoginButton);
        permissionsList = findViewById(R.id.permissionsList);

        // Get accountName from intent
        String accountName = getIntent().getStringExtra("Name");
        if (accountName != null) {
            transAuthLoginButton.setText("Войти как " + accountName);
        }

        // Get permissions from intent
        List<String> permissions = Arrays.asList(TransAuth.getPermissionsArray());
        if (permissions != null) {
            StringBuilder permissionsDescription = new StringBuilder();
            for (String permission : permissions) {
                String description = MessagePermissions.getDescription(permission);
                if (description != null) {
                    permissionsDescription.append(description).append("\n");
                }
            }
            permissionsList.setText(permissionsDescription.toString().trim());
        }

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