// src/main/java/com/example/messaging/MessageManager.java

package com.example.transauth;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class MessageManager {
    public static final String ACTION_SEND_MESSAGE = "com.example.messaging.ACTION_SEND_MESSAGE";
    public static final String EXTRA_MESSAGE_LIST = "com.example.messaging.EXTRA_MESSAGE_LIST";
    public static final String EXTRA_PERMISSION = "com.example.messaging.EXTRA_PERMISSION";

    public static void sendMessage(Context context, String targetPackage, ArrayList<String> message, int permission) {
        Intent intent = new Intent(ACTION_SEND_MESSAGE);
        intent.setPackage(targetPackage);
        intent.putStringArrayListExtra(EXTRA_MESSAGE_LIST, message);
        intent.putExtra(EXTRA_PERMISSION, permission);
        context.sendBroadcast(intent);
    }

    public static ArrayList<String> extractMessageFromIntent(Intent intent, int permission) {
        ArrayList<String> message = intent.getStringArrayListExtra(EXTRA_MESSAGE_LIST);
        if (permission == Permissions.FIRST && message != null && !message.isEmpty()) {
            ArrayList<String> filteredMessage = new ArrayList<>();
            filteredMessage.add(message.get(0));
            return filteredMessage;
        }
        return message;
    }
}