package com.antgroup.ewallet.model.request;

public class IdentifyCodeRequest {
    private String codeValue;
    private String customerId;
    
    
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getCodeValue() {
        return codeValue;
    }
    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }
}