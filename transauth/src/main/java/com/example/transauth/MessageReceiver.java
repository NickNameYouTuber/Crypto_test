package com.example.transauth;

import static com.example.transauth.TransAuth.setUser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BroadcastReceiver для приема и обработки сообщений от других приложений.
 */
public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageReceiver";
    private static final String SPECIAL_CODE = "1234"; // Код для тестового сценария

    private MessageListener listener; // Слушатель для обработки принятых сообщений
    private Gson gson; // Объект Gson для работы с JSON
    private TransAuthUserDatabaseHelper db; // Помощник для работы с базой данных

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
     * @param context  Контекст приложения.
     * @param listener Слушатель для обработки принятых сообщений.
     */
    public MessageReceiver(Context context, MessageListener listener) {
        this.listener = listener;
        this.gson = new Gson();
        this.db = new TransAuthUserDatabaseHelper(context);
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
     * В этом примере проверяет наличие специального кода и отправляет данные из базы данных обратно отправителю.
     *
     * @param context       Контекст приложения.
     * @param message       Полученное сообщение.
     * @param permissions    Уровень доступа сообщения.
     * @param senderPackage Имя пакета отправителя.
     */
    private void handleEnterToMessage(Context context, Map<String, String> message, List<String> permissions, String senderPackage) {
        if (message.size() == 1 && message.containsKey("code") && message.get("code").equals(SPECIAL_CODE)) {
            TransAuthUser user = db.getUser("nicktaser"); // Предполагается, что логин передается в сообщении
            Map<String, String> responseMessage = new HashMap<>();

            if (user != null) {
                for (String permission : permissions) {
                    switch (permission) {
                        case MessagePermissions.GET_EMAIL:
                            // Добавляем email
                            responseMessage.put("Email", user.getEmail());
                            break;
                        case MessagePermissions.GET_LOGIN:
                            // Добавляем login
                            responseMessage.put("Login", user.getLogin());
                            break;
                        case MessagePermissions.GET_USERNAME:
                            // Добавляем username
                            responseMessage.put("Username", user.getUsername());
                            break;
                        // Добавьте другие разрешения по мере необходимости
                    }
                }
                sendMessageBack(context, responseMessage, MessageTags.ENTER_FROM, senderPackage, permissions);
            } else {
                Log.d(TAG, "User not found in database");
            }
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

        TransAuthUser transAuthUser = new TransAuthUser();

        if (message.containsKey("Email")) {
            transAuthUser.setEmail(message.get("Email"));
        }
        if (message.containsKey("Login")) {
            transAuthUser.setLogin(message.get("Login"));
        }
        if (message.containsKey("Username")) {
            transAuthUser.setUsername(message.get("Username"));
        }

        if (message.containsKey("Login")) {
            db.addUser(transAuthUser); // Сохраняем пользователя в базе данных

            setUser(transAuthUser); // Set user
        }


        if (listener != null) {
            listener.onMessageReceived(message);
        }
        Log.d(TAG, "Received message: " + message.toString());
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
     * Отменяет регистрацию BroadcastReceiver.
     *
     * @param context Контекст приложения.
     */
    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
