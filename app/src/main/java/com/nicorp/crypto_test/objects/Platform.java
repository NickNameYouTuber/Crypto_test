package com.nicorp.crypto_test.objects;

public class Platform {
    private String name;
    private int imageResId;

    public Platform(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
