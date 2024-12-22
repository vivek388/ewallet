package com.antgroup.ewallet.model.response;

import com.antgroup.ewallet.model.entity.BaseResult;

public class RefundResponse {
    public String refundId;
    public String refundTime;
    public BaseResult result;
    
    public RefundResponse(String refundId, String refundTime, BaseResult result) {
        this.refundId = refundId;
        this.refundTime = refundTime;
        this.result = result;
    }
    public String getRefundId() {
        return refundId;
    }
    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }
    public String getRefundTime() {
        return refundTime;
    }
    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }
    public BaseResult getResult() {
        return result;
    }
    public void setResult(BaseResult result) {
        this.result = result;
    }
    

}