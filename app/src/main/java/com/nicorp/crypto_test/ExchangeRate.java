package com.nicorp.crypto_test;

public class ExchangeRate {
    private int logo;
    private String currencyName;
    private String rate;

    public ExchangeRate(int logo, String currencyName, String rate) {
        this.logo = logo;
        this.currencyName = currencyName;
        this.rate = rate;
    }

    public int getLogo() {
        return logo;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getRate() {
        return rate;
    }
}
