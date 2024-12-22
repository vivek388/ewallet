package com.antgroup.ewallet.model.entity

class Merchant {
    var referenceMerchantId: String? = null
    var merchantMCC: String? = null
    var merchantName: String? = null
    var merchantAddress: MerchantAddress? = null
    var merchantDisplayName: String? = null
    var store: Store? = null
}