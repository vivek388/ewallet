package com.antgroup.ewallet.model.entity

class BaseResult {
    var resultCode: String? = null
    var resultMessage: String? = null
    var resultStatus: String? = null

    constructor(resultCode: String?, resultMessage: String?, resultStatus: String?) {
        this.resultCode = resultCode
        this.resultMessage = resultMessage
        this.resultStatus = resultStatus
    }

    constructor()

    constructor(isSuccess: Boolean) {
        this.resultCode = "SUCCESS"
        this.resultMessage = "Success"
        this.resultStatus = "S"
    }
}