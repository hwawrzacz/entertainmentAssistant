package com.wawrzacz.entertainmentassistant.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wawrzacz.entertainmentassistant.data.AuthReposotory
import com.wawrzacz.entertainmentassistant.data.LoginError
import java.util.regex.Pattern

class LoginViewModel: ViewModel() {
    private val authRepository = AuthReposotory
    private val _loginError = MutableLiveData<LoginError?>(LoginError.NOT_INITIALIZED)
    val loginError: LiveData<LoginError?> = _loginError

    private val _passwordError = MutableLiveData<LoginError?>(LoginError.NOT_INITIALIZED)
    val passwordError: LiveData<LoginError?> = _passwordError

    private val _inputsValidity = MutableLiveData<Boolean>(false)
    val inputsValidity: LiveData<Boolean> = _inputsValidity

    //#region Validation
    fun loginChanged(login: String) {
        if (login.isNullOrBlank()) {
            this._loginError.value = LoginError.LOGIN_EMPTY
        } else if (login.length < 6) {
            this._loginError.value = LoginError.LOGIN_TOO_SHORT
        } else if (Pattern.matches("[A-z0-9_/-@/.]", login)) {
            this._loginError.value = LoginError.LOGIN_INVALID_CHARACTERS
        } else this._loginError.value = null
        validateInputs()
    }

    fun passwordChanged(password: String) {
        if (password.isNullOrBlank()) {
            this._passwordError.value = LoginError.PASSWORD_EMPTY
        } else if (password.length < 6) {
            this._passwordError.value = LoginError.PASSWORD_TOO_SHORT
        } else this._passwordError.value = null
        validateInputs()
    }

    private fun validateInputs() {
        this._inputsValidity.value =
            this._loginError.value == null &&
            this._passwordError.value == null
        Log.i("schab","${this._loginError.value != null && this._passwordError.value != null}")
    }
    //#endregion

    private fun singIn(username: String, password: String) {
        authRepository.signIn(username, password)
    }

}