// src/main/java/com/example/transauth/MessageManager.java

package com.example.transauth;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class MessageManager {
    public static final String ACTION_SEND_MESSAGE = "com.example.messaging.ACTION_SEND_MESSAGE";
    public static final String EXTRA_MESSAGE_TEXT = "com.example.messaging.EXTRA_MESSAGE_TEXT";
    public static final String EXTRA_MESSAGE_TAG = "com.example.messaging.EXTRA_MESSAGE_TAG";
    public static final String EXTRA_MESSAGE_PERMISSION = "com.example.messaging.EXTRA_MESSAGE_PERMISSION";
    public static final String EXTRA_MESSAGE_LIST = "com.example.messaging.EXTRA_MESSAGE_LIST";


    public static void sendMessage(Context context, String targetPackage, String messageText, String tag, String permission) {
        Intent intent = new Intent(ACTION_SEND_MESSAGE);
        intent.setPackage(targetPackage);
        intent.putExtra(EXTRA_MESSAGE_TEXT, messageText);
        intent.putExtra(EXTRA_MESSAGE_TAG, tag);
        intent.putExtra(EXTRA_MESSAGE_PERMISSION, permission);
        context.sendBroadcast(intent);
    }

    public static String extractMessageTextFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_MESSAGE_TEXT);
    }

    public static String extractMessageTagFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_MESSAGE_TAG);
    }

    public static String extractMessagePermissionFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_MESSAGE_PERMISSION);
    }
}
