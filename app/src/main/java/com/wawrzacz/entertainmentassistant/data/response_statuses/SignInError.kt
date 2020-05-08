package com.wawrzacz.entertainmentassistant.data.response_statuses

enum class SignInError (val value: String){
    INCORRECT_CREDENTIALS("Incorrect credentials"),
    ACCOUNT_NOT_ACTIVATED("Your account has not been activated yet"),
    CANCELLED("Sign-in cancelled"),
    FAILED("Authentication failed"),
    NOT_INITIALIZED("")
}