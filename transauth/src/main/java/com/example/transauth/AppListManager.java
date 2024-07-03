// src/main/java/com/example/transauth/AppListManager.java

package com.example.transauth;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AppListManager {
    private static final String FILE_NAME = "applist.json";
    private static final String TAG = "AppListManager";
    private static final Gson gson = new Gson();

    public static void saveAppList(Context context, List<String> appList) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            String json = gson.toJson(appList);
            fos.write(json.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "Error writing file", e);
        }
    }

    public static List<String> readAppList(Context context) {
        List<String> appList = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            Type type = new TypeToken<List<String>>(){}.getType();
            appList = gson.fromJson(json.toString(), type);
        } catch (Exception e) {
            Log.e(TAG, "Error reading file", e);
        }
        return appList;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
