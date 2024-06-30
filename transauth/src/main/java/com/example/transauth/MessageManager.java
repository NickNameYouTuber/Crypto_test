// src/main/java/com/example/messaging/MessageManager.java

package com.example.transauth;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class MessageManager {
    public static final String ACTION_SEND_MESSAGE = "com.example.messaging.ACTION_SEND_MESSAGE";
    public static final String EXTRA_MESSAGE_LIST = "com.example.messaging.EXTRA_MESSAGE_LIST";

    public static void sendMessage(Context context, String targetPackage, ArrayList<String> message) {
        Intent intent = new Intent(ACTION_SEND_MESSAGE);
        intent.setPackage(targetPackage);
        intent.putStringArrayListExtra(EXTRA_MESSAGE_LIST, message);
        context.sendBroadcast(intent);
    }

    public static ArrayList<String> extractMessageFromIntent(Intent intent) {
        return intent.getStringArrayListExtra(EXTRA_MESSAGE_LIST);
    }
}
