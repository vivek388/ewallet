package com.antgroup.ewallet.model.entity

class ApiResult {
    var result: BaseResult? = null

    constructor(result: BaseResult?) {
        this.result = result
    }

    constructor()
}