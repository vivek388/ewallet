package com.antgroup.ewallet.model.response;

import com.antgroup.ewallet.model.entity.*;

import java.util.Date;

public class UserInitiatedPayResponse {
    public BaseResult result;
    public String acquirerId;
    public String pspId;
    public String codeType;
    public String paymentRequestId;
    public Order order;
    public BasePayment paymentAmount;
    public BasePayment payToAmount;
    public PaymentFactor paymentFactor;
    public PaymentQuote paymentQuote;
    public PaymentPromoInfo paymentPromoInfo;
    public ActionForm actionForm;
    public Date paymentExpiryTime;
    public String paymentRedirectUrl;
    public BaseResult getResult() {
        return result;
    }
    public void setResult(BaseResult result) {
        this.result = result;
    }
    public String getAcquirerId() {
        return acquirerId;
    }
    public void setAcquirerId(String acquirerId) {
        this.acquirerId = acquirerId;
    }
    public String getPspId() {
        return pspId;
    }
    public void setPspId(String pspId) {
        this.pspId = pspId;
    }
    public String getCodeType() {
        return codeType;
    }
    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }
    public String getPaymentRequestId() {
        return paymentRequestId;
    }
    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
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
    public PaymentFactor getPaymentFactor() {
        return paymentFactor;
    }
    public void setPaymentFactor(PaymentFactor paymentFactor) {
        this.paymentFactor = paymentFactor;
    }
    public PaymentQuote getPaymentQuote() {
        return paymentQuote;
    }
    public void setPaymentQuote(PaymentQuote paymentQuote) {
        this.paymentQuote = paymentQuote;
    }
    public ActionForm getActionForm() {
        return actionForm;
    }
    public void setActionForm(ActionForm actionForm) {
        this.actionForm = actionForm;
    }
    public Date getPaymentExpiryTime() {
        return paymentExpiryTime;
    }
    public void setPaymentExpiryTime(Date paymentExpiryTime) {
        this.paymentExpiryTime = paymentExpiryTime;
    }
    public PaymentPromoInfo getPaymentPromoInfo() {
        return paymentPromoInfo;
    }
    public void setPaymentPromoInfo(PaymentPromoInfo paymentPromoInfo) {
        this.paymentPromoInfo = paymentPromoInfo;
    }
    public String getPaymentRedirectUrl() {
        return paymentRedirectUrl;
    }
    public void setPaymentRedirectUrl(String paymentRedirectUrl) {
        this.paymentRedirectUrl = paymentRedirectUrl;
    }

    
}