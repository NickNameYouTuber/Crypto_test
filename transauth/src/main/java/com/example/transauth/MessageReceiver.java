// src/main/java/com/example/transauth/MessageReceiver.java

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
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * BroadcastReceiver для приема и обработки сообщений от других приложений.
 */
public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageReceiver";
    private static final String FILE_NAME = "message.json";
    private static final String SPECIAL_CODE = "1234"; // Код для тестового сценария

    private MessageListener listener; // Слушатель для обработки принятых сообщений
    private Gson gson; // Объект Gson для работы с JSON

    /**
     * Интерфейс для обратного вызова при получении сообщения.
     */
    public interface MessageListener {
        /**
         * Вызывается при получении сообщения.
         *
         * @param message Сообщение в виде Map<String, String>.
         */
        void onMessageReceived(Map<String, String> message);
    }

    public MessageReceiver() {

    }

    /**
     * Конструктор MessageReceiver.
     *
     * @param listener Слушатель для обработки принятых сообщений.
     */
    public MessageReceiver(MessageListener listener) {
        this.listener = listener;
        this.gson = new Gson();
    }

    /**
     * Вызывается при получении широковещательного сообщения.
     *
     * @param context Контекст приложения.
     * @param intent  Intent, содержащий сообщение.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action);

        // Обрабатываем только сообщения с действием ACTION_SEND_MESSAGE
        if (action != null && action.equals(MessageManager.ACTION_SEND_MESSAGE)) {
            String tag = MessageManager.extractMessageTagFromIntent(intent);
            String permission = MessageManager.extractMessagePermissionFromIntent(intent);
            String senderPackage = MessageManager.extractSenderPackageFromIntent(intent);
            Map<String, String> message = MessageManager.extractMessageMapFromIntent(intent);

            Log.d(TAG, "Received message: " + message.toString());
            Log.d(TAG, "Received tag: " + tag);
            Log.d(TAG, "Received permission: " + permission);
            Log.d(TAG, "Sender package: " + senderPackage);

            // Обработка сообщения в зависимости от тега
            if (tag.equals(MessageTags.ENTER_TO)) {
                handleEnterToMessage(context, message, permission, senderPackage);
            } else if (tag.equals(MessageTags.ENTER_FROM)) {
                handleEnterFromMessage(context, message, intent);
            }
        }
    }

    /**
     * Обрабатывает сообщение с тегом ENTER_TO.
     * В этом примере проверяет наличие специального кода и отправляет данные из файла обратно отправителю.
     *
     * @param context       Контекст приложения.
     * @param message       Полученное сообщение.
     * @param permission    Уровень доступа сообщения.
     * @param senderPackage Имя пакета отправителя.
     */
    private void handleEnterToMessage(Context context, Map<String, String> message, String permission, String senderPackage) {
        if (message.size() == 1 && message.containsKey("code") && message.get("code").equals(SPECIAL_CODE)) {
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
    }

    /**
     * Обрабатывает сообщение с тегом ENTER_FROM.
     * В этом примере передает сообщение слушателю и вызывает `MessageManager.processResponse` для обработки ответа.
     *
     * @param context Контекст приложения.
     * @param message Полученное сообщение.
     * @param intent  Intent, содержащий сообщение.
     */
    private void handleEnterFromMessage(Context context, Map<String, String> message, Intent intent) {
        MessageManager.processResponse(context, intent);
        if (listener != null) {
            listener.onMessageReceived(message);
        }
        Log.d(TAG, "Received message: " + message.toString());
    }

    /**
     * Читает данные из файла.
     *
     * @param context Контекст приложения.
     * @return Map<String, String> с данными из файла или пустую Map, если произошла ошибка чтения.
     */
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
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            fileMessage = gson.fromJson(json.toString(), type);
        } catch (Exception e) {
            Log.e(TAG, "Error reading file", e);
        }
        return fileMessage;
    }

    /**
     * Отправляет сообщение обратно отправителю.
     *
     * @param context       Контекст приложения.
     * @param message       Сообщение для отправки.
     * @param tag           Тег сообщения.
     * @param targetPackage Имя пакета получателя.
     */
    private void sendMessageBack(Context context, Map<String, String> message, String tag, String targetPackage) {
        Intent intent = new Intent(MessageManager.ACTION_SEND_MESSAGE);
        intent.setPackage(targetPackage);
        intent.putExtra(MessageManager.EXTRA_MESSAGE_MAP, gson.toJson(message));
        intent.putExtra(MessageManager.EXTRA_MESSAGE_TAG, tag);
        context.sendBroadcast(intent);
        Log.d(TAG, "Sent response message to package: " + targetPackage);
    }

    /**
     * Регистрирует BroadcastReceiver.
     *
     * @param context Контекст приложения.
     */
    public void register(Context context) {
        Log.d(TAG, "register() called with: context = [" + context + "]");
        IntentFilter filter = new IntentFilter(MessageManager.ACTION_SEND_MESSAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED);
        } else {
            context.registerReceiver(this, filter);
        }
        Log.d(TAG, "register was successful");
    }

    /**
     * Записывает данные в файл.
     *
     * @param context Контекст приложения.
     * @param message Данные для записи.
     */
    public void writeFile(Context context, Map<String, String> message) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            String json = gson.toJson(message);
            fos.write(json.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "Error writing file", e);
        }
    }

    /**
     * Отменяет регистрацию BroadcastReceiver.
     *
     * @param context Контекст приложения.
     */
    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
