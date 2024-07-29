package com.nicorp.crypto_test.objects;

import com.google.gson.annotations.SerializedName;

public class CryptoData {

    @SerializedName("bitcoin")
    private CryptoInfo bitcoin;

    @SerializedName("ethereum")
    private CryptoInfo ethereum;

    @SerializedName("tether")
    private CryptoInfo tether;

    public CryptoInfo getBitcoin() {
        return bitcoin;
    }

    public CryptoInfo getEthereum() {
        return ethereum;
    }

    public CryptoInfo getTether() {
        return tether;
    }
}