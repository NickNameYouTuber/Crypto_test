package com.example.transauth;

public class Wallet {
    private String address;
    private String platform;
    private String name;

    public Wallet(String address, String platform, String name) {
        this.address = address;
        this.platform = platform;
        this.name = name;
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
}
