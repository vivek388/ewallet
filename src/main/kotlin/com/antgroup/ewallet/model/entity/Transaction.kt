package com.antgroup.ewallet.model.entity

import java.util.*

class Transaction(
    var id: Double,
    var dateTime: Date,
    var customerId: String?,
    var amount: Double,
    var details: String?,
    var statusCode: String?,
    var statusMessage: String?,
    var status: String?,
    var paymentRequestId: String?,
    var payCurrency: String?,
    var payAmount: String?,
    var payToCurrency: String?,
    var payToAmount: String?,
    var paymentTime: String?,
    var quoteId: String?,
    var quoteCurrencyPair: String?,
    var quotePrice: String?,
    var pspId: String?,
    var acquirerId: String?,
    var promoJson: String?,
    var refundRequestId: String?
) {
    companion object {
        var sheet: String = "Transactions"
    }
}