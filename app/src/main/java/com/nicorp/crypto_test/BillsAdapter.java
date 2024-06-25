package com.nicorp.crypto_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillViewHolder> {

    private Context context;
    private List<Bill> bills;
    private int parentWidth;

    public BillsAdapter(Context context, List<Bill> bills) {
        this.context = context;
        this.bills = bills;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bill, parent, false);
        parentWidth = parent.getMeasuredWidth();
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.ivBillLogo.setImageResource(bill.getLogo());
        holder.tvBillTitle.setText(bill.getTitle());
        holder.tvBillAmount.setText(bill.getAmount());
        holder.tvBillUsdAmount.setText(bill.getUsdAmount());

        // Устанавливаем ширину и высоту для соотношения 5/9
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = parentWidth / 3;
        layoutParams.height = (int) (layoutParams.width * (5.0 / 9.0));
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBillLogo;
        TextView tvBillTitle, tvBillAmount, tvBillUsdAmount;
        CardView cardView;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBillLogo = itemView.findViewById(R.id.ivBillLogo);
            tvBillTitle = itemView.findViewById(R.id.tvBillTitle);
            tvBillAmount = itemView.findViewById(R.id.tvBillAmount);
            tvBillUsdAmount = itemView.findViewById(R.id.tvBillUsdAmount);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}