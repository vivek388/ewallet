package com.antgroup.ewallet.model.entity;
import java.util.ArrayList;

public class PaymentPromoInfo{
    public ArrayList<PaymentPromoDetail> paymentPromoDetails;

    public ArrayList<PaymentPromoDetail> getPaymentPromoDetails() {
        return paymentPromoDetails;
    }

    public void setPaymentPromoDetails(ArrayList<PaymentPromoDetail> paymentPromoDetails) {
        this.paymentPromoDetails = paymentPromoDetails;
    }

    
}