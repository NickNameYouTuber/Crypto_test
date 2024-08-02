package com.nicorp.crypto_test.objects;

public class Bank {

    private String name;
    private int logoResource;

    public Bank(String name, int logoResource) {
        this.name = name;
        this.logoResource = logoResource;
    }

    public String getName() {
        return name;
    }

    public int getLogoResource() {
        return logoResource;
    }
}
