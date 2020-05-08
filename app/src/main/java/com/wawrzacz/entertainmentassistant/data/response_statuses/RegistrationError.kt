package com.wawrzacz.entertainmentassistant.data.response_statuses

enum class RegistrationError (val value: String) {
    USER_ALREADY_EXISTS("User already exists"),
    FAILED("User registration failed"),
    CANCELLED("User registration cancelled"),
    NOT_INITIALIZED("")
}