package com.antgroup.ewallet.model.entity;

public class PaymentFactor{
    public String isInStorePayment;
    public String isCashierPayment;
    public String inStorePaymentScenario;
    public String getIsInStorePayment() {
        return isInStorePayment;
    }
    public void setIsInStorePayment(String isInStorePayment) {
        this.isInStorePayment = isInStorePayment;
    }
    public String getIsCashierPayment() {
        return isCashierPayment;
    }
    public void setIsCashierPayment(String isCashierPayment) {
        this.isCashierPayment = isCashierPayment;
    }
    public String getInStorePaymentScenario() {
        return inStorePaymentScenario;
    }
    public void setInStorePaymentScenario(String inStorePaymentScenario) {
        this.inStorePaymentScenario = inStorePaymentScenario;
    }
    
}