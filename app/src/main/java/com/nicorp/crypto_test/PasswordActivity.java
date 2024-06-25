package com.nicorp.crypto_test;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class PasswordActivity extends AppCompatActivity {

    private static final String CORRECT_PASSWORD = "11111";
    private static final String USE_FINGERPRINT_KEY = "use_fingerprint";
    private StringBuilder enteredPassword = new StringBuilder();
    private LinearLayout llDots;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_password);

        llDots = findViewById(R.id.llDots);
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Button button = (Button) gridLayout.getChildAt(i);
            button.setOnClickListener(this::onNumberButtonClick);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean(USE_FINGERPRINT_KEY, false)) {
            startFingerprintAuthentication();
        }
    }

    private void startFingerprintAuthentication() {
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(PasswordActivity.this, "Fingerprint authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                animateCorrectPassword();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(PasswordActivity.this, "Fingerprint authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Use your fingerprint to unlock")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void onNumberButtonClick(View view) {
        Button button = (Button) view;
        String text = button.getText().toString();

        if (text.equals("<")) {
            if (enteredPassword.length() > 0) {
                enteredPassword.deleteCharAt(enteredPassword.length() - 1);
                updateDots();
            }
        } else if (text.equals("O")) {
            checkPassword();
        } else {
            enteredPassword.append(text);
            updateDots();
        }
    }

    private void updateDots() {
        for (int i = 0; i < llDots.getChildCount(); i++) {
            View dot = llDots.getChildAt(i);
            if (i < enteredPassword.length()) {
                dot.setBackgroundResource(R.drawable.selected_dot);
            } else {
                dot.setBackgroundResource(R.drawable.dot);
            }
        }
    }

    private void checkPassword() {
        if (enteredPassword.toString().equals(CORRECT_PASSWORD)) {
            promptFingerprintUsage();
        } else {
            animateWrongPassword();
        }
    }

    private void promptFingerprintUsage() {
        if (!sharedPreferences.getBoolean(USE_FINGERPRINT_KEY, false)) {
            new AlertDialog.Builder(this)
                    .setTitle("Enable Fingerprint Authentication")
                    .setMessage("Would you like to use fingerprint authentication for future logins?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences.edit().putBoolean(USE_FINGERPRINT_KEY, true).apply();
                            animateCorrectPassword();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            animateCorrectPassword();
                        }
                    })
                    .show();
        } else {
            animateCorrectPassword();
        }
    }

    private void animateWrongPassword() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        llDots.startAnimation(shake);
        for (int i = 0; i < llDots.getChildCount(); i++) {
            View dot = llDots.getChildAt(i);
            dot.setBackgroundColor(Color.RED);
        }
        new Handler().postDelayed(this::resetDots, 500);
    }

    private void animateCorrectPassword() {
        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet scaleDown = new AnimatorSet();
        for (int i = 0; i < llDots.getChildCount(); i++) {
            View dot = llDots.getChildAt(i);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(dot, "scaleX", 1f, 0.5f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(dot, "scaleY", 1f, 0.5f);
            scaleDown.playTogether(scaleX, scaleY);
        }
        scaleDown.setDuration(500);

        AnimatorSet rotate = new AnimatorSet();
        for (int i = 0; i < llDots.getChildCount(); i++) {
            View dot = llDots.getChildAt(i);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(dot, "rotation", 0f, 720f);
            rotate.playTogether(rotation);
        }
        rotate.setDuration(2000);

        AnimatorSet scaleUp = new AnimatorSet();
        for (int i = 0; i < llDots.getChildCount(); i++) {
            View dot = llDots.getChildAt(i);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(dot, "scaleX", 0.5f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(dot, "scaleY", 0.5f, 1f);
            scaleUp.playTogether(scaleX, scaleY);
        }
        scaleUp.setDuration(500);

        animatorSet.playSequentially(scaleDown, rotate, scaleUp);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for (int i = 0; i < llDots.getChildCount(); i++) {
                    View dot = llDots.getChildAt(i);
                    // Set background as green circle
                    dot.setBackgroundResource(R.drawable.green_dot);
                }
                // Go to FirstTabActivity
                startActivity(new Intent(PasswordActivity.this, FirstTabActivity.class));
                finish();
            }
        });
        animatorSet.start();
    }

    private void resetDots() {
        for (int i = 0; i < llDots.getChildCount(); i++) {
            View dot = llDots.getChildAt(i);
            dot.setBackgroundResource(R.drawable.dot);
        }
        enteredPassword.setLength(0);
    }
}