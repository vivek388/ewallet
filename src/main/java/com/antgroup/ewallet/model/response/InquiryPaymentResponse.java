package com.antgroup.ewallet.model.response;

import com.antgroup.ewallet.model.entity.BasePayment;
import com.antgroup.ewallet.model.entity.BaseResult;

public class InquiryPaymentResponse {
    public String customerId;
    public BasePayment payToAmount;
    public BasePayment paymentAmount;
    public String paymentId;
    public BaseResult paymentResult;
    public String paymentTime;
    public BaseResult result;

    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public BasePayment getPayToAmount() {
        return payToAmount;
    }
    public void setPayToAmount(BasePayment payToAmount) {
        this.payToAmount = payToAmount;
    }
    public BasePayment getPaymentAmount() {
        return paymentAmount;
    }
    public void setPaymentAmount(BasePayment paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    public String getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public BaseResult getPaymentResult() {
        return paymentResult;
    }
    public void setPaymentResult(BaseResult paymentResult) {
        this.paymentResult = paymentResult;
    }
    public String getPaymentTime() {
        return paymentTime;
    }
    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }
    public BaseResult getResult() {
        return result;
    }
    public void setResult(BaseResult result) {
        this.result = result;
    }
    public InquiryPaymentResponse(String customerId, BasePayment payToAmount, BasePayment paymentAmount,
                                  String paymentId, BaseResult paymentResult, String paymentTime, BaseResult result) {
        this.customerId = customerId;
        this.payToAmount = payToAmount;
        this.paymentAmount = paymentAmount;
        this.paymentId = paymentId;
        this.paymentResult = paymentResult;
        this.paymentTime = paymentTime;
        this.result = result;
    }


}
