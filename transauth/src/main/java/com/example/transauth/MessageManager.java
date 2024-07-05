// src/main/java/com/example/transauth/MessageManager.java

package com.example.transauth;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MessageManager {
    private static final String TAG = "MessageManager";
    public static final String ACTION_SEND_MESSAGE = "com.example.messaging.ACTION_SEND_MESSAGE";
    public static final String EXTRA_MESSAGE_TAG = "com.example.messaging.EXTRA_MESSAGE_TAG";
    public static final String EXTRA_MESSAGE_PERMISSION = "com.example.messaging.EXTRA_MESSAGE_PERMISSION";
    public static final String EXTRA_MESSAGE_MAP = "com.example.messaging.EXTRA_MESSAGE_MAP";
    public static final String EXTRA_SENDER_PACKAGE = "com.example.messaging.EXTRA_SENDER_PACKAGE";
    private static final long RESPONSE_TIMEOUT = 5000; // 5 секунд на ожидание ответа

    private static final Gson gson = new Gson();
    private static Map<String, String> receivedMessage = null;
    private static CountDownLatch responseLatch = null;

    // --- Публичные методы ---

    /**
     * Отправляет сообщение в другие приложения с проверкой доступности данных.
     *
     * @param context         Контекст приложения.
     * @param targetPackage   Имя пакета целевого приложения (необязательно, если используется список).
     * @param message         Сообщение в виде Map<String, String>.
     * @param tag             Тег сообщения.
     * @param permission      Уровень доступа сообщения.
     */
    public static void sendMessage(Context context, String targetPackage, Map<String, String> message, String tag, String permission) {
        sendMessageWithDataCheck(context, targetPackage, message, tag, permission);
    }

    /**
     * Извлекает Map<String, String> из Intent.
     *
     * @param intent Intent, содержащий сообщение.
     * @return Map<String, String> с данными сообщения или null, если данные отсутствуют.
     */
    public static Map<String, String> extractMessageMapFromIntent(Intent intent) {
        String json = intent.getStringExtra(EXTRA_MESSAGE_MAP);
        Log.d(TAG, "Extracted message map: " + json);
        return gson.fromJson(json, Map.class);
    }

    /**
     * Извлекает тег сообщения из Intent.
     *
     * @param intent Intent, содержащий сообщение.
     * @return Строку с тегом сообщения или null, если тег отсутствует.
     */
    public static String extractMessageTagFromIntent(Intent intent) {
        String tag = intent.getStringExtra(EXTRA_MESSAGE_TAG);
        Log.d(TAG, "Extracted message tag: " + tag);
        return tag;
    }

    /**
     * Извлекает разрешение сообщения из Intent.
     *
     * @param intent Intent, содержащий сообщение.
     * @return Строку с разрешением сообщения или null, если разрешение отсутствует.
     */
    public static String extractMessagePermissionFromIntent(Intent intent) {
        String permission = intent.getStringExtra(EXTRA_MESSAGE_PERMISSION);
        Log.d(TAG, "Extracted message permission: " + permission);
        return permission;
    }

    /**
     * Извлекает имя пакета отправителя из Intent.
     *
     * @param intent Intent, содержащий сообщение.
     * @return Строку с именем пакета отправителя или null, если имя пакета отсутствует.
     */
    public static String extractSenderPackageFromIntent(Intent intent) {
        String senderPackage = intent.getStringExtra(EXTRA_SENDER_PACKAGE);
        Log.d(TAG, "Extracted sender package: " + senderPackage);
        return senderPackage;
    }

    /**
     * Обрабатывает полученный ответ от приложения.
     *
     * @param context Контекст приложения.
     * @param intent  Intent, содержащий ответ.
     */
    public static void processResponse(Context context, Intent intent) {
        String senderPackage = MessageManager.extractSenderPackageFromIntent(intent);
        String originalSender = context.getPackageName();

        // Проверяем, что сообщение пришло от пакета, которому мы отправляли запрос
        if (senderPackage != null && senderPackage.equals(originalSender)) {
            Log.d(TAG, "Processing response from: " + senderPackage);
            receivedMessage = MessageManager.extractMessageMapFromIntent(intent);
            if (responseLatch != null) {
                responseLatch.countDown();
            }
        } else {
            Log.w(TAG, "Received response from an unexpected package: " + senderPackage);
        }
    }

    // --- Приватные методы ---

    /**
     * Отправляет сообщение в другие приложения с проверкой доступности данных.
     *
     * @param context         Контекст приложения.
     * @param targetPackage   Имя пакета целевого приложения (необязательно, если используется список).
     * @param message         Сообщение в виде Map<String, String>.
     * @param tag             Тег сообщения.
     * @param permission      Уровень доступа сообщения.
     * @return Map<String, String> с данными ответа от приложения или null, если данные не получены.
     */
    private static Map<String, String> sendMessageWithDataCheck(Context context, String targetPackage, Map<String, String> message, String tag, String permission) {
        List<String> availablePackages = getAvailablePackages(context, "packages.txt");

        if (availablePackages.isEmpty()) {
            Log.d(TAG, "No available packages found");
            return null;
        }

        for (String packageName : availablePackages) {
            Log.d(TAG, "Checking package: " + packageName);
            if (isPackageInstalled(packageName, context.getPackageManager())) {
                Log.d(TAG, "Sending message to package: " + packageName);
                // Используем новый CountDownLatch для каждого пакета
                responseLatch = new CountDownLatch(1);
                receivedMessage = null;

                sendBroadcastMessage(context, packageName, message, tag, permission);

                try {
                    if (responseLatch.await(RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS)) {
                        // Получен ответ от приложения
                        Log.d(TAG, "Received response from package: " + packageName);
                        return receivedMessage;
                    } else {
                        // Истекло время ожидания ответа
                        Log.d(TAG, "Timeout waiting for response from package: " + packageName);
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted while waiting for response", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        Log.d(TAG, "No data received from available packages");
        return null;
    }

    /**
     * Отправляет широковещательное сообщение.
     *
     * @param context         Контекст приложения.
     * @param packageName     Имя пакета целевого приложения.
     * @param message         Сообщение в виде Map<String, String>.
     * @param tag             Тег сообщения.
     * @param permission      Уровень доступа сообщения.
     */
    private static void sendBroadcastMessage(Context context, String packageName, Map<String, String> message, String tag, String permission) {
        Intent intent = new Intent(ACTION_SEND_MESSAGE);
        intent.setPackage(packageName);
        intent.putExtra(EXTRA_MESSAGE_MAP, gson.toJson(message));
        intent.putExtra(EXTRA_MESSAGE_TAG, tag);
        intent.putExtra(EXTRA_MESSAGE_PERMISSION, permission);
        intent.putExtra(EXTRA_SENDER_PACKAGE, context.getPackageName());
        context.sendBroadcast(intent);
        Log.d(TAG, "Sent broadcast message to package: " + packageName);
    }

    /**
     * Получает список доступных пакетов из файла `packages.txt`.
     *
     * @param context  Контекст приложения.
     * @param fileName Имя файла со списком пакетов.
     * @return Список доступных пакетов.
     */
    private static List<String> getAvailablePackages(Context context, String fileName) {
        List<String> packages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                packages.add(line.trim());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading available packages", e);
        }
        return packages;
    }

    /**
     * Проверяет, установлен ли пакет на устройстве.
     *
     * @param packageName Имя пакета.
     * @param packageManager PackageManager для проверки установки пакета.
     * @return true, если пакет установлен, иначе false.
     */
    private static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        if (packageManager == null) {
            Log.e("PackageUtils", "PackageManager is null");
            return false;
        }

        if (packageName == null || packageName.isEmpty()) {
            Log.e("PackageUtils", "Package name is null or empty");
            return false;
        }

        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("PackageUtils", "Package not found: " + packageName);
            return false;
        }
    }
}
