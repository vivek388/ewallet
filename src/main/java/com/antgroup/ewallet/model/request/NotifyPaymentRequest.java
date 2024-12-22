package com.antgroup.ewallet.model.request;

import com.antgroup.ewallet.model.entity.*;

public class NotifyPaymentRequest {
    public BaseResult paymentResult;
    public String paymentRequestId;
    public String paymentId;
    public String paymentTime;
    public BasePayment paymentAmount;
    public BasePayment payToAmount;
    public String customerId;
    
    public BaseResult getPaymentResult() {
        return paymentResult;
    }
    public void setPaymentResult(BaseResult paymentResult) {
        this.paymentResult = paymentResult;
    }
    public String getPaymentRequestId() {
        return paymentRequestId;
    }
    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }
    public String getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public String getPaymentTime() {
        return paymentTime;
    }
    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }
    public BasePayment getPaymentAmount() {
        return paymentAmount;
    }
    public void setPaymentAmount(BasePayment paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    public BasePayment getPayToAmount() {
        return payToAmount;
    }
    public void setPayToAmount(BasePayment payToAmount) {
        this.payToAmount = payToAmount;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public NotifyPaymentRequest(BaseResult paymentResult, String paymentRequestId, String paymentId, String paymentTime,
            BasePayment paymentAmount, BasePayment payToAmount, String customerId) {
        this.paymentResult = paymentResult;
        this.paymentRequestId = paymentRequestId;
        this.paymentId = paymentId;
        this.paymentTime = paymentTime;
        this.paymentAmount = paymentAmount;
        this.payToAmount = payToAmount;
        this.customerId = customerId;
    }
    
}