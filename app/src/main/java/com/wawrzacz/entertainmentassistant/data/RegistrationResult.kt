package com.wawrzacz.entertainmentassistant.data

import com.wawrzacz.entertainmentassistant.data.errors.RegistrationError

class RegistrationResult (
    val registeredSuccessfully: Boolean,
    val error: RegistrationError?,
    val customMessage: String?
) {
}