package com.nicorp.crypto_test.objects;

public class PaymentRecipient {
    private String name;
    private String address;
    private int amount;
    private String currency;

    public PaymentRecipient(String name, String address, int amount, String currency) {
        this.name = name;
        this.address = address;
        this.amount = amount;
        this.currency = currency;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getAmount() { return amount; }
    public String getCurrency() { return currency; }
}
