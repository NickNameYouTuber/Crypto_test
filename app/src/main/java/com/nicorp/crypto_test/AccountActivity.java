package com.nicorp.crypto_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AllHelpersSetup.setup(this, R.layout.activity_account);

        // Обработчик нажатия на кнопку "Управление счетами"
        ConstraintLayout billsManagementButton = findViewById(R.id.billsManagementButton);
        billsManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this, Bills_Management_Activity.class));
            }
        });
    }
}