package com.nicorp.crypto_test;

import android.app.Activity;
import android.app.Fragment;

import androidx.fragment.app.FragmentActivity;

public class AllHelpersSetup {

    public static void setup(FragmentActivity activity, int activityID, boolean navigationEnabled) {
        ThemeHelper.applyTheme(activity);
        activity.setContentView(activityID);
        if (navigationEnabled) {
            NavigationHelper.setupBottomNavigation(activity);
        }
    }

    public static void setup(FragmentActivity activity, int activityID) {
        setup(activity, activityID, true);
    }

}
