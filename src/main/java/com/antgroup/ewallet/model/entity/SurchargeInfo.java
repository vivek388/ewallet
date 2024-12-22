package com.antgroup.ewallet.model.entity;

public class SurchargeInfo{
    public BasePayment surchargeAmount;
    public PaymentQuote surchargeQuote;
    public BasePayment getSurchargeAmount() {
        return surchargeAmount;
    }
    public void setSurchargeAmount(BasePayment surchargeAmount) {
        this.surchargeAmount = surchargeAmount;
    }
    public PaymentQuote getSurchargeQuote() {
        return surchargeQuote;
    }
    public void setSurchargeQuote(PaymentQuote surchargeQuote) {
        this.surchargeQuote = surchargeQuote;
    }
    
}