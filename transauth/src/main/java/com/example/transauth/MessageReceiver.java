package com.example.transauth;

// src/main/java/com/example/messaging/MessageReceiver.java

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;

public class MessageReceiver extends BroadcastReceiver {
    private MessageListener listener;

    public interface MessageListener {
        void onMessageReceived(ArrayList<String> message);
    }

    public MessageReceiver(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<String> message = MessageManager.extractMessageFromIntent(intent);
        if (listener != null) {
            listener.onMessageReceived(message);
        }
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter(MessageManager.ACTION_SEND_MESSAGE);
        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
