package com.wawrzacz.entertainmentassistant.data.response_statuses

enum class ResponseStatus (val value: String) {
    NOT_INITIALIZED("NOT_INITIALIZED"),
    IN_PROGRESS("IN_PROGRESS"),
    SUCCESS("SUCCESS"),
    CANCELLED("CANCELLED"),
    NO_RESULT("NO_RESULT"),
    ERROR("ERROR"),
}