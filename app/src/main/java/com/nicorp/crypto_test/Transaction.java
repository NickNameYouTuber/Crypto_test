package com.nicorp.crypto_test;

public class Transaction {
    private int logo;
    private String name;
    private String amount;

    public Transaction(int logo, String name, String amount) {
        this.logo = logo;
        this.name = name;
        this.amount = amount;
    }

    public int getLogo() {
        return logo;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }
}
