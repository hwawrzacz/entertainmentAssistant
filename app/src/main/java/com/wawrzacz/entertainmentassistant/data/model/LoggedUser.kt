package com.wawrzacz.entertainmentassistant.data.model

class LoggedUser(
    val email: String,
    var displayName: String?
) {
    init {
        if (displayName.isNullOrBlank()) {
            val indexOfAtSign = email.indexOf("@")
            displayName = email.substring(0, indexOfAtSign)
        }
    }
}