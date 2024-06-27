package com.nicorp.crypto_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {

    private static final int START_DELAY = 1000; // 1 секунда ничего не происходит
    private static final int FADE_IN_DURATION = 2000; // 2 секунды появления логотипа
    private static final int WAIT_DURATION = 2000; // 2 секунды ожидания
    private static final int FADE_OUT_DURATION = 2000; // 2 секунды плавный переход на другое активити

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AllHelpersSetup.setup(this, R.layout.activity_splash, false);
//        // Установка темы в зависимости от системной темы устройства
//        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
//        switch (currentNightMode) {
//            case android.content.res.Configuration.UI_MODE_NIGHT_NO:
//                // Нет ночного режима, используем светлую тему
//                setTheme(R.style.AppTheme_Light);
//                System.out.println("No night mode");
//                break;
//            case android.content.res.Configuration.UI_MODE_NIGHT_YES:
//                // Есть ночной режим, используем темную тему
//                setTheme(R.style.AppTheme_Dark);
//                System.out.println("Yes night mode");
//                break;
//            default:
//                // Используем светлую тему по умолчанию
//                setTheme(R.style.AppTheme_Dark);
//                System.out.println("Default night mode");
//                break;
//        }

        // Определение логотипа в зависимости от текущей темы
        int logoResId;
        if (isDarkThemeSelected()) {
            logoResId = R.drawable.test_logo_b; // Логотип для темной темы
        } else {
            logoResId = R.drawable.test_logo_w; // Логотип для светлой темы
        }

        ImageView logo = findViewById(R.id.logoImageView);
        logo.setVisibility(View.GONE); // Скрыть логотип по умолчанию
        logo.setImageResource(logoResId); // Установка логотипа


        new Handler().postDelayed(() -> startLogoAnimation(logo), START_DELAY);
    }

    private boolean isDarkThemeSelected() {
        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    private void startLogoAnimation(ImageView logo) {
        logo.setVisibility(View.VISIBLE); // Показать логотип перед началом анимации
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(FADE_IN_DURATION); // 2 секунды появления логотипа
        fadeIn.setFillAfter(true);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Ничего не делаем
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(() -> startFadeOutAnimation(logo), WAIT_DURATION); // 2 секунды ожидания
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Ничего не делаем
            }
        });
        logo.startAnimation(fadeIn);
    }

    private void startFadeOutAnimation(ImageView logo) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(FADE_OUT_DURATION); // 2 секунды плавный переход на другое активити
        fadeOut.setFillAfter(true);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Ничего не делаем
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                checkLoginStatus(); // Переход к следующей активности
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Ничего не делаем
            }
        });
        logo.startAnimation(fadeOut);
    }

    private void checkLoginStatus() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        Intent intent;
        if (isLoggedIn) {
            intent = new Intent(SplashActivity.this, BalanceActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
