package com.example.transauth;

import android.content.Context;
import android.content.SharedPreferences;

public class AccountProvider {
    private static final String PREFS_NAME = "AccountPrefs";
    private static final String KEY_ACCOUNT_INFO = "AccountInfo";

    private SharedPreferences sharedPreferences;

    public AccountProvider(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveAccountInfo(AccountInfo accountInfo) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCOUNT_INFO, ObjectSerializer.serialize(accountInfo));
        editor.apply();
    }

    public AccountInfo getAccountInfo() {
        String serializedAccountInfo = sharedPreferences.getString(KEY_ACCOUNT_INFO, null);
        if (serializedAccountInfo != null) {
            return (AccountInfo) ObjectSerializer.deserialize(serializedAccountInfo);
        }
        return null;
    }
}
