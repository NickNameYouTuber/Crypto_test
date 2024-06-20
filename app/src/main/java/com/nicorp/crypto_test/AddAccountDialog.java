package com.nicorp.crypto_test;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddAccountDialog extends DialogFragment {

    private EditText accountNameEditText;
    private EditText accountCurrencyEditText;
    private EditText accountAddressEditText; // Новое поле для адреса кошелька
    private Button addAccountButton;
    private OnAccountAddedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_account, null);
        dialog.setContentView(view);

        accountNameEditText = view.findViewById(R.id.accountNameEditText);
        accountCurrencyEditText = view.findViewById(R.id.accountCurrencyEditText);
        accountAddressEditText = view.findViewById(R.id.accountAddressEditText); // Инициализация нового поля
        addAccountButton = view.findViewById(R.id.addAccountButton);

        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = accountNameEditText.getText().toString().trim();
                String currency = accountCurrencyEditText.getText().toString().trim();
                String address = accountAddressEditText.getText().toString().trim(); // Получение адреса кошелька

                if (!name.isEmpty() && !currency.isEmpty() && !address.isEmpty()) {
                    AccountItem accountItem = new AccountItem(name, currency, address);
                    if (listener != null) {
                        listener.onAccountAdded(accountItem);
                    }
                    dismiss();
                }
            }
        });

        return dialog;
    }

    public void setOnAccountAddedListener(OnAccountAddedListener listener) {
        this.listener = listener;
    }

    public interface OnAccountAddedListener {
        void onAccountAdded(AccountItem accountItem);
    }
}
