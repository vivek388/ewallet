package com.antgroup.ewallet.model.response

import com.antgroup.ewallet.model.entity.*
import java.util.*

class UserInitiatedPayResponse {
    var result: BaseResult? = null
    var acquirerId: String? = null
    var pspId: String? = null
    var codeType: String? = null
    var paymentRequestId: String? = null
    var order: Order? = null
    var paymentAmount: BasePayment? = null
    var payToAmount: BasePayment? = null
    var paymentFactor: PaymentFactor? = null
    var paymentQuote: PaymentQuote? = null
    var paymentPromoInfo: PaymentPromoInfo? = null
    var actionForm: ActionForm? = null
    var paymentExpiryTime: Date? = null
    var paymentRedirectUrl: String? = null
}