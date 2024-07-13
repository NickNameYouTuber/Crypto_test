package com.nicorp.crypto_test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transauth.TransAuth;
import com.example.transauth.TransAuthUser;
import com.example.transauth.TransAuthUserDatabaseHelper;
import com.example.transauth.Wallet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillsManagementAdapter extends RecyclerView.Adapter<BillsManagementAdapter.BillViewHolder> {

    private List<Bill> bills;
    private Context context;
    private TransAuthUserDatabaseHelper db;
    private TransAuthUser currentUser;

    public BillsManagementAdapter(List<Bill> bills, Context context, TransAuthUser currentUser) {
        this.bills = bills;
        this.context = context;
        this.currentUser = currentUser;
        this.db = new TransAuthUserDatabaseHelper(context);
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_manage, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Bill bill = bills.get(position);
        holder.iconBill.setImageResource(bill.getLogo());
        holder.titleBill.setText(bill.getTitle());
        holder.amountBill.setText(bill.getAmount());
        holder.equivalentBill.setText(bill.getUsdAmount());
        setCardViewBackground(holder.cardView, holder.iconBill, holder.titleBill, holder.amountBill, holder.equivalentBill);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        ImageView iconBill;
        TextView titleBill, amountBill, equivalentBill;
        CardView cardView;
        ImageView btnDelete;

        BillViewHolder(View itemView) {
            super(itemView);
            iconBill = itemView.findViewById(R.id.iconBill);
            titleBill = itemView.findViewById(R.id.titleBill);
            amountBill = itemView.findViewById(R.id.amountBill);
            equivalentBill = itemView.findViewById(R.id.equivalentBill);
            cardView = itemView.findViewById(R.id.cardView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void setCardViewBackground(CardView cardView, ImageView imageView, TextView titleBill, TextView amountBill, TextView equivalentBill) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        int[] pixels = getBitmapPixels(bitmap);
        int dominantColor = getDominantColor(pixels);

        cardView.setCardBackgroundColor(dominantColor);

        Log.d("BillViewHolder", "Is dark color: " + isDarkColor(dominantColor));

        // Определяем цвет текста на основе яркости фона
        int textColor = isDarkColor(dominantColor) ? ContextCompat.getColor(context, R.color.text_color_dark) : ContextCompat.getColor(context, R.color.text_color_light);
        titleBill.setTextColor(textColor);
        amountBill.setTextColor(textColor);
        equivalentBill.setTextColor(textColor);
    }

    // Получаем массив пикселей изображения
    private int[] getBitmapPixels(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    // Находим наиболее часто встречающийся цвет среди пикселей
    private int getDominantColor(int[] pixels) {
        Map<Integer, Integer> colorCountMap = new HashMap<>();

        for (int color : pixels) {
            if (colorCountMap.containsKey(color)) {
                colorCountMap.put(color, colorCountMap.get(color) + 1);
            } else {
                colorCountMap.put(color, 1);
            }
        }

        Log.d("BillViewHolder", "Color count map size: " + colorCountMap.size());
        Log.d("BillViewHolder", "Color count map: " + colorCountMap);

        int maxCount = 0;
        int dominantColor = Color.WHITE; // По умолчанию, если не найден другой цвет
        for (Map.Entry<Integer, Integer> entry : colorCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                dominantColor = entry.getKey();
            }
        }

        return dominantColor;
    }

    // Helper function to determine if a color is dark
    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.3;
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this bill?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteBill(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void deleteBill(int position) {
        Bill bill = bills.get(position);
        Wallet walletToRemove = null;

        for (Wallet wallet : currentUser.getWallets()) {
            if (wallet.getName().equals(bill.getTitle())) {
                walletToRemove = wallet;
                break;
            }
        }

        if (walletToRemove != null) {
            currentUser.removeWallet(walletToRemove);
            db.updateUser(currentUser);
            TransAuth.setUser(currentUser);
            bills.remove(position);
            notifyItemRemoved(position);
            db.deleteWallet(walletToRemove.getId());
        }
    }
}
