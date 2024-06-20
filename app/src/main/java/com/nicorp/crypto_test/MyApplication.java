package com.nicorp.crypto_test;

import android.app.Application;
import android.content.res.Configuration;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Установка темы в зависимости от системной темы устройства
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {// Есть ночной режим, используем темную тему
            setTheme(R.style.AppTheme_Dark);
        } else {// Используем светлую тему по умолчанию
            setTheme(R.style.AppTheme_Light);
        }
    }
}
