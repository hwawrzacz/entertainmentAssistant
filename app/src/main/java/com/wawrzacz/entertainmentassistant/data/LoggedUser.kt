package com.wawrzacz.entertainmentassistant.data

class LoggedUser(
    val email: String,
    var displayName: String?
) {
    init {
        if (displayName == null) {
            val indexOfAtSign = email.indexOf("@")
            displayName = email.substring(0, indexOfAtSign)
        }
    }
}