package com.antgroup.ewallet.model.entity;

public class Store{
    public String referenceStoreId;
    public String storeName;
    public String storeMCC;
    public String getReferenceStoreId() {
        return referenceStoreId;
    }
    public void setReferenceStoreId(String referenceStoreId) {
        this.referenceStoreId = referenceStoreId;
    }
    public String getStoreName() {
        return storeName;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public String getStoreMCC() {
        return storeMCC;
    }
    public void setStoreMCC(String storeMCC) {
        this.storeMCC = storeMCC;
    }
    
}