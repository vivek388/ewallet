package com.antgroup.ewallet.model.entity;

public class WalletApiResult {
    public BaseResult result;
    public double id;
    public BaseResult getResult() {
        return result;
    }
    public void setResult(BaseResult result) {
        this.result = result;
    }
    public double getId() {
        return id;
    }
    public void setId(double id) {
        this.id = id;
    }
    public WalletApiResult(BaseResult result, double id) {
        this.result = result;
        this.id = id;
    }
}