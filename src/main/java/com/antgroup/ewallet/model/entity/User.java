package com.antgroup.ewallet.model.entity;

public class User {
    public static String sheet = "Users";

    private Long id;
    private String username;
    private String password;
    private Double balance;

    public Long getId() {
        return id;
    }
    public void setId(Double id) {
        this.id = id.longValue();
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
    public Double getBalance() {
        return balance;
    }
    public void setBalance(Double balance) {
        this.balance = balance;
    }
}