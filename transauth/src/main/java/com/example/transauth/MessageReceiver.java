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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        if (action != null && action.equals(MessageManager.ACTION_SEND_MESSAGE)) {
            String tag = MessageManager.extractMessageTagFromIntent(intent);
            List<String> permissions = MessageManager.extractMessagePermissionsFromIntent(intent); // Извлекаем список разрешений
            String senderPackage = MessageManager.extractSenderPackageFromIntent(intent);
            Map<String, String> message = MessageManager.extractMessageMapFromIntent(intent);

            Log.d(TAG, "Received message: " + message.toString());
            Log.d(TAG, "Received tag: " + tag);
            Log.d(TAG, "Received permissions: " + permissions);
            Log.d(TAG, "Sender package: " + senderPackage);

            if (tag.equals(MessageTags.ENTER_TO)) {
                handleEnterToMessage(context, message, permissions, senderPackage);
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
     * @param permissions    Уровень доступа сообщения.
     * @param senderPackage Имя пакета отправителя.
     */
    private void handleEnterToMessage(Context context, Map<String, String> message, List<String> permissions, String senderPackage) {
        if (message.size() == 1 && message.containsKey("code") && message.get("code").equals(SPECIAL_CODE)) {
            Map<String, String> fileMessage = readFile(context);
            Map<String, String> responseMessage = new HashMap<>();

            for (String permission : permissions) {
                switch (permission) {
                    case MessagePermissions.GET_EMAIL:
                        // Добавляем Name
                        if (fileMessage.containsKey("Email")) {
                            responseMessage.put("Email", fileMessage.get("Email"));
                        }
                        break;
                    case MessagePermissions.GET_LOGIN:
                        // Добавляем все данные
                        if (fileMessage.containsKey("Login")) {
                            responseMessage.put("Login", fileMessage.get("Login"));
                        }
                        break;
                    case MessagePermissions.GET_NAME:
                        // Добавляем данные для другого разрешения, если требуется
                        if (fileMessage.containsKey("Name")) {
                            responseMessage.put("Name", fileMessage.get("Name"));
                        }
                        break;
                    // Добавьте другие разрешения по мере необходимости
                }
            }
            sendMessageBack(context, responseMessage, MessageTags.ENTER_FROM, senderPackage, permissions);
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
     * @param permissions   Список разрешений.
     */
    private void sendMessageBack(Context context, Map<String, String> message, String tag, String targetPackage, List<String> permissions) {
        Intent intent = new Intent(MessageManager.ACTION_SEND_MESSAGE);
        intent.setPackage(targetPackage);
        intent.putExtra(MessageManager.EXTRA_MESSAGE_MAP, gson.toJson(message));
        intent.putExtra(MessageManager.EXTRA_MESSAGE_TAG, tag);
        intent.putStringArrayListExtra(MessageManager.EXTRA_MESSAGE_PERMISSIONS, new ArrayList<>(permissions));
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
    public void writeFile(Context context, TransAuthUser transAuthUser) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            String json = gson.toJson(transAuthUser);
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
