package com.nicorp.crypto_test.objects;

public class TransactionItem {

    private String text;
    private int imageResId;
    private String transactionType;

    public TransactionItem(String text, int imageResId, String transactionType) {
        this.text = text;
        this.imageResId = imageResId;
        this.transactionType = transactionType;
    }

    public String getText() {
        return text;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTransactionType() {
        return transactionType;
    }
}