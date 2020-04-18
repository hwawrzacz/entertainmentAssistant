package com.wawrzacz.entertainmentassistant.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

object AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var loggedUser = MutableLiveData<LoggedUser?>(null)

    fun getLoggedUser(): LiveData<LoggedUser?> {
        if (firebaseAuth.currentUser != null) {
            val loggedUser = firebaseAuth.currentUser
            val login = loggedUser!!.email!!
            val displayName = loggedUser!!.displayName!!
            this.loggedUser.value = LoggedUser(login, displayName)
        }

        return loggedUser
    }

    fun signIn(username: String, password: String) {
        if (loggedUser.value != null) {

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
        loggedUser.value = null
        firebaseAuth.signOut()
    }

}