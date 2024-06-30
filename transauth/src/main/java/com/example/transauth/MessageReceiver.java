package com.example.transauth;

// src/main/java/com/example/messaging/MessageReceiver.java

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class MessageReceiver extends BroadcastReceiver {
    private MessageListener listener;
    private int permission;

    public interface MessageListener {
        void onMessageReceived(ArrayList<String> message);
    }

    public MessageReceiver(MessageListener listener, int permission) {
        this.listener = listener;
        this.permission = permission;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<String> message = MessageManager.extractMessageFromIntent(intent, permission);
        if (listener != null) {
            listener.onMessageReceived(message);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void register(Context context) {
        IntentFilter filter = new IntentFilter(MessageManager.ACTION_SEND_MESSAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED);
        }
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}