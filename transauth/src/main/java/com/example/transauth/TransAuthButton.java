package com.example.transauth;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.HashMap;
import java.util.Map;

public class TransAuthButton extends CardView {
    private static final String TAG = "TransAuthSendButton";
    private MessageReceiver messageReceiver;

    private Class<?> successActivityClass; // Activity для успешного входа
    private Class<?> defaultActivityClass; // Activity по умолчанию
    private boolean isAuthSuccessful = false; // Флаг успешного входа

    private TextView textView;

    public TransAuthButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
        sendAuthMessage(context);

        defaultActivityClass = LoginInfoActivity.class;
    }

    private void initialize(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trans_auth_button, this, true);
        textView = view.findViewById(R.id.trans_auth_button_text);

        // Получение атрибутов из XML
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TransAuthButton, 0, 0);
        String buttonText = "Войти через TransAuth";
        int backgroundColor = 0; // Значение по умолчанию
        try {
            buttonText = a.getString(R.styleable.TransAuthButton_buttonText);
            backgroundColor = a.getColor(R.styleable.TransAuthButton_buttonBackgroundColor, 0);
        } finally {
            a.recycle();
        }

        // Установка текста кнопки
        textView.setText(buttonText);

        // Установка параметров CardView
        setRadius(12.0f); // Радиус углов CardView
        setCardElevation(8.0f); // Высота тени
        setContentPadding(16, 16, 16, 16); // Отступы внутри CardView
        setCardBackgroundColor(backgroundColor);
        setClickable(true);
        setFocusable(true);

        // Обработка нажатия
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                // Проверяем, на какую Activity нужно перейти
                if (isAuthSuccessful) {
                    Intent intent = new Intent(getContext(), successActivityClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), defaultActivityClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
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
                    textView.setText("Войти через TransAuth");
                }
            }
        });
        messageReceiver.register(getContext());
    }

    private void sendAuthMessage(Context context) {
        Map<String, String> message = new HashMap<>();
        message.put("code", "1234");
        MessageManager.sendMessage(context, "com.example.transauth_test", message, MessageTags.ENTER_TO, MessagePermissions.USER);
    }

    public void updateButton(String accountName) {
        textView.setText("Войти как " + accountName);
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
