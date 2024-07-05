package com.example.transauth;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import java.util.HashMap;
import java.util.Map;

public class TransAuthButton extends AppCompatButton {
    private static final String TAG = "TransAuthSendButton";
    private MessageReceiver messageReceiver;

    private Class<?> successActivityClass; // Activity для успешного входа
    private Class<?> defaultActivityClass; // Activity по умолчанию
    private boolean isAuthSuccessful = false; // Флаг успешного входа

    public TransAuthButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
        sendAuthMessage(context);

        defaultActivityClass = LoginInfoActivity.class;
    }

    private void initialize(Context context) {
        setText("Войти через TransAuth");
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                // Проверяем, на какую Activity нужно перейти
                if (!isAuthSuccessful) {
                    Intent intent = new Intent(context, successActivityClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, defaultActivityClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        // Регистрация MessageReceiver для прослушивания ответов
        messageReceiver = new MessageReceiver(new MessageReceiver.MessageListener() {
            @Override
            public void onMessageReceived(Map<String, String> message) {
                Log.d(TAG, "Received message: " + message.toString());
                if (message.containsKey("Name")) {
                    updateButton(message.get("Name"));
                } else {
                    setText("Войти через TransAuth");
                }
            }
        });
        messageReceiver.register(context);
    }

    private void sendAuthMessage(Context context) {
        Map<String, String> message = new HashMap<>();
        message.put("code", "1234");
        MessageManager.sendMessage(context, "com.example.transauth_test", message, MessageTags.ENTER_TO, MessagePermissions.USER);
    }

    public void updateButton(String accountName) {
        setText("Войти как " + accountName);
        isAuthSuccessful = true;
    }

    public void setSuccessActivityClass(Class<?> successActivityClass) {
        this.successActivityClass = successActivityClass;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (messageReceiver != null) {
            messageReceiver.unregister(getContext());
        }
    }
}
