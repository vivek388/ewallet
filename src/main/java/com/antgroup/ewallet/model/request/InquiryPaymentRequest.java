package com.antgroup.ewallet.model.request;

public class InquiryPaymentRequest {
    public String acquirerId;
    public String paymentRequestId;
    public String pspId;
    
    public String getAcquirerId() {
        return acquirerId;
    }
    public void setAcquirerId(String acquirerId) {
        this.acquirerId = acquirerId;
    }
    public String getPaymentRequestId() {
        return paymentRequestId;
    }
    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }
    public String getPspId() {
        return pspId;
    }
    public void setPspId(String pspId) {
        this.pspId = pspId;
    }
    public InquiryPaymentRequest(String acquirerId, String paymentRequestId, String pspId) {
        this.acquirerId = acquirerId;
        this.paymentRequestId = paymentRequestId;
        this.pspId = pspId;
    }

    
}