package com.nicorp.crypto_test.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nicorp.crypto_test.objects.Account;
import com.nicorp.crypto_test.R;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Account> accountList;
    private Context context;
    private int selectedPosition = -1;

    public AccountAdapter(Context context, List<Account> accountList) {
        this.context = context;
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.accountName.setText(account.getName());
//        holder.accountBalance.setText("Balance: " + account.getBalance());
        holder.accountCurrency.setText("Currency: " + account.getCurrency());

        holder.itemView.setBackgroundColor(selectedPosition == position ? Color.LTGRAY : Color.WHITE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Selected account: " + account.getAddress());
                System.out.println("Selected account: " + account.getAddress());

                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
                // Сохранение выбранного счета в SharedPreferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("CryptoPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("selected_account", selectedPosition);
                editor.apply();
                // Обновление информации о счете
//                ((Ьф) context).updateSelectedAccount(account);
            }
        });
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView accountName, accountBalance, accountCurrency;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.accountName);
            accountBalance = itemView.findViewById(R.id.accountBalance);
            accountCurrency = itemView.findViewById(R.id.accountCurrency);
        }
    }
}
