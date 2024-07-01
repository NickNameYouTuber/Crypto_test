// src/main/java/com/example/transauth/MessageReceiver.java

package com.example.transauth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageReceiver";
    private static final String FILE_NAME = "message.txt";
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
                // Записываем сообщение в файл
                writeFile(context, message);

                // Читаем сообщение из файла и отправляем его обратно в uppercase
                ArrayList<String> modifiedMessage = readFileAndModify(context, true);
                sendMessageBack(context, modifiedMessage, MessageTags.ENTER_FROM, senderPackage);
            } else if (permission.equals(MessagePermissions.ADMIN)) {
                // Записываем сообщение в файл
                writeFile(context, message);

                // Читаем сообщение из файла и отправляем его обратно в lowercase
                ArrayList<String> modifiedMessage = readFileAndModify(context, false);
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

    private void writeFile(Context context, ArrayList<String> message) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            for (String line : message) {
                fos.write((line + "\n").getBytes());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error writing file", e);
        }
    }

    private ArrayList<String> readFileAndModify(Context context, boolean toUpperCase) {
        ArrayList<String> modifiedMessage = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (toUpperCase) {
                    modifiedMessage.add(line.toUpperCase());
                } else {
                    modifiedMessage.add(line.toLowerCase());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading file", e);
        }
        return modifiedMessage;
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
