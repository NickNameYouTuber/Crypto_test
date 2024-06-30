package com.example.transauth;

import java.io.Serializable;

public class AccountInfo implements Serializable {
    private String name;
    private String login;
    private String password;
    private String email;

    public AccountInfo(String name, String login, String password, String email) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
