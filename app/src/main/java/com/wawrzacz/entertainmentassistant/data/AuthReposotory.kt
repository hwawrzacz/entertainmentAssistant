package com.wawrzacz.entertainmentassistant.data

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

object AuthReposotory {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var _loggedUser = MutableLiveData<LoggedUser?>(null)
    private var loggedUser: LiveData<LoggedUser?> = _loggedUser

    init {
        if (firebaseAuth.currentUser != null) {
            val loggedUser = firebaseAuth.currentUser
            val email = loggedUser!!.email!!
            this._loggedUser.value = LoggedUser(email)
        }
    }

    fun signIn(username: String, password: String) {
        if (_loggedUser.value != null) {

        firebaseAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {

                    }
                    it.isComplete -> {

                    }
                    it.isCanceled -> {

                    }
                }
            }
        }
    }

    fun register(username: String, password: String) {

    }

    fun signOut() {
        _loggedUser.value = null
        firebaseAuth.signOut()
    }

}