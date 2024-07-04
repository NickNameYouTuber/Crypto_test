package com.example.transauth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageReceiver";
    private static final String FILE_NAME = "message.json";
    private static final String SPECIAL_CODE = "1234";
    private MessageListener listener;
    private Gson gson;

    public interface MessageListener {
        void onMessageReceived(Map<String, String> message);
    }

    public MessageReceiver(MessageListener listener) {
        this.listener = listener;
        this.gson = new Gson();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String tag = MessageManager.extractMessageTagFromIntent(intent);
        String permission = MessageManager.extractMessagePermissionFromIntent(intent);
        String senderPackage = MessageManager.extractSenderPackageFromIntent(intent);
        Map<String, String> message = MessageManager.extractMessageMapFromIntent(intent);

        Log.d(TAG, "Received message: " + message);
        Log.d(TAG, "Received tag: " + tag);
        Log.d(TAG, "Received permission: " + permission);
        Log.d(TAG, "Sender package: " + senderPackage);

        if (tag != null && permission != null && senderPackage != null) {
            if (tag.equals(MessageTags.ENTER_TO)) {
                if (message != null && message.containsKey("code") && message.get("code").equals(SPECIAL_CODE)) {
                    // Читаем сообщение из файла и отправляем его обратно
                    Map<String, String> fileMessage = readFile(context);
                    Map<String, String> responseMessage = new HashMap<>();

                    if (permission.equals(MessagePermissions.USER)) {
                        // Отправляем только Name
                        if (fileMessage.containsKey("Name")) {
                            responseMessage.put("Name", fileMessage.get("Name"));
                        }
                    } else if (permission.equals(MessagePermissions.ADMIN)) {
                        // Отправляем все данные
                        responseMessage.putAll(fileMessage);
                    }

                    sendMessageBack(context, responseMessage, MessageTags.ENTER_FROM, senderPackage);
                }
            } else if (tag.equals(MessageTags.ENTER_FROM)) {
                // Выводим полученное сообщение
                if (listener != null) {
                    listener.onMessageReceived(message);
                }
                Log.d(TAG, "Received message: " + message);
            } else if (tag.equals("ДАННЫХ НЕТ")) {
                // Отправляем сообщение самому себе, что данных нет
                Log.d(TAG, "ДАННЫХ НЕТ");
                sendMessageToSelf(context, "ДАННЫХ НЕТ");
            }
        } else {
            Log.e(TAG, "Received invalid/null data from intent.");
        }
    }

<<<<<<< Updated upstream
    private void writeFile(Context context, Map<String, String> message) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            String json = gson.toJson(message);
            fos.write(json.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "Error writing file", e);
        }
    }
=======
>>>>>>> Stashed changes

    private Map<String, String> readFile(Context context) {
        Map<String, String> fileMessage = new HashMap<>();
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            fileMessage = gson.fromJson(json.toString(), type);
        } catch (Exception e) {
            Log.e(TAG, "Error reading file", e);
        }
        return fileMessage;
    }

    private Map<String, String> readFileAndModify(Context context, boolean toUpperCase) {
        Map<String, String> modifiedMessage = readFile(context);
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : modifiedMessage.entrySet()) {
            if (toUpperCase) {
                result.put(entry.getKey(), entry.getValue().toUpperCase());
            } else {
                result.put(entry.getKey(), entry.getValue().toLowerCase());
            }
        }
        return result;
    }

    private void sendMessageBack(Context context, Map<String, String> message, String tag, String targetPackage) {
        Intent intent = new Intent(MessageManager.ACTION_SEND_MESSAGE);
        intent.setPackage(targetPackage); // Отправляем сообщение обратно в исходный пакет
        intent.putExtra(MessageManager.EXTRA_MESSAGE_MAP, gson.toJson(message));
        intent.putExtra(MessageManager.EXTRA_MESSAGE_TAG, tag);
        context.sendBroadcast(intent);
    }

    private void sendMessageToSelf(Context context, String message) {
        Intent intent = new Intent(MessageManager.ACTION_SEND_MESSAGE);
        intent.setPackage(context.getPackageName()); // Отправляем сообщение самому себе
        intent.putExtra(MessageManager.EXTRA_MESSAGE_MAP, message);
        intent.putExtra(MessageManager.EXTRA_MESSAGE_TAG, MessageTags.ENTER_FROM);
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
