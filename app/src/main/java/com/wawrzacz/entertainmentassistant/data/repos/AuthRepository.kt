package com.wawrzacz.entertainmentassistant.data.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.wawrzacz.entertainmentassistant.data.model.LoggedUser
import com.wawrzacz.entertainmentassistant.data.model.RegistrationResult
import com.wawrzacz.entertainmentassistant.data.model.SignInResult
import com.wawrzacz.entertainmentassistant.data.errors.RegistrationError
import com.wawrzacz.entertainmentassistant.data.errors.SignInError

object AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun getLoggedUser(): LiveData<LoggedUser?> {
        val loggedUserLiveData = MutableLiveData<LoggedUser?>(null)

        if (firebaseAuth.currentUser != null) {
            val loggedUser = firebaseAuth.currentUser
            val login = loggedUser!!.email!!
            val displayName = loggedUser.displayName
            loggedUserLiveData.value =
                LoggedUser(
                    login,
                    displayName
                )
        }

        return loggedUserLiveData
    }

    fun signIn(username: String, password: String): LiveData<SignInResult> {
        val signInResult = MutableLiveData<SignInResult>(
            SignInResult(
                false,
                SignInError.NOT_INITIALIZED,
                null
            )
        )

        firebaseAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        signInResult.value =
                            SignInResult(
                                true,
                                null,
                                null
                            )
                    }
                    it.isCanceled -> {
                        signInResult.value =
                            SignInResult(
                                false,
                                SignInError.CANCELLED,
                                null
                            )
                    }
                    it.isComplete -> {
                        signInResult.value =
                            SignInResult(
                                false,
                                null,
                                it.exception?.message
                            )
                        Log.i("schab", "sign in failed with exception: ${it.exception?.message}")
                    }
                }
            }

        return signInResult
    }

    fun register(username: String, password: String): LiveData<RegistrationResult> {
        val registrationResult = MutableLiveData<RegistrationResult>(
            RegistrationResult(
                false,
                RegistrationError.NOT_INITIALIZED,
                null
            )
        )
        firebaseAuth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
//                        userCreatedSuccessfully()
                        registrationResult.value =
                            RegistrationResult(
                                true,
                                null,
                                null
                            )
                    }
                    it.isCanceled -> {
                        registrationResult.value =
                            RegistrationResult(
                                true,
                                RegistrationError.CANCELLED,
                                null
                            )
                    }
                    it.isComplete -> {
                        registrationResult.value =
                            RegistrationResult(
                                false,
                                null,
                                it.exception?.message
                            )
                        Log.i("schab", "Registration competed with exception: ${it.exception?.message}")
                    }
                }
            }

        return registrationResult
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun deleteAccount(): LiveData<Boolean> {
        val userDeleteResult = MutableLiveData<Boolean>()

        firebaseAuth.currentUser?.delete()?.addOnCompleteListener {
            userDeleteResult.value = it.isSuccessful
        }
//        val number = Math.random()
//        userDeleteResult.value = number > 0.5

        return userDeleteResult
    }

}