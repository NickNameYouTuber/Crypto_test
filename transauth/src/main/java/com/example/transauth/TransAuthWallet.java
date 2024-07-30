package com.example.transauth;

public class TransAuthWallet {
    private int id;
    private String address;
    private String platform;
    private String name;
    private double balance;
    private String currency;

    public TransAuthWallet(String address, String platform, String name) {
        this.address = address;
        this.platform = platform;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public String getPlatform() {
        return platform;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
