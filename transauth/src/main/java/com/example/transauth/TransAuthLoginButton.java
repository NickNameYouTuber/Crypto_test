package com.example.transauth;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransAuthLoginButton extends AppCompatButton {
    private static final String TAG = "TransAuthSendButton";
    private MessageReceiver messageReceiver;

    private Class<?> successActivityClass; // Activity для успешного входа
    private Class<?> defaultActivityClass; // Activity по умолчанию
    private boolean isAuthSuccessful = false; // Флаг успешного входа

    public TransAuthLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);

        defaultActivityClass = LoginInfoActivity.class;
    }

    private void initialize(Context context) {
        successActivityClass = TransAuthButton.getSuccessActivityClass();

//        setText("Войти через TransAuth");
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAuthMessage(context);
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
        messageReceiver = new MessageReceiver(context, new MessageReceiver.MessageListener() {
            @Override
            public void onMessageReceived(Map<String, String> message) {
                Log.d(TAG, "Received message: " + message.toString());
                if (message.containsKey("Login")) {
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

        messageReceiver.register(context);
    }

    private void sendAuthMessage(Context context) {
        Map<String, String> message = new HashMap<>();
        message.put("code", "1234");
        List<String> permissions = Arrays.asList(TransAuth.getPermissionsArray());
        MessageManager.sendMessage(context, "com.example.transauth_test", message, MessageTags.ENTER_TO, permissions);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (messageReceiver != null) {
            messageReceiver.unregister(getContext());
        }
    }
}
