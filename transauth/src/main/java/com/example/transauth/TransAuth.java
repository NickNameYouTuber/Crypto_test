package com.example.transauth;

import android.util.Log;

public class TransAuth {
    private static final String TAG = "TransAuth";

    private String TransAuthToken;
    private String testToken = "1234";

    // --- Публичные конструкторы ---
    public TransAuth(String TransAuthToken) {
        this.TransAuthToken = TransAuthToken;

        checkToken();
    }

    // --- Публичные методы ---

    // -------

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
