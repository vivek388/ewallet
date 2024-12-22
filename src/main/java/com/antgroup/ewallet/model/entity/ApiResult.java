package com.antgroup.ewallet.model.entity;

public class ApiResult {
    public BaseResult result;

    public BaseResult getResult() {
        return result;
    }

    public void setResult(BaseResult result) {
        this.result = result;
    }

    public ApiResult(BaseResult result) {
        this.result = result;
    }

    public ApiResult() {
    }
    
}