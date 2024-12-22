package com.antgroup.ewallet.model.entity

class BasePayment {
    var value: String? = null
    var currency: String? = null

    constructor(value: String?, currency: String?) {
        this.value = value
        this.currency = currency
    }

    constructor()
}