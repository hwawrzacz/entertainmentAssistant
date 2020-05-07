package com.wawrzacz.entertainmentassistant.activity_login.registration

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.repositories.AuthRepository
import com.wawrzacz.entertainmentassistant.data.model.RegistrationResult
import com.wawrzacz.entertainmentassistant.data.errors.FormValidationState

class RegistrationViewModel: ViewModel() {
    private val authRepository =
        AuthRepository

    private val _loginError = MutableLiveData<FormValidationState?>(
        FormValidationState.NOT_INITIALIZED)
    val loginError: LiveData<FormValidationState?> = _loginError

    private val _passwordError = MutableLiveData<FormValidationState?>(
        FormValidationState.NOT_INITIALIZED)
    val passwordError: LiveData<FormValidationState?> = _passwordError

    private val _repeatPasswordError = MutableLiveData<FormValidationState?>(
        FormValidationState.NOT_INITIALIZED)
    val repeatPasswordError: LiveData<FormValidationState?> = _repeatPasswordError

    private val _inputsValidity = MutableLiveData<Boolean>(false)
    val inputsValidity: LiveData<Boolean> = _inputsValidity


    //#region Validation
    fun loginChanged(login: String) {
        when {
            login.isBlank() -> {
                this._loginError.value = FormValidationState.EMAIL_EMPTY
            }
            !Patterns.EMAIL_ADDRESS.matcher(login).matches() -> {
                this._loginError.value = FormValidationState.NO_A_VALID_EMAIL
            }
            else -> this._loginError.value = null
        }
        validateInputs()
    }

    fun passwordChanged(password: String) {
        when {
            password.isBlank() -> {
                this._passwordError.value = FormValidationState.PASSWORD_EMPTY
            }
            password.length < 6 -> {
                this._passwordError.value = FormValidationState.PASSWORD_TOO_SHORT
            }
            else -> this._passwordError.value = null
        }
        validateInputs()
    }

    fun repeatPasswordChanged(password: String, repeatPassword: String) {
        if (password != repeatPassword && !password.isBlank()) {
            this._repeatPasswordError.value = FormValidationState.PASSWORDS_DONT_MATCH
        } else this._repeatPasswordError.value = null
        validateInputs()
    }

    private fun validateInputs() {
        this._inputsValidity.value =
            this._loginError.value == null &&
            this._passwordError.value == null &&
            this._repeatPasswordError.value == null
    }
    //#endregion

    fun register(username: String, password: String): LiveData<RegistrationResult> {
        return Transformations.map(authRepository.register(username, password)) {
            it
        }
    }
}