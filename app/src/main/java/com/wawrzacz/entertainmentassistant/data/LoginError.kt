package com.wawrzacz.entertainmentassistant.data

enum class LoginError(var value: String) {
    LOGIN_TOO_SHORT("Login must contain > 5 digits"),
    LOGIN_EMPTY("Login cannot be empty"),
    LOGIN_INVALID_CHARACTERS("Login contains invalid characters"),
    PASSWORD_EMPTY("Password cannot be empty"),
    PASSWORD_TOO_SHORT("Password must contain > 5 digits"),
    NOT_INITIALIZED("")
}
