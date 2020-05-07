package com.wawrzacz.entertainmentassistant.data.errors

enum class ResponseStatus (val value: String) {
    NOT_INITIALIZED("NOT_INITIALIZED"),
    NO_RESULT("NO_RESULT"),
    IN_PROGRESS("IN_PROGRESS"),
    ERROR("ERROR"),
    SUCCESS("SUCCESS")
}