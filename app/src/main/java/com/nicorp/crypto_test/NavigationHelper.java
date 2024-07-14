package com.nicorp.crypto_test;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHelper {
    private static Fragment currentFragment;
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
                switchFragment(activity, targetFragment, item.getItemId());
                return true;
            }
            return false;
        });

        // Set initial fragment
        if (activity.getSupportFragmentManager().getFragments().isEmpty()) {
            bottomNav.setSelectedItemId(selectedItemId);
        } else {
            // Restore the last fragment
            currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
            previousItemId = bottomNav.getSelectedItemId();
        }
    }

    private static void switchFragment(FragmentActivity activity, Fragment targetFragment, int newItemId) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        boolean isDirectionRight = newItemId > previousItemId;

        int enterAnimation = isDirectionRight ? R.anim.slide_in_left : R.anim.slide_in_right;
        int exitAnimation = isDirectionRight ? R.anim.slide_out_right : R.anim.slide_out_left;
        int popEnterAnimation = isDirectionRight ? R.anim.slide_in_right : R.anim.slide_in_left;
        int popExitAnimation = isDirectionRight ? R.anim.slide_out_left : R.anim.slide_out_right;

        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation);

        // Hide the current fragment if it's not null
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        // Check if the target fragment is already added
        Fragment fragment = fragmentManager.findFragmentByTag(targetFragment.getClass().getName());

        if (fragment == null) {
            // Add the fragment if it is not already added
            fragment = targetFragment;
            transaction.add(R.id.fragmentContainerView, fragment, fragment.getClass().getName());
        } else {
            // Show the existing fragment
            transaction.show(fragment);
        }

        // Commit the transaction
        transaction.commitAllowingStateLoss();

        // Update the current fragment and previous item ID
        currentFragment = fragment;
        previousItemId = newItemId;
    }

    public static void navigateToFragment(FragmentActivity activity, Fragment targetFragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        // Hide the current fragment if it's not null
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        // Check if the target fragment is already added
        Fragment fragment = fragmentManager.findFragmentByTag(targetFragment.getClass().getName());

        if (fragment == null) {
            // Add the fragment if it is not already added
            fragment = targetFragment;
            transaction.add(R.id.fragmentContainerView, fragment, fragment.getClass().getName());
        } else {
            // Show the existing fragment
            transaction.show(fragment);
        }

        // Commit the transaction
        transaction.addToBackStack(null).commitAllowingStateLoss();

        // Update the current fragment
        currentFragment = fragment;
    }

    public static void handleBackButton(FragmentActivity activity, Fragment targetFragment, int targetItemId) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (targetItemId == -1) {
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        } else {
            boolean isDirectionRight = targetItemId > previousItemId;

            int enterAnimation = isDirectionRight ? R.anim.slide_in_left : R.anim.slide_in_right;
            int exitAnimation = isDirectionRight ? R.anim.slide_out_right : R.anim.slide_out_left;
            int popEnterAnimation = isDirectionRight ? R.anim.slide_in_right : R.anim.slide_in_left;
            int popExitAnimation = isDirectionRight ? R.anim.slide_out_left : R.anim.slide_out_right;

            transaction.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation);
        }

        // Hide the current fragment if it's not null
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        // Check if the target fragment is already added
        Fragment fragment = fragmentManager.findFragmentByTag(targetFragment.getClass().getName());

        if (fragment == null) {
            // Add the fragment if it is not already added
            fragment = targetFragment;
            transaction.add(R.id.fragmentContainerView, fragment, fragment.getClass().getName());
        } else {
            // Show the existing fragment
            transaction.show(fragment);
        }

        // Commit the transaction
        transaction.addToBackStack(null).commitAllowingStateLoss();

        // Update the current fragment and previous item ID
        currentFragment = fragment;
        if (targetItemId != -1) {
            previousItemId = targetItemId;
        }
    }
}
