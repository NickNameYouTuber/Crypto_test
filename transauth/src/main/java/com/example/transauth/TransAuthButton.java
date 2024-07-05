package com.example.transauth;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.AppCompatButton;

import java.util.HashMap;
import java.util.Map;

public class TransAuthButton extends AppCompatButton {
    private static final String TAG = "TransAuthSendButton";
    private MessageReceiver messageReceiver;

    public TransAuthButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
        sendAuthMessage(context);
    }

    private void initialize(Context context) {
        setText("Войти через TransAuth");
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
            }
        });

        // Register the MessageReceiver to listen for responses
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
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (messageReceiver != null) {
            messageReceiver.unregister(getContext());
        }
    }
}
