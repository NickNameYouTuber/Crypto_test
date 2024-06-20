package com.nicorp.crypto_test;

public class AccountItem {
    private String name;
    private String balance;
    private String exchange;

    public AccountItem(String name, String balance, String exchange) {
        this.name = name;
        this.balance = balance;
        this.exchange = exchange;
    }

    public String getName() {
        return name;
    }

    public String getBalance() {
        return balance;
    }

    public String getExchange() {
        return exchange;
    }
}
