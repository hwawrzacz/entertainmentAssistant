package com.wawrzacz.entertainmentassistant.data

import com.wawrzacz.entertainmentassistant.data.errors.SignInError

class SignInResult (
    val signedInSuccessfully: Boolean,
    val signInError: SignInError?,
    val customMessage: String?
) {
}