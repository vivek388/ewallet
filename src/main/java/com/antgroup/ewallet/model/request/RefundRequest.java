package com.antgroup.ewallet.model.request;

import com.antgroup.ewallet.model.entity.*;

public class RefundRequest {
    public String acquirerId;
    public String paymentId;
    public String paymentRequestId;
    public String pspId;
    public BasePayment refundAmount;
    public BasePayment refundFromAmount;
    public PaymentPromoInfo refundPromoInfo;
    public PaymentQuote refundQuote;
    public String refundRequestId;
    public String extendInfo;
    public SurchargeInfo surchargeInfo;

    public RefundRequest(String acquirerId, String paymentId, String paymentRequestId, String pspId,
            BasePayment refundAmount, BasePayment refundFromAmount, PaymentPromoInfo refundPromoInfo,
            PaymentQuote refundQuote, String refundRequestId, String extendInfo, SurchargeInfo surchargeInfo) {
        this.acquirerId = acquirerId;
        this.paymentId = paymentId;
        this.paymentRequestId = paymentRequestId;
        this.pspId = pspId;
        this.refundAmount = refundAmount;
        this.refundFromAmount = refundFromAmount;
        this.refundPromoInfo = refundPromoInfo;
        this.refundQuote = refundQuote;
        this.refundRequestId = refundRequestId;
        this.extendInfo = extendInfo;
        this.surchargeInfo = surchargeInfo;
    }
    public String getAcquirerId() {
        return acquirerId;
    }
    public void setAcquirerId(String acquirerId) {
        this.acquirerId = acquirerId;
    }
    public String getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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
    public BasePayment getRefundAmount() {
        return refundAmount;
    }
    public void setRefundAmount(BasePayment refundAmount) {
        this.refundAmount = refundAmount;
    }
    public BasePayment getRefundFromAmount() {
        return refundFromAmount;
    }
    public void setRefundFromAmount(BasePayment refundFromAmount) {
        this.refundFromAmount = refundFromAmount;
    }
    public PaymentQuote getRefundQuote() {
        return refundQuote;
    }
    public void setRefundQuote(PaymentQuote refundQuote) {
        this.refundQuote = refundQuote;
    }
    public String getRefundRequestId() {
        return refundRequestId;
    }
    public void setRefundRequestId(String refundRequestId) {
        this.refundRequestId = refundRequestId;
    }
    public PaymentPromoInfo getRefundPromoInfo() {
        return refundPromoInfo;
    }
    public void setRefundPromoInfo(PaymentPromoInfo refundPromoInfo) {
        this.refundPromoInfo = refundPromoInfo;
    }
    public String getExtendInfo() {
        return extendInfo;
    }
    public SurchargeInfo getSurchargeInfo() {
        return surchargeInfo;
    }
    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }
    public void setSurchargeInfo(SurchargeInfo surchargeInfo) {
        this.surchargeInfo = surchargeInfo;
    }
    
}