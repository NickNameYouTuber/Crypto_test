package com.example.transauth;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransAuth {
    private static final String TAG = "TransAuth";

    private String TransAuthToken;
    private String testToken = "1234";
    private List<String> permissions = new ArrayList<>();

    // --- Публичные конструкторы ---
    public TransAuth(String TransAuthToken) {
        this.TransAuthToken = TransAuthToken;
        checkToken();
    }

    // --- Публичные методы ---

    /**
     * Добавление списка разрешений на отправку сообщений
     * @param permissions список разрешений
     *                    example: "GET_LOGIN", "GET_MAIL"
     */
    public void addPermissions(String... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        for (String permission : permissions) {
            Log.d(TAG, "Added permission: " + permission);
        }
    }

    /**
     * Получение списка разрешений
     * @return список разрешений
     */
    public List<String> getPermissions() {
        return permissions;
    }

    // --- Приватные методы ---

    /**
     * Проверка токена
     */
    private void checkToken() {
        if (TransAuthToken.equals(testToken)) {
            Log.d(TAG, "Token is valid");
        } else {
            throw new IllegalArgumentException("Invalid token");
        }
    }
}
