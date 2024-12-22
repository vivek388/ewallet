package com.antgroup.ewallet.model.request

import com.antgroup.ewallet.model.entity.BasePayment
import com.antgroup.ewallet.model.entity.BaseResult

class NotifyPaymentRequest(
    var paymentResult: BaseResult?, var paymentRequestId: String?, var paymentId: String, var paymentTime: String,
    var paymentAmount: BasePayment?, var payToAmount: BasePayment?, var customerId: String?
) 