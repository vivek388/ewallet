package com.antgroup.ewallet.model.response

import com.antgroup.ewallet.model.entity.BasePayment
import com.antgroup.ewallet.model.entity.BaseResult

class InquiryPaymentResponse(
    var customerId: String?, var payToAmount: BasePayment?, var paymentAmount: BasePayment?,
    var paymentId: String?, var paymentResult: BaseResult?, var paymentTime: String?, var result: BaseResult
)
