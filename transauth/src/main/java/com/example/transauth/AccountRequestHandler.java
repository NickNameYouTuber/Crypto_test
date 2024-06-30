package com.example.transauth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class AccountRequestHandler {
    private static final String REQUEST_ACTION = "com.example.transauth.REQUEST";
    private static final String RESPONSE_ACTION = "com.example.transauth.RESPONSE";
    private static final String EXTRA_APP_CODE = "AppCode";
    private static final String EXTRA_REQUEST_TYPE = "RequestType";
    private static final String EXTRA_ACCOUNT_INFO = "AccountInfo";
    private static final int VALID_APP_CODE = 1111;

    public enum RequestType {
        USER_INFO, BUTTON
    }

    private Context context;
    private AccountProvider accountProvider;
    private BroadcastReceiver requestReceiver;

    public AccountRequestHandler(Context context, AccountProvider accountProvider) {
        this.context = context;
        this.accountProvider = accountProvider;

        this.requestReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onReceive(Context context, Intent intent) {
                int appCode = intent.getIntExtra(EXTRA_APP_CODE, -1);
                String requestType = intent.getStringExtra(EXTRA_REQUEST_TYPE);

                if (appCode == VALID_APP_CODE && requestType != null) {
                    AccountInfo accountInfo = accountProvider.getAccountInfo();
                    if (accountInfo != null) {
                        Intent responseIntent = new Intent(RESPONSE_ACTION);
                        responseIntent.putExtra(EXTRA_APP_CODE, appCode);
                        responseIntent.putExtra(EXTRA_REQUEST_TYPE, requestType);
                        responseIntent.putExtra(EXTRA_ACCOUNT_INFO, ObjectSerializer.serialize(accountInfo));
                        context.sendBroadcast(responseIntent);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(REQUEST_ACTION);
        context.registerReceiver(requestReceiver, filter);
    }

    public void unregister() {
        context.unregisterReceiver(requestReceiver);
    }
}
