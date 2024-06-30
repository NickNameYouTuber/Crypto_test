package com.example.transauth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class AccountProvider {
    private static final String PREFS_NAME = "AccountPrefs";
    private static final String KEY_ACCOUNT_INFO = "AccountInfo";

    private SharedPreferences sharedPreferences;

    public AccountProvider(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveAccountInfo(AccountInfo accountInfo) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCOUNT_INFO, ObjectSerializer.serialize(accountInfo));
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AccountInfo getAccountInfo() {
        String serializedAccountInfo = sharedPreferences.getString(KEY_ACCOUNT_INFO, null);
        if (serializedAccountInfo != null) {
            return (AccountInfo) ObjectSerializer.deserialize(serializedAccountInfo);
        }
        return null;
    }
}
