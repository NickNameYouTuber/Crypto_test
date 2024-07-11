package com.example.transauth;

import java.util.HashMap;
import java.util.List;

public class TransAuthUser {
    private String login;
    private String username;
    private String password;
    private String email;
    private String phone;
    private HashMap<String, String> tokens;
    private List<Wallet> wallets;

    public TransAuthUser() {

    }

    public TransAuthUser(String login, String username, String password, String email, String phone, HashMap<String, String> tokens) {
        this.login = login;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.tokens = tokens;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public HashMap<String, String> getTokens() {
        return tokens;
    }

    public void setTokens(HashMap<String, String> tokens) {
        this.tokens = tokens;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<Wallet> getWallets() {
        return wallets;
    }

    public void setWallets(List<Wallet> wallets) {
        this.wallets = wallets;
    }
}
