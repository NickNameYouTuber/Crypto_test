package com.example.transauth;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TransAuthButton extends androidx.appcompat.widget.AppCompatButton {
//    private AccountRequestHandler accountRequestHandler;

    public TransAuthButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setText("Войти через TransAuth");
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TransAuthButton", "onClick() called with: v = [" + v + "]");
//                requestAccountInfo();
            }
        });
    }

//    public void setAccountRequestHandler(AccountRequestHandler accountRequestHandler) {
//        this.accountRequestHandler = accountRequestHandler;
//    }
//
//    private void requestAccountInfo() {
//        Intent requestIntent = new Intent(getContext(), AccountRequestService.class);
//        requestIntent.setAction("com.example.transauth.REQUEST");
//        requestIntent.putExtra("AppCode", 1111);
//        requestIntent.putExtra("RequestType", "YOUR_REQUEST_TYPE");
//        getContext().startService(requestIntent);
//
//
//    }

    public void updateButton(String accountName) {
        setText("Войти как " + accountName);
    }
}
