package com.antgroup.ewallet.model.entity

class User {
    var id: Long? = null
        private set
    var username: String? = null
    var password: String? = null
    var balance: Double? = null

    fun setId(id: Double) {
        this.id = id.toLong()
    }

    companion object {
        var sheet: String = "Users"
    }
}