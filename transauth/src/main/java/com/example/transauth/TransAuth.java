package com.example.transauth;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransAuth {
    private static final String TAG = "TransAuth";
    private static String TransAuthToken;
    private String testToken = "1234";
    private static TransAuthUser user;
    private static List<String> permissions = new ArrayList<>();

    // --- Публичные конструкторы ---
    public TransAuth(String TransAuthToken) {
        this.TransAuthToken = TransAuthToken;
        checkToken();
        user = new TransAuthUser();
    }

    // --- Публичные методы ---

    /**
     * Добавление списка разрешений на отправку сообщений
     * @param permissions список разрешений
     *                    example: "GET_LOGIN", "GET_MAIL"
     */
    public static void addPermissions(String... permissions) {
        TransAuth.permissions.addAll(Arrays.asList(permissions));
        for (String permission : permissions) {
            Log.d(TAG, "Added permission: " + permission);
        }
    }

    /**
     * Получение списка разрешений
     * @return список разрешений
     */
    public static String[] getPermissionsArray() {
        return permissions.toArray(new String[0]);
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

    public static TransAuthUser getUser() {
        return user;
    }

    public static void setUser(TransAuthUser user) {
        TransAuth.user = user;
    }
}
