// src/main/java/com/example/transauth/MessageManager.java

package com.example.transauth;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageManager {
    public static final String ACTION_SEND_MESSAGE = "com.example.messaging.ACTION_SEND_MESSAGE";
    public static final String EXTRA_MESSAGE_TAG = "com.example.messaging.EXTRA_MESSAGE_TAG";
    public static final String EXTRA_MESSAGE_PERMISSION = "com.example.messaging.EXTRA_MESSAGE_PERMISSION";
    public static final String EXTRA_MESSAGE_MAP = "com.example.messaging.EXTRA_MESSAGE_MAP";
    public static final String EXTRA_SENDER_PACKAGE = "com.example.messaging.EXTRA_SENDER_PACKAGE";
    private static final Gson gson = new Gson();

    public static void sendMessage(Context context, Map<String, String> message, String tag, String permission) {
        Intent intent = new Intent(ACTION_SEND_MESSAGE);
        intent.putExtra(EXTRA_MESSAGE_MAP, gson.toJson(message));
        intent.putExtra(EXTRA_MESSAGE_TAG, tag);
        intent.putExtra(EXTRA_MESSAGE_PERMISSION, permission);
        intent.putExtra(EXTRA_SENDER_PACKAGE, context.getPackageName());

        List<String> availableApps = readAvailableApps(context);
        boolean sent = false;

        for (String appPackage : availableApps) {
            if (isAppInstalled(context, appPackage)) {
                intent.setPackage(appPackage);
                context.sendBroadcast(intent);
                Log.d("MessageManager", "Message sent to app: " + appPackage);
                sent = true; // Успешно отправили сообщение
                break; // Прекращаем отправку после первого успешного отправления
            }
        }

        if (!sent) {
            // Если ни одно из приложений не найдено или ни одно не ответило
            Log.d("MessageManager", "No app found to send message");
            sendMessageToSelf(context, "ДАННЫХ НЕТ");
        }
    }

    private static void sendMessageToSelf(Context context, String message) {
        Intent intent = new Intent(ACTION_SEND_MESSAGE);
        intent.setPackage(context.getPackageName()); // Отправляем сообщение самому себе
        intent.putExtra(EXTRA_MESSAGE_MAP, message);
        intent.putExtra(EXTRA_MESSAGE_TAG, MessageTags.ENTER_FROM);
        context.sendBroadcast(intent);
    }

    private static List<String> readAvailableApps(Context context) {
        List<String> availableApps = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open("available_apps.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                json.append(line);
            }
            Type type = new TypeToken<List<String>>() {}.getType();
            availableApps = gson.fromJson(json.toString(), type);
        } catch (Exception e) {
            Log.e("MessageManager", "Error reading available_apps.json", e);
        }
        return availableApps;
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static Map<String, String> extractMessageMapFromIntent(Intent intent) {
        String json = intent.getStringExtra(EXTRA_MESSAGE_MAP);
        return gson.fromJson(json, Map.class);
    }

    public static String extractMessageTagFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_MESSAGE_TAG);
    }

    public static String extractMessagePermissionFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_MESSAGE_PERMISSION);
    }

    public static String extractSenderPackageFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_SENDER_PACKAGE);
    }
}
