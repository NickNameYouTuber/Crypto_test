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
    private EditText accountBalanceEditText;
    private EditText accountExchangeEditText;
    private Button addAccountButton;
    private OnAccountAddedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_account, null);
        dialog.setContentView(view);

        accountNameEditText = view.findViewById(R.id.accountNameEditText);
        accountBalanceEditText = view.findViewById(R.id.accountBalanceEditText);
        accountExchangeEditText = view.findViewById(R.id.accountExchangeEditText);
        addAccountButton = view.findViewById(R.id.addAccountButton);

        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = accountNameEditText.getText().toString().trim();
                String balance = accountBalanceEditText.getText().toString().trim();
                String exchange = accountExchangeEditText.getText().toString().trim();

                if (!name.isEmpty() && !balance.isEmpty() && !exchange.isEmpty()) {
                    AccountItem accountItem = new AccountItem(name, balance, exchange);
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
