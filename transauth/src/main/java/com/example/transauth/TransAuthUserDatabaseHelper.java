package com.example.transauth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

        for (TransAuthWallet transAuthWallet : user.getWallets()) {
            addWallet(db, transAuthWallet, user.getLogin());
        }

        db.close();
    }

    private void addWallet(SQLiteDatabase db, TransAuthWallet transAuthWallet, String userLogin) {
        ContentValues values = new ContentValues();
        values.put(KEY_USER_LOGIN, userLogin);
        values.put(KEY_ADDRESS, transAuthWallet.getAddress());
        values.put(KEY_PLATFORM, transAuthWallet.getPlatform());
        values.put(KEY_NAME, transAuthWallet.getName());
        values.put(KEY_BALANCE, transAuthWallet.getBalance());
        values.put(KEY_CURRENCY, transAuthWallet.getCurrency());

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

        List<TransAuthWallet> transAuthWallets = getWalletsForUser(login);
        user.setWallets(transAuthWallets);

        return user;
    }

    public List<TransAuthWallet> getWalletsForUser(String userLogin) {
        List<TransAuthWallet> transAuthWalletList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_WALLETS, new String[]{KEY_ID, KEY_ADDRESS, KEY_PLATFORM, KEY_NAME, KEY_BALANCE, KEY_CURRENCY},
                KEY_USER_LOGIN + "=?", new String[]{userLogin}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                TransAuthWallet transAuthWallet = new TransAuthWallet(cursor.getString(1), cursor.getString(2), cursor.getString(3));
                transAuthWallet.setId(cursor.getInt(0));
                transAuthWallet.setBalance(cursor.getDouble(4));
                transAuthWallet.setCurrency(cursor.getString(5));
                transAuthWalletList.add(transAuthWallet);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return transAuthWalletList;
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

        List<TransAuthWallet> existingTransAuthWallets = getWalletsForUser(user.getLogin());
        List<Integer> existingWalletIds = new ArrayList<>();
        for (TransAuthWallet transAuthWallet : existingTransAuthWallets) {
            existingWalletIds.add(transAuthWallet.getId());
        }

        for (TransAuthWallet transAuthWallet : user.getWallets()) {
            if (existingWalletIds.contains(transAuthWallet.getId())) {
                updateWallet(db, transAuthWallet);
            } else {
                addWallet(db, transAuthWallet, user.getLogin());
            }
        }

        db.close();
    }

    private void updateWallet(SQLiteDatabase db, TransAuthWallet transAuthWallet) {
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, transAuthWallet.getAddress());
        values.put(KEY_PLATFORM, transAuthWallet.getPlatform());
        values.put(KEY_NAME, transAuthWallet.getName());
        values.put(KEY_BALANCE, transAuthWallet.getBalance());
        values.put(KEY_CURRENCY, transAuthWallet.getCurrency());

        db.update(TABLE_WALLETS, values, KEY_ID + " = ?", new String[]{String.valueOf(transAuthWallet.getId())});
    }

    public void deleteWallet(int walletId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WALLETS, KEY_ID + " = ?", new String[]{String.valueOf(walletId)});
        db.close();

        // Update user's wallets list after deletion
        updateUserAfterWalletDeletion(walletId);
    }

    public void updateUserAfterWalletDeletion(int walletId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Retrieve current user
        TransAuthUser user = TransAuth.getUser();

        if (user != null) {
            List<TransAuthWallet> updatedTransAuthWallets = new ArrayList<>(user.getWallets());

            // Find and remove wallet from updated wallets list
            Iterator<TransAuthWallet> iterator = updatedTransAuthWallets.iterator();
            while (iterator.hasNext()) {
                TransAuthWallet transAuthWallet = iterator.next();
                if (transAuthWallet.getId() == walletId) {
                    iterator.remove();
                    break; // Assuming wallet IDs are unique, we can break once found
                }
            }

            // Update user's wallets list in the database
            updateUser(new TransAuthUser(user.getLogin(), user.getUsername(), user.getPassword(),
                    user.getEmail(), user.getPhone(), user.getTokens(), updatedTransAuthWallets));
        }

        db.close();
    }

}
