package com.wawrzacz.entertainmentassistant.data.model

import com.wawrzacz.entertainmentassistant.data.response_statuses.RegistrationError

class RegistrationResult (
    val registeredSuccessfully: Boolean,
    val error: RegistrationError?,
    val customMessage: String?
) {
}