package com.wawrzacz.entertainmentassistant.data

class LoggedUser(
    var username: String
) {
    var displayName: String?

    init {
        this.displayName = username.substring(0, 6)
    }
}