package com.antgroup.ewallet.model.entity;

public class OrderAmount{
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
    
}