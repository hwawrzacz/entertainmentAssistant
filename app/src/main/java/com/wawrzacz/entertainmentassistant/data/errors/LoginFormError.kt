package com.wawrzacz.entertainmentassistant.data.errors

enum class LoginFormError(var value: String) {
    LOGIN_TOO_SHORT("Email must contain > 5 digits"),
    LOGIN_EMPTY("Email cannot be empty"),
    LOGIN_INVALID_CHARACTERS("Email contains invalid characters"),
    NO_A_VALID_EMAIL("Provided string does not match an email pattern"),
    PASSWORD_EMPTY("Password cannot be empty"),
    PASSWORD_TOO_SHORT("Password must contain > 5 digits"),
    PASSWORDS_DONT_MATCH("Passwords don't match"),
    NOT_INITIALIZED("")
}
