package com.wawrzacz.entertainmentassistant.activity_login.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.repositories.AuthRepository
import com.wawrzacz.entertainmentassistant.data.model.LoggedUser
import com.wawrzacz.entertainmentassistant.data.model.SignInResult
import com.wawrzacz.entertainmentassistant.data.errors.FormValidationState

class LoginViewModel: ViewModel() {
    private val authRepository =
        AuthRepository

    private val _loginError = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val loginError: LiveData<FormValidationState?> = _loginError

    private val _passwordError = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val passwordError: LiveData<FormValidationState?> = _passwordError

    private val _inputsValidity = MutableLiveData(false)
    val inputsValidity: LiveData<Boolean> = _inputsValidity

    val loggedUser: LiveData<LoggedUser?> = Transformations.map(authRepository.getLoggedUser()) {
        it
    }

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

    private fun validateInputs() {
        this._inputsValidity.value =
            this._loginError.value == null &&
            this._passwordError.value == null
    }
    //#endregion

    fun signIn(username: String, password: String): LiveData<SignInResult> {
        return Transformations.map(authRepository.signIn(username, password)) {
            it
        }
    }


}