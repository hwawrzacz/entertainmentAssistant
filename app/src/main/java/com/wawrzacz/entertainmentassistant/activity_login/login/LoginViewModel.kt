package com.wawrzacz.entertainmentassistant.activity_login.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.AuthRepository
import com.wawrzacz.entertainmentassistant.data.LoggedUser
import com.wawrzacz.entertainmentassistant.data.LoginError
import java.util.regex.Pattern

class LoginViewModel: ViewModel() {
    private val authRepository = AuthRepository

    private val _loginError = MutableLiveData<LoginError?>(LoginError.NOT_INITIALIZED)
    val loginError: LiveData<LoginError?> = _loginError

    private val _passwordError = MutableLiveData<LoginError?>(LoginError.NOT_INITIALIZED)
    val passwordError: LiveData<LoginError?> = _passwordError

    private val _inputsValidity = MutableLiveData<Boolean>(false)
    val inputsValidity: LiveData<Boolean> = _inputsValidity

    val loggedUser: LiveData<LoggedUser?> = Transformations.map(authRepository.getLoggedUser()) {
        it
    }

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

    private fun validateInputs() {
        this._inputsValidity.value =
            this._loginError.value == null &&
            this._passwordError.value == null
    }
    //#endregion

    private fun singIn(username: String, password: String) {
        authRepository.signIn(username, password)
    }


}