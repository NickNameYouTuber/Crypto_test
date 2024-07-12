package com.example.transauth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransAuthUserDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "transAuthUsers_db";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_WALLETS = "wallets";

    // User Table Columns
    private static final String KEY_LOGIN = "login";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";

    // Wallet Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_USER_LOGIN = "user_login";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PLATFORM = "platform";
    private static final String KEY_NAME = "name";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_CURRENCY = "currency";

    public TransAuthUserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_LOGIN + " TEXT PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PHONE + " TEXT" + ")";

        String CREATE_WALLETS_TABLE = "CREATE TABLE " + TABLE_WALLETS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_LOGIN + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_PLATFORM + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_BALANCE + " REAL,"
                + KEY_CURRENCY + " TEXT,"
                + "FOREIGN KEY (" + KEY_USER_LOGIN + ") REFERENCES " + TABLE_USERS + "(" + KEY_LOGIN + ")" + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_WALLETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLETS);
        onCreate(db);
    }

    public void addUser(TransAuthUser user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOGIN, user.getLogin());
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PHONE, user.getPhone());

        db.insert(TABLE_USERS, null, values);

        for (Wallet wallet : user.getWallets()) {
            addWallet(db, wallet, user.getLogin());
        }

        db.close();
    }

    private void addWallet(SQLiteDatabase db, Wallet wallet, String userLogin) {
        ContentValues values = new ContentValues();
        values.put(KEY_USER_LOGIN, userLogin);
        values.put(KEY_ADDRESS, wallet.getAddress());
        values.put(KEY_PLATFORM, wallet.getPlatform());
        values.put(KEY_NAME, wallet.getName());
        values.put(KEY_BALANCE, wallet.getBalance());
        values.put(KEY_CURRENCY, wallet.getCurrency());

        db.insert(TABLE_WALLETS, null, values);
    }

    public TransAuthUser getUser(String login) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_LOGIN, KEY_USERNAME, KEY_PASSWORD, KEY_EMAIL, KEY_PHONE},
                KEY_LOGIN + "=?", new String[]{login}, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();

        TransAuthUser user = new TransAuthUser(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                new HashMap<>()
        );

        cursor.close();

        List<Wallet> wallets = getWalletsForUser(login);
        user.setWallets(wallets);

        return user;
    }

    public List<Wallet> getWalletsForUser(String userLogin) {
        List<Wallet> walletList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_WALLETS, new String[]{KEY_ID, KEY_ADDRESS, KEY_PLATFORM, KEY_NAME, KEY_BALANCE, KEY_CURRENCY},
                KEY_USER_LOGIN + "=?", new String[]{userLogin}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Wallet wallet = new Wallet(cursor.getString(1), cursor.getString(2), cursor.getString(3));
                wallet.setId(cursor.getInt(0));
                wallet.setBalance(cursor.getDouble(4));
                wallet.setCurrency(cursor.getString(5));
                walletList.add(wallet);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return walletList;
    }

    public void deleteUser(String login) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_LOGIN + " = ?", new String[]{login});
        db.delete(TABLE_WALLETS, KEY_USER_LOGIN + " = ?", new String[]{login});
        db.close();
    }

    public void updateUser(TransAuthUser user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PHONE, user.getPhone());

        db.update(TABLE_USERS, values, KEY_LOGIN + " = ?", new String[]{user.getLogin()});

        List<Wallet> existingWallets = getWalletsForUser(user.getLogin());
        List<Integer> existingWalletIds = new ArrayList<>();
        for (Wallet wallet : existingWallets) {
            existingWalletIds.add(wallet.getId());
        }

        for (Wallet wallet : user.getWallets()) {
            if (existingWalletIds.contains(wallet.getId())) {
                updateWallet(db, wallet);
            } else {
                addWallet(db, wallet, user.getLogin());
            }
        }

        db.close();
    }

    private void updateWallet(SQLiteDatabase db, Wallet wallet) {
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, wallet.getAddress());
        values.put(KEY_PLATFORM, wallet.getPlatform());
        values.put(KEY_NAME, wallet.getName());
        values.put(KEY_BALANCE, wallet.getBalance());
        values.put(KEY_CURRENCY, wallet.getCurrency());

        db.update(TABLE_WALLETS, values, KEY_ID + " = ?", new String[]{String.valueOf(wallet.getId())});
    }

    public void deleteWallet(int walletId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WALLETS, KEY_ID + " = ?", new String[]{String.valueOf(walletId)});
        db.close();
    }
}
