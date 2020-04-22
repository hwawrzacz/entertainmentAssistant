package com.wawrzacz.entertainmentassistant.activity_main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.AuthRepository
import com.wawrzacz.entertainmentassistant.data.LoggedUser

class AccountViewModel : ViewModel() {
    private val authRepository = AuthRepository

    val loggedUser: LiveData<LoggedUser?> = Transformations.map(authRepository.getLoggedUser()) {
        it
    }

    fun signOut() {
        authRepository.signOut()
    }
}
