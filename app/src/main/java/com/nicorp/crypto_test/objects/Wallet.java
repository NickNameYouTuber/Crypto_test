package com.nicorp.crypto_test.objects;

public class Wallet {
    private int logo;
    private String title;
    private String amount;
    private String usdAmount;

    public Wallet(int logo, String title, String amount, String usdAmount) {
        this.logo = logo;
        this.title = title;
        this.amount = amount;
        this.usdAmount = usdAmount;
    }

    public int getLogo() {
        return logo;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public String getUsdAmount() {
        return usdAmount;
    }
}
