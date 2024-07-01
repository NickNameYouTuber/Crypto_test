// src/main/java/com/example/transauth/MessageManager.java

package com.example.transauth;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import java.util.Map;

public class MessageManager {
    public static final String ACTION_SEND_MESSAGE = "com.example.messaging.ACTION_SEND_MESSAGE";
    public static final String EXTRA_MESSAGE_TAG = "com.example.messaging.EXTRA_MESSAGE_TAG";
    public static final String EXTRA_MESSAGE_PERMISSION = "com.example.messaging.EXTRA_MESSAGE_PERMISSION";
    public static final String EXTRA_MESSAGE_MAP = "com.example.messaging.EXTRA_MESSAGE_MAP";
    public static final String EXTRA_SENDER_PACKAGE = "com.example.messaging.EXTRA_SENDER_PACKAGE";

    private static final Gson gson = new Gson();

    public static void sendMessage(Context context, String targetPackage, Map<String, String> message, String tag, String permission) {
        Intent intent = new Intent(ACTION_SEND_MESSAGE);
        intent.setPackage(targetPackage);
        intent.putExtra(EXTRA_MESSAGE_MAP, gson.toJson(message));
        intent.putExtra(EXTRA_MESSAGE_TAG, tag);
        intent.putExtra(EXTRA_MESSAGE_PERMISSION, permission);
        intent.putExtra(EXTRA_SENDER_PACKAGE, context.getPackageName());
        context.sendBroadcast(intent);
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
