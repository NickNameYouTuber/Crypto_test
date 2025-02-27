package com.nicorp.crypto_test.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.transauth.MessagePermissions;
import com.example.transauth.MessageReceiver;
import com.example.transauth.TransAuth;
import com.example.transauth.TransAuthButton;
import com.example.transauth.TransAuthUserDatabaseHelper;
import com.nicorp.crypto_test.AllHelpersSetup;
import com.nicorp.crypto_test.R;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;

public class PasswordActivity extends AppCompatActivity {

    private static final String CORRECT_PASSWORD = "11111";
    private static final String USE_FINGERPRINT_KEY = "use_fingerprint";
    private StringBuilder enteredPassword = new StringBuilder();
    private LinearLayout llDots;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private SharedPreferences sharedPreferences;
    private MessageReceiver messageReceiver;
    private TransAuthButton transAuthButton;
    private static TransAuth transAuth;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AllHelpersSetup.setup(this, R.layout.activity_password, false);

        // Initialize TransAuth
        transAuth = new TransAuth("1234");
        TransAuth.addPermissions(MessagePermissions.GET_LOGIN);
        TransAuth.addPermissions(MessagePermissions.GET_USERNAME);

        Log.d("transAuth", Arrays.toString(TransAuth.getPermissionsArray()));

        transAuthButton = findViewById(R.id.transAuthButton);
        transAuthButton.setSuccessActivityClass(MainActivity.class);

        messageReceiver = new MessageReceiver(this, new MessageReceiver.MessageListener() {
            @Override
            public void onMessageReceived(Map<String, String> message) {
                Log.d("PasswordActivity", "Received message: " + message.toString());
                Toast.makeText(PasswordActivity.this, "Received message: " + message.toString(), Toast.LENGTH_LONG).show();
            }
        });

        messageReceiver.register(this);

        llDots = findViewById(R.id.llDots);
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            if (i == gridLayout.getChildCount() - 1) {
                break;
            }
            Button button = (Button) gridLayout.getChildAt(i);
            button.setOnClickListener(this::onNumberButtonClick);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean(USE_FINGERPRINT_KEY, false)) {
            startFingerprintAuthentication();
        }

        ImageButton fingerButton = findViewById(R.id.fingerButton);
        fingerButton.setOnClickListener(v -> startFingerprintAuthentication());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageReceiver.unregister(this);
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
            startFingerprintAuthentication();
        } else {
            enteredPassword.append(text);
            updateDots();

            if (enteredPassword.length() == 5) {
                checkPassword();
            }
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
                    dot.setBackgroundResource(R.drawable.green_dot);
                }
                startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                TransAuthUserDatabaseHelper db = new TransAuthUserDatabaseHelper(PasswordActivity.this);
                if (db.getUser("nicktaser") != null) {
                    TransAuth.setUser(db.getUser("nicktaser"));
                }
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

    public static TransAuth getTransAuth() {
        return transAuth;
    }

    public static void setTransAuth(TransAuth transAuth) {
        PasswordActivity.transAuth = transAuth;
    }
}