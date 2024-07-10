package com.example.transauth;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TransAuthEnterButton extends AppCompatButton {
    private static final String TAG = "TransAuthSendButton";
    private MessageReceiver messageReceiver;

    private static Class<?> successActivityClass; // Activity для успешного входа
    private Class<?> defaultActivityClass; // Activity по умолчанию
    private boolean isAuthSuccessful = false; // Флаг успешного входа
    private String accountName;

    public static Class<?> getSuccessActivityClass() {
        return successActivityClass;
    }

    public TransAuthEnterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
//        sendAuthMessage(context);

        defaultActivityClass = LoginFirstActivity.class;
    }

    private void initialize(Context context) {
        setText("Войти через TransAuth");
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                // Проверяем, на какую Activity нужно перейти
                if (isAuthSuccessful) {
                    Intent intent = new Intent(context, LoginInfoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Name", accountName);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, defaultActivityClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });



        // Регистрация MessageReceiver для прослушивания ответов
        messageReceiver = new MessageReceiver(new MessageReceiver.MessageListener() {
            @Override
            public void onMessageReceived(Map<String, String> message) {
                Log.d(TAG, "Received message: " + message.toString());
                if (message.containsKey("Username")) {
                    updateButton(message.get("Username"));
                    accountName = message.get("Username");
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
        MessageManager.sendMessage(context, "com.example.transauth_test", message, MessageTags.ENTER_TO, Collections.singletonList(MessagePermissions.GET_USERNAME));
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
