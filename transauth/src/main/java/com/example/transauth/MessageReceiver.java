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
        String senderPackage = MessageManager.extractSenderPackageFromIntent(intent);
        ArrayList<String> message = MessageManager.extractMessageListFromIntent(intent);

        Log.d(TAG, "Received message: " + message.toString());
        Log.d(TAG, "Received tag: " + tag);
        Log.d(TAG, "Received permission: " + permission);
        Log.d(TAG, "Sender package: " + senderPackage);

        if (tag.equals(MessageTags.ENTER_TO)) {
            if (permission.equals(MessagePermissions.USER)) {
                // Отправляем ответное сообщение в uppercase
                ArrayList<String> modifiedMessage = modifyMessage(message, true);
                sendMessageBack(context, modifiedMessage, MessageTags.ENTER_FROM, senderPackage);
            } else if (permission.equals(MessagePermissions.ADMIN)) {
                // Отправляем ответное сообщение в lowercase
                ArrayList<String> modifiedMessage = modifyMessage(message, false);
                sendMessageBack(context, modifiedMessage, MessageTags.ENTER_FROM, senderPackage);
            }
        } else if (tag.equals(MessageTags.ENTER_FROM)) {
            // Выводим полученное сообщение
            if (listener != null) {
                listener.onMessageReceived(message);
            }
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

    private void sendMessageBack(Context context, ArrayList<String> message, String tag, String targetPackage) {
        Intent intent = new Intent(MessageManager.ACTION_SEND_MESSAGE);
        intent.setPackage(targetPackage); // Отправляем сообщение обратно в исходный пакет
        intent.putStringArrayListExtra(MessageManager.EXTRA_MESSAGE_LIST, message);
        intent.putExtra(MessageManager.EXTRA_MESSAGE_TAG, tag);
        context.sendBroadcast(intent);
    }

    public void register(Context context) {
        Log.d(TAG, "register() called with: context = [" + context + "]");
        IntentFilter filter = new IntentFilter(MessageManager.ACTION_SEND_MESSAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED);
            Log.d(TAG, "register was successful");
        } else {
            context.registerReceiver(this, filter);
            Log.d(TAG, "register was successful");
        }
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
