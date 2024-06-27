package com.nicorp.crypto_test;

import android.app.Activity;

public class AllHelpersSetup {

    public static void setup(Activity activity, int activityID, boolean navigationEnabled) {
        ThemeHelper.applyTheme(activity);
        activity.setContentView(activityID);
        if (navigationEnabled) {
            NavigationHelper.setupBottomNavigation(activity);
        }
    }

    public static void setup(Activity activity, int activityID) {
        setup(activity, activityID, true);
    }

}
