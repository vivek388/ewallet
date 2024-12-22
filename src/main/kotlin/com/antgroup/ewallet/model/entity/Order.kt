package com.antgroup.ewallet.model.entity

class Order {
    var referenceOrderId: String? = null
    var orderDescription: String? = null
    var orderAmount: OrderAmount? = null
    var merchant: Merchant? = null
}