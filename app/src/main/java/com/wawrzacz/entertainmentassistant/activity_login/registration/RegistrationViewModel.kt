package com.wawrzacz.entertainmentassistant.activity_login.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.AuthReposotory
import com.wawrzacz.entertainmentassistant.data.LoginError
import java.util.regex.Pattern

class RegistrationViewModel: ViewModel() {
    private val authRepository = AuthReposotory

    private val _loginError = MutableLiveData<LoginError?>(LoginError.NOT_INITIALIZED)
    val loginError: LiveData<LoginError?> = _loginError

    private val _passwordError = MutableLiveData<LoginError?>(LoginError.NOT_INITIALIZED)
    val passwordError: LiveData<LoginError?> = _passwordError

    private val _repeatPasswordError = MutableLiveData<LoginError?>(LoginError.NOT_INITIALIZED)
    val repeatPasswordError: LiveData<LoginError?> = _repeatPasswordError

    private val _inputsValidity = MutableLiveData<Boolean>(false)
    val inputsValidity: LiveData<Boolean> = _inputsValidity


    //#region Validation
    fun loginChanged(login: String) {
        when {
            login.isBlank() -> {
                this._loginError.value = LoginError.LOGIN_EMPTY
            }
            login.length < 6 -> {
                this._loginError.value = LoginError.LOGIN_TOO_SHORT
            }
            Pattern.matches("[A-z0-9_/-@/.]", login) -> {
                this._loginError.value = LoginError.LOGIN_INVALID_CHARACTERS
            }
            else -> this._loginError.value = null
        }
        validateInputs()
    }

    fun passwordChanged(password: String) {
        when {
            password.isBlank() -> {
                this._passwordError.value = LoginError.PASSWORD_EMPTY
            }
            password.length < 6 -> {
                this._passwordError.value = LoginError.PASSWORD_TOO_SHORT
            }
            else -> this._passwordError.value = null
        }
        validateInputs()
    }

    fun repeatPasswordChanged(password: String, repeatPassword: String) {
        if (password != repeatPassword && !password.isBlank()) {
            this._repeatPasswordError.value = LoginError.PASSWORDS_DONT_MATCH
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

    fun register(username: String, password: String) {
        authRepository.register(username, password)
    }
}