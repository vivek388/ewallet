package com.antgroup.ewallet.model.entity;

public class PaymentPromoDetail{
    public String promoId;
    public String promoName;
    public String promoType;
    public BasePayment savingsAmount;
    public String getPromoId() {
        return promoId;
    }
    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }
    public String getPromoName() {
        return promoName;
    }
    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }
    public String getPromoType() {
        return promoType;
    }
    public void setPromoType(String promoType) {
        this.promoType = promoType;
    }
    public BasePayment getSavingsAmount() {
        return savingsAmount;
    }
    public void setSavingsAmount(BasePayment savingsAmount) {
        this.savingsAmount = savingsAmount;
    }
    
}