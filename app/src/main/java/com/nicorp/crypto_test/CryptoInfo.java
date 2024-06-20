package com.nicorp.crypto_test;

import com.google.gson.annotations.SerializedName;

public class CryptoInfo {

    @SerializedName("usd")
    private double price;

    public double getPrice() {
        return price;
    }
}