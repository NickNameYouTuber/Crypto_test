// NavigationHelper.java
package com.nicorp.crypto_test;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHelper {
    private static int previousItemId = R.id.nav_wallet;

    public static void setupBottomNavigation(Activity activity) {
        // Инициализируем BottomNavigationView
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNav);

        // Определяем текущую активность, чтобы выбрать правильный пункт меню
        int selectedItemId;
        if (activity instanceof FirstTabActivity) {
            selectedItemId = R.id.nav_wallet;
        } else if (activity instanceof AccountActivity) {
            selectedItemId = R.id.nav_profile;
        } else if (activity instanceof QRActivity) {
            selectedItemId = R.id.nav_qr;
        } else {
            selectedItemId = R.id.nav_wallet; // По умолчанию
        }
        bottomNav.setSelectedItemId(selectedItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            Class<?> targetActivity = null;
            if (item.getItemId() == R.id.nav_wallet) {
                targetActivity = FirstTabActivity.class;
            } else if (item.getItemId() == R.id.nav_qr) {
                targetActivity = QRActivity.class;
            } else if (item.getItemId() == R.id.nav_profile) {
                targetActivity = AccountActivity.class;
            }

            if (targetActivity != null && !activity.getClass().equals(targetActivity)) {
                Intent intent = new Intent(activity, targetActivity);
                activity.startActivity(intent);

                // Анимация в зависимости от направления
                if (item.getItemId() < previousItemId) {
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }

                previousItemId = item.getItemId(); // Обновляем предыдущий ID
                return true;
            }
            return false;
        });
    }
}