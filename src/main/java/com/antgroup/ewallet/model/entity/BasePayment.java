package com.antgroup.ewallet.model.entity;

public class BasePayment {
    public String value;
    public String currency;
    
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public BasePayment(String value, String currency) {
        this.value = value;
        this.currency = currency;
    }
    public BasePayment() {
    }

    
}