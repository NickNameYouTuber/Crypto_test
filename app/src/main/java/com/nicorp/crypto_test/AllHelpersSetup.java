package com.nicorp.crypto_test;

import androidx.fragment.app.FragmentActivity;

import com.nicorp.crypto_test.helpers.NavigationHelper;
import com.nicorp.crypto_test.helpers.ThemeHelper;

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
