package com.antgroup.ewallet.model.entity;

public class PaymentQuote{
    public String quoteId;
    public String quoteCurrencyPair;
    public String quotePrice;
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
}