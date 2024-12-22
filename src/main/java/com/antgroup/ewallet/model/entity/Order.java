package com.antgroup.ewallet.model.entity;

public class Order{
    public String referenceOrderId;
    public String orderDescription;
    public OrderAmount orderAmount;
    public Merchant merchant;
    public String getReferenceOrderId() {
        return referenceOrderId;
    }
    public void setReferenceOrderId(String referenceOrderId) {
        this.referenceOrderId = referenceOrderId;
    }
    public String getOrderDescription() {
        return orderDescription;
    }
    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }
    public OrderAmount getOrderAmount() {
        return orderAmount;
    }
    public void setOrderAmount(OrderAmount orderAmount) {
        this.orderAmount = orderAmount;
    }
    public Merchant getMerchant() {
        return merchant;
    }
    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    
}