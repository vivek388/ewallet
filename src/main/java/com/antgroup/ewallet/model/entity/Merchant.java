package com.antgroup.ewallet.model.entity;

public class Merchant{
    public String referenceMerchantId;
    public String merchantMCC;
    public String merchantName;
    public MerchantAddress merchantAddress;
    public String merchantDisplayName;
    public Store store;
    public String getReferenceMerchantId() {
        return referenceMerchantId;
    }
    public void setReferenceMerchantId(String referenceMerchantId) {
        this.referenceMerchantId = referenceMerchantId;
    }
    public String getMerchantMCC() {
        return merchantMCC;
    }
    public void setMerchantMCC(String merchantMCC) {
        this.merchantMCC = merchantMCC;
    }
    public String getMerchantName() {
        return merchantName;
    }
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    public MerchantAddress getMerchantAddress() {
        return merchantAddress;
    }
    public void setMerchantAddress(MerchantAddress merchantAddress) {
        this.merchantAddress = merchantAddress;
    }
    public String getMerchantDisplayName() {
        return merchantDisplayName;
    }
    public void setMerchantDisplayName(String merchantDisplayName) {
        this.merchantDisplayName = merchantDisplayName;
    }
    public Store getStore() {
        return store;
    }
    public void setStore(Store store) {
        this.store = store;
    }

    
}