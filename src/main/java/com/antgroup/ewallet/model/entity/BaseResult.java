package com.antgroup.ewallet.model.entity;

public class BaseResult {
    public String resultCode;
    public String resultMessage;
    public String resultStatus;
    
    public String getResultCode() {
        return resultCode;
    }
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
    public String getResultMessage() {
        return resultMessage;
    }
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
    public String getResultStatus() {
        return resultStatus;
    }
    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }
    public BaseResult(String resultCode, String resultMessage, String resultStatus) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.resultStatus = resultStatus;
    }
    public BaseResult() {
    }

    public BaseResult(boolean isSuccess){
        this.resultCode = "SUCCESS";
        this.resultMessage = "Success";
        this.resultStatus = "S";
    }
}