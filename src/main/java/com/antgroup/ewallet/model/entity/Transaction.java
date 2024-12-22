package com.antgroup.ewallet.model.entity;

import java.util.Date;

public class Transaction {
    public static String sheet = "Transactions";
    
    private double id;
    private Date dateTime;
    private String customerId;
    private double amount;
    private String details;
    private String statusCode;
    private String statusMessage;
    private String status;
    private String paymentRequestId;
    private String payCurrency;
    private String payAmount;
    private String payToCurrency;
    private String payToAmount;
    private String paymentTime;
    private String quoteId;
    private String quoteCurrencyPair;
    private String quotePrice;
    private String pspId;
    private String acquirerId;
    private String promoJson;
    private String refundRequestId;
    

    public Transaction(double id, Date dateTime, String customerId, double amount, String details, String statusCode,
            String statusMessage, String status, String paymentRequestId, String payCurrency, String payAmount,
            String payToCurrency, String payToAmount, String paymentTime, String quoteId, String quoteCurrencyPair,
            String quotePrice, String pspId, String acquirerId, String promoJson, String refundRequestId) {
        this.id = id;
        this.dateTime = dateTime;
        this.customerId = customerId;
        this.amount = amount;
        this.details = details;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.status = status;
        this.paymentRequestId = paymentRequestId;
        this.payCurrency = payCurrency;
        this.payAmount = payAmount;
        this.payToCurrency = payToCurrency;
        this.payToAmount = payToAmount;
        this.paymentTime = paymentTime;
        this.quoteId = quoteId;
        this.quoteCurrencyPair = quoteCurrencyPair;
        this.quotePrice = quotePrice;
        this.pspId = pspId;
        this.acquirerId = acquirerId;
        this.promoJson = promoJson;
        this.refundRequestId = refundRequestId;
    }
    public static String getSheet() {
        return sheet;
    }
    public static void setSheet(String sheet) {
        Transaction.sheet = sheet;
    }
    public double getId() {
        return id;
    }
    public void setId(double id) {
        this.id = id;
    }
    public Date getDateTime() {
        return dateTime;
    }
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public String getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getPaymentRequestId() {
        return paymentRequestId;
    }
    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }
    public String getPayCurrency() {
        return payCurrency;
    }
    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }
    public String getPayAmount() {
        return payAmount;
    }
    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }
    public String getPayToCurrency() {
        return payToCurrency;
    }
    public void setPayToCurrency(String payToCurrency) {
        this.payToCurrency = payToCurrency;
    }
    public String getPayToAmount() {
        return payToAmount;
    }
    public void setPayToAmount(String payToAmount) {
        this.payToAmount = payToAmount;
    }
    public String getPaymentTime() {
        return paymentTime;
    }
    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteCurrencyPair() {
        return quoteCurrencyPair;
    }

    public void setQuoteCurrencyPair(String quoteCurrencyPair) {
        this.quoteCurrencyPair = quoteCurrencyPair;
    }

    public String getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(String quotePrice) {
        this.quotePrice = quotePrice;
    }
    public String getPspId() {
        return pspId;
    }
    public void setPspId(String pspId) {
        this.pspId = pspId;
    }
    public String getAcquirerId() {
        return acquirerId;
    }
    public void setAcquirerId(String acquirerId) {
        this.acquirerId = acquirerId;
    }
    public String getStatusMessage() {
        return statusMessage;
    }
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
    public String getPromoJson() {
        return promoJson;
    }
    public void setPromoJson(String promoJson) {
        this.promoJson = promoJson;
    }
    public String getRefundRequestId() {
        return refundRequestId;
    }
    public void setRefundRequestId(String refundRequestId) {
        this.refundRequestId = refundRequestId;
    }
    

    
}