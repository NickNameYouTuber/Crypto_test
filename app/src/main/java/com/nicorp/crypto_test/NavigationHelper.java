package com.nicorp.crypto_test;

import static com.nicorp.crypto_test.QRFragment.restartScanning;
import static com.nicorp.crypto_test.QRFragment.stopScanning;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHelper {
    private static Fragment currentFragment;
    private static int previousItemId = R.id.nav_wallet;
    private static BottomNavigationView bottomNav;
    private static boolean isNavigating = false;

    public static void setupBottomNavigation(FragmentActivity activity) {
        bottomNav = activity.findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            if (isNavigating) return false;
            Fragment targetFragment = getFragmentById(item.getItemId());
            if (targetFragment != null) {
                switchFragment(activity, targetFragment, item.getItemId());
                return true;
            }
            return false;
        });

        if (activity.getSupportFragmentManager().getFragments().isEmpty()) {
            bottomNav.setSelectedItemId(R.id.nav_wallet);
        } else {
            currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
            previousItemId = bottomNav.getSelectedItemId();
        }
    }

    private static Fragment getFragmentById(int itemId) {
//        switch (itemId) {
//            case R.id.nav_wallet:
//                return new BalanceFragment();
//            case R.id.nav_qr:
//                return new QRFragment();
//            case R.id.nav_profile:
//                return new ProfileFragment();
//            default:
//                return null;
//        }
        if (itemId == R.id.nav_wallet) return new BalanceFragment();
        if (itemId == R.id.nav_qr) return new QRFragment();
        if (itemId == R.id.nav_profile) {

            return new ProfileFragment();}
        return null;
    }

    private static void switchFragment(FragmentActivity activity, Fragment targetFragment, int newItemId) {
        isNavigating = true;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .setCustomAnimations(getEnterAnimation(newItemId), getExitAnimation(newItemId));

        if (currentFragment != null) transaction.hide(currentFragment);

        Fragment fragment = fragmentManager.findFragmentByTag(targetFragment.getClass().getName());
        if (fragment == null) {
            fragment = targetFragment;
            transaction.add(R.id.fragmentContainerView, fragment, fragment.getClass().getName());
        } else {
            transaction.show(fragment);
        }

        transaction.commitAllowingStateLoss();
        currentFragment = fragment;
        previousItemId = newItemId;
        bottomNav.setSelectedItemId(newItemId);
        isNavigating = false;
        Log.d("Navigation", "Switched to " + targetFragment.getClass().getName());
        if (targetFragment.getClass().getName().equals(QRFragment.class.getName())) {
            Log.d("QRFragment", "Restarting scanning");
            restartScanning();
        } else {
            Log.d("QRFragment", "Not restarting scanning");
            stopScanning();
        }
    }

    private static int getEnterAnimation(int newItemId) {
        return newItemId > previousItemId ? R.anim.slide_in_left : R.anim.slide_in_right;
    }

    private static int getExitAnimation(int newItemId) {
        return newItemId > previousItemId ? R.anim.slide_out_right : R.anim.slide_out_left;
    }

    public static void navigateToFragment(FragmentActivity activity, Fragment targetFragment) {
        int navId = getNavId(targetFragment);
        if (navId != -1) {
            bottomNav.setSelectedItemId(navId);
        }
        switchFragment(activity, targetFragment, navId);
        Log.d("Navigation", "Navigating to " + targetFragment.getClass().getName());
        if (targetFragment.getClass().getName().equals(QRFragment.class.getName())) {
            Log.d("QRFragment", "Restarting scanning");
            restartScanning();
        } else {
            Log.d("QRFragment", "Not restarting scanning");
            stopScanning();
        }
    }

    public static void navigateToFragment(FragmentActivity activity, Fragment targetFragment, @Nullable Bundle bundle) {
        if (bundle != null) targetFragment.setArguments(bundle);
        navigateToFragment(activity, targetFragment);
    }

    private static int getNavId(Fragment fragment) {
        if (fragment instanceof BalanceFragment) return R.id.nav_wallet;
        if (fragment instanceof QRFragment) return R.id.nav_qr;
        if (fragment instanceof ProfileFragment) return R.id.nav_profile;
        return -1;
    }

    public static void handleBackButton(FragmentActivity activity, Fragment targetFragment, int targetItemId) {
        switchFragment(activity, targetFragment, targetItemId);
    }
}
