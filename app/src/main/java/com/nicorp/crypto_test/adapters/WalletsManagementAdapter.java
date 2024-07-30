package com.nicorp.crypto_test.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transauth.TransAuth;
import com.example.transauth.TransAuthUser;
import com.example.transauth.TransAuthUserDatabaseHelper;
import com.example.transauth.TransAuthWallet;
import com.nicorp.crypto_test.objects.Wallet;
import com.nicorp.crypto_test.R;

import java.util.List;

public class WalletsManagementAdapter extends RecyclerView.Adapter<WalletsManagementAdapter.WalletViewHolder> {

    private List<Wallet> wallets;
    private Context context;
    private TransAuthUserDatabaseHelper db;
    private TransAuthUser currentUser;

    public WalletsManagementAdapter(List<Wallet> wallets, Context context, TransAuthUser currentUser) {
        this.wallets = wallets;
        this.context = context;
        this.currentUser = currentUser;
        this.db = new TransAuthUserDatabaseHelper(context);
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_manage, parent, false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Wallet wallet = wallets.get(position);
        holder.iconWallet.setImageResource(wallet.getLogo());
        holder.titleWallet.setText(wallet.getTitle());
        holder.amountWallet.setText(wallet.getAmount());
        holder.equivalentWallet.setText(wallet.getUsdAmount());
        setCardViewBackground(holder.cardView, holder.iconWallet, holder.titleWallet, holder.amountWallet, holder.equivalentWallet);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    static class WalletViewHolder extends RecyclerView.ViewHolder {
        ImageView iconWallet;
        TextView titleWallet, amountWallet, equivalentWallet;
        CardView cardView;
        ImageView btnDelete;

        WalletViewHolder(View itemView) {
            super(itemView);
            iconWallet = itemView.findViewById(R.id.iconWallet);
            titleWallet = itemView.findViewById(R.id.titleWallet);
            amountWallet = itemView.findViewById(R.id.amountWallet);
            equivalentWallet = itemView.findViewById(R.id.equivalentWallet);
            cardView = itemView.findViewById(R.id.cardView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView titleWallet, TextView amountWallet, TextView equivalentWallet) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.background_color_light));
                cardView.setCardBackgroundColor(dominantColor);

                int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, R.color.text_color_dark) : ContextCompat.getColor(context, R.color.text_color_light);
                titleWallet.setTextColor(textColor);
                amountWallet.setTextColor(textColor);
                equivalentWallet.setTextColor(textColor);
            }
        });
    }

    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color) + 0.587 * android.graphics.Color.green(color) + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness >= 0.3;
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this wallet?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteWallet(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void deleteWallet(int position) {
        Wallet wallet = wallets.get(position);
        TransAuthWallet transAuthWalletToRemove = null;

        for (TransAuthWallet transAuthWallet : currentUser.getWallets()) {
            if (transAuthWallet.getName().equals(wallet.getTitle())) {
                transAuthWalletToRemove = transAuthWallet;
                break;
            }
        }

        if (transAuthWalletToRemove != null) {
            currentUser.removeWallet(transAuthWalletToRemove);
            db.updateUser(currentUser);
            TransAuth.setUser(currentUser);
            wallets.remove(position);
            notifyItemRemoved(position);
            db.deleteWallet(transAuthWalletToRemove.getId());
        }
    }
}