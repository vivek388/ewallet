package com.antgroup.ewallet.model.request

import com.antgroup.ewallet.model.entity.BasePayment
import com.antgroup.ewallet.model.entity.PaymentPromoInfo
import com.antgroup.ewallet.model.entity.PaymentQuote
import com.antgroup.ewallet.model.entity.SurchargeInfo

class RefundRequest(
    var acquirerId: String, var paymentId: String, var paymentRequestId: String, var pspId: String,
    var refundAmount: BasePayment, var refundFromAmount: BasePayment, var refundPromoInfo: PaymentPromoInfo,
    var refundQuote: PaymentQuote, var refundRequestId: String, var extendInfo: String, var surchargeInfo: SurchargeInfo
) 