package com.nicorp.crypto_test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Bills_Management_Adapter extends RecyclerView.Adapter<Bills_Management_Adapter.BillViewHolder> {

    private List<Bill> bills;

    public Bills_Management_Adapter(List<Bill> bills) {
        this.bills = bills;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_manage, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.iconBill.setImageResource(bill.getLogo());
        holder.titleBill.setText(bill.getTitle());
        holder.amountBill.setText(bill.getAmount());
        holder.equivalentBill.setText(bill.getUsdAmount());
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        ImageView iconBill;
        TextView titleBill, amountBill, equivalentBill;

        BillViewHolder(View itemView) {
            super(itemView);
            iconBill = itemView.findViewById(R.id.iconBill);
            titleBill = itemView.findViewById(R.id.titleBill);
            amountBill = itemView.findViewById(R.id.amountBill);
            equivalentBill = itemView.findViewById(R.id.equivalentBill);
        }
    }
}