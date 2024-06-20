package com.nicorp.crypto_test;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public class ThemeHelper {

    private static final String SELECTED_THEME = "selected_theme";

    public static void applyTheme(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Светлая тема
                setTheme(activity, R.style.AppTheme_Light);
                sharedPreferences.edit().putInt(SELECTED_THEME, R.style.AppTheme_Light).apply();
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Темная тема
                setTheme(activity, R.style.AppTheme_Dark);
                sharedPreferences.edit().putInt(SELECTED_THEME, R.style.AppTheme_Dark).apply();
                break;
            default:
                // Тема по умолчанию
                int savedTheme = sharedPreferences.getInt(SELECTED_THEME, R.style.AppTheme_Light);
                setTheme(activity, savedTheme);
                break;
        }
    }

    private static void setTheme(Activity activity, int theme) {
        activity.setTheme(theme);
    }
}