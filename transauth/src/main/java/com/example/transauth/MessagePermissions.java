package com.example.transauth;

import java.util.HashMap;
import java.util.Map;

public class MessagePermissions {
    public static final String GET_USERNAME = "GET_USERNAME";
    public static final String GET_LOGIN = "GET_LOGIN";
    public static final String GET_PASSWORD = "GET_PASSWORD";
    public static final String GET_TOKEN = "GET_TOKEN";
    public static final String GET_EMAIL = "GET_EMAIL";

    private static final Map<String, String> permissionDescriptions = new HashMap<>();

    static {
        permissionDescriptions.put(GET_USERNAME, "К вашему имени");
        permissionDescriptions.put(GET_LOGIN, "К вашему логину");
        permissionDescriptions.put(GET_PASSWORD, "К паролю профиля");
        permissionDescriptions.put(GET_TOKEN, "К токену профиля");
        permissionDescriptions.put(GET_EMAIL, "К вашему email");
    }

    /**
     * Возвращает описание для данного разрешения.
     *
     * @param permission Разрешение.
     * @return Описание разрешения или null, если разрешение не найдено.
     */
    public static String getDescription(String permission) {
        return permissionDescriptions.get(permission);
    }
}
