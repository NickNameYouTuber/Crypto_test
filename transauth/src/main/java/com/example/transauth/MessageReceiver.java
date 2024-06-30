// src/main/java/com/example/transauth/MessageReceiver.java

package com.example.transauth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageReceiver";
    private MessageListener listener;

    public interface MessageListener {
        void onMessageReceived(ArrayList<String> message);
    }

    public MessageReceiver(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String tag = MessageManager.extractMessageTagFromIntent(intent);
        String permission = MessageManager.extractMessagePermissionFromIntent(intent);
        ArrayList<String> message = new ArrayList<>();
        message.add(MessageManager.extractMessageTextFromIntent(intent));

        if (tag.equals(MessageTags.ENTER_TO)) {
            if (permission.equals(MessagePermissions.USER)) {
                // Отправляем ответное сообщение в uppercase
                ArrayList<String> modifiedMessage = modifyMessage(message, true);
                sendMessageBack(context, modifiedMessage, MessageTags.ENTER_FROM);
            } else if (permission.equals(MessagePermissions.ADMIN)) {
                // Отправляем ответное сообщение в lowercase
                ArrayList<String> modifiedMessage = modifyMessage(message, false);
                sendMessageBack(context, modifiedMessage, MessageTags.ENTER_FROM);
            }
        } else if (tag.equals(MessageTags.ENTER_FROM)) {
            // Выводим полученное сообщение
            Log.d(TAG, "Received message: " + message.toString());
        }
    }

    private ArrayList<String> modifyMessage(ArrayList<String> message, boolean toUpperCase) {
        ArrayList<String> modified = new ArrayList<>();
        for (String msg : message) {
            if (toUpperCase) {
                modified.add(msg.toUpperCase());
            } else {
                modified.add(msg.toLowerCase());
            }
        }
        return modified;
    }

    private void sendMessageBack(Context context, ArrayList<String> message, String tag) {
        Intent intent = new Intent(MessageManager.ACTION_SEND_MESSAGE);
        intent.setPackage(context.getPackageName()); // Отправляем сообщение в тот же пакет
        intent.putStringArrayListExtra(MessageManager.EXTRA_MESSAGE_LIST, message);
        intent.putExtra(MessageManager.EXTRA_MESSAGE_TAG, tag);
        context.sendBroadcast(intent);
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter(MessageManager.ACTION_SEND_MESSAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED);
        }
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
