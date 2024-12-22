package com.antgroup.ewallet.model.response

import com.antgroup.ewallet.model.entity.BaseResult


class IdentifyCodeResponse {
    var acDecodeConfig: String? = null
    var codeValue: String? = null
    var redirectUrl: String? = null
    var isSupported: Boolean = false
    var postCodeMatchActionType: String? = null
    var result: BaseResult? = null
    var userAgent: String? = null
}