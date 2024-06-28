package com.nicorp.crypto_test;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHelper {
    private static int previousItemId = R.id.nav_wallet;

    public static void setupBottomNavigation(FragmentActivity activity) {
        // Initialize BottomNavigationView
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNav);

        // Determine the current fragment to select the correct menu item
        int selectedItemId = R.id.nav_wallet; // Default selection

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment targetFragment = null;
            if (item.getItemId() == R.id.nav_wallet) {
                targetFragment = new BalanceFragment();
            } else if (item.getItemId() == R.id.nav_qr) {
                targetFragment = new QRFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                targetFragment = new ProfileFragment();
            }

            if (targetFragment != null) {
                switchFragment(activity.getSupportFragmentManager(), targetFragment, item.getItemId());

                // Update previous item ID
                previousItemId = item.getItemId();
                return true;
            }
            return false;
        });

        // Set initial fragment
        if (activity.getSupportFragmentManager().getFragments().isEmpty()) {
            bottomNav.setSelectedItemId(selectedItemId);
        }
    }

    private static void switchFragment(FragmentManager fragmentManager, Fragment targetFragment, int newItemId) {
        boolean isDirectionRight = newItemId < previousItemId;

        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        isDirectionRight ? R.anim.slide_in_right : R.anim.slide_in_left,
                        isDirectionRight ? R.anim.slide_out_left : R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainerView, targetFragment)
                .commit();
    }
}
