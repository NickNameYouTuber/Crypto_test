package com.nicorp.crypto_test;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordActivity extends AppCompatActivity {

    private static final String CORRECT_PASSWORD = "12345";
    private StringBuilder enteredPassword = new StringBuilder();
    private LinearLayout llDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        llDots = findViewById(R.id.llDots);
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Button button = (Button) gridLayout.getChildAt(i);
            button.setOnClickListener(this::onNumberButtonClick);
        }
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
            for (int i = 0; i < llDots.getChildCount(); i++) {
                View dot = llDots.getChildAt(i);
                dot.setBackgroundColor(Color.GREEN);
            }
            Toast.makeText(this, "Password Correct!", Toast.LENGTH_SHORT).show();
            // Proceed to the next screen
        } else {
            animateWrongPassword();
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

    private void resetDots() {
        for (int i = 0; i < llDots.getChildCount(); i++) {
            View dot = llDots.getChildAt(i);
            dot.setBackgroundResource(R.drawable.dot);
        }
        enteredPassword.setLength(0);
    }
}