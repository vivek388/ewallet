package com.antgroup.ewallet.model.response;

public class CodeResponse {
    private String rawCode;
    private String code;
    private boolean verified;
    private IdentifyCodeResponse result;

    
    public boolean isVerified() {
        return verified;
    }
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    public String getRawCode() {
        return rawCode;
    }
    public void setRawCode(String rawCode) {
        this.rawCode = rawCode;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public IdentifyCodeResponse getResult() {
        return result;
    }
    public void setResult(IdentifyCodeResponse result) {
        this.result = result;
    }
}