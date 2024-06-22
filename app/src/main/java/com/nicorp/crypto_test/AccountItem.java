package com.nicorp.crypto_test;

public class AccountItem {
    private String name;
    private String currency;
    private String address;

    public AccountItem(String name, String currency, String address) {
        this.name = name;
        this.currency = currency;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAddress() {
        return address;
    }
}