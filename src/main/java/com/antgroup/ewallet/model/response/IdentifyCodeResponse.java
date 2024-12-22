package com.antgroup.ewallet.model.response;


import com.antgroup.ewallet.model.entity.BaseResult;

public class IdentifyCodeResponse {
    public String acDecodeConfig;
    public String codeValue;
    public String redirectUrl;
    public boolean isSupported;
    public String postCodeMatchActionType;
    public BaseResult result;
    public String userAgent;
    
    public String getAcDecodeConfig() {
        return acDecodeConfig;
    }
    public void setAcDecodeConfig(String acDecodeConfig) {
        this.acDecodeConfig = acDecodeConfig;
    }
    public String getCodeValue() {
        return codeValue;
    }
    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }
    public String getRedirectUrl() {
        return redirectUrl;
    }
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    public boolean isSupported() {
        return isSupported;
    }
    public void setSupported(boolean isSupported) {
        this.isSupported = isSupported;
    }
    public String getPostCodeMatchActionType() {
        return postCodeMatchActionType;
    }
    public void setPostCodeMatchActionType(String postCodeMatchActionType) {
        this.postCodeMatchActionType = postCodeMatchActionType;
    }
    public BaseResult getResult() {
        return result;
    }
    public void setResult(BaseResult result) {
        this.result = result;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public IdentifyCodeResponse() {
    }

    
}