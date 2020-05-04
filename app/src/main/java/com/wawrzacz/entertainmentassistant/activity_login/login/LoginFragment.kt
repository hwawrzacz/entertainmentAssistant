package com.wawrzacz.entertainmentassistant.activity_login.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.data.model.SignInResult
import com.wawrzacz.entertainmentassistant.data.errors.LoginFormError
import com.wawrzacz.entertainmentassistant.data.errors.SignInError
import com.wawrzacz.entertainmentassistant.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {

    private object Codes {
        const val SING_IN_REQUEST_CODE = 1
        const val SIGN_IN_CANCELLED_ERROR_CODE = "12501"
        const val SIGN_IN_NO_INTERNET_ERROR_CODE = "7"
    }

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var emailWrapper: TextInputLayout
    private lateinit var email: TextInputEditText
    private lateinit var passwordWrapper: TextInputLayout
    private lateinit var password: TextInputEditText
    private lateinit var signInButton: Button
    private lateinit var signInWithGoogleButton: Button
    private lateinit var registerButton: Button

    private lateinit var fragmentTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_login, container, false)

        initializeViewModel()
        initializeBindings()

        setInputsListeners()
        observeViewModelChanges()
        setButtonsListeners()

        return binding.root
    }

    private fun initializeViewModel() {
        loginViewModel = ViewModelProvider(requireActivity().viewModelStore, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
    }

    private fun initializeBindings() {
        emailWrapper = binding.emailWrapper
        email = binding.email
        passwordWrapper = binding.passwordWrapper
        password = binding.password
        signInButton = binding.buttonSignIn
        signInWithGoogleButton = binding.buttonSignInWithGoogle
        registerButton = binding.buttonRegister
        fragmentTitle = binding.fragmentTitle
    }

    private fun setInputsListeners() {
        email.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                 loginViewModel.loginChanged(email.text.toString())
            }
        })

        password.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                 loginViewModel.passwordChanged(password.text.toString())
            }
        })
    }

    private fun observeViewModelChanges() {
        loginViewModel.loginError.observe(viewLifecycleOwner, Observer {
            val loginError = it
            if (loginError != LoginFormError.NOT_INITIALIZED)
                emailWrapper.error = loginError?.value
            if (loginError == null)
                emailWrapper.error = null
        })

        loginViewModel.passwordError.observe(viewLifecycleOwner, Observer {
            val passwordError = it
            if (passwordError != LoginFormError.NOT_INITIALIZED)
                passwordWrapper.error = passwordError?.value
            if (passwordError == null)
                passwordWrapper.error = null
        })

        loginViewModel.inputsValidity.observe(viewLifecycleOwner, Observer {
            val inputsValidity = it
            signInButton.isEnabled = inputsValidity
        })

        loginViewModel.loggedUser.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                handleSuccessfullyLoggedInUser()
            }
        })
    }

    private fun setButtonsListeners() {
        binding.buttonSignIn.setOnClickListener {
            startSignInAnimation()
            loginViewModel.signIn(email.text.toString(), password.text.toString())
                .observe(viewLifecycleOwner, Observer{
                    handleSignInResponse(it)
                })
        }

        binding.buttonSignInWithGoogle.setOnClickListener {
            val googleSignInOptions = createGoogleSignInOptions()

            if (GoogleSignIn.getLastSignedInAccount(context) != null)
                GoogleSignIn.getClient(requireContext(), googleSignInOptions).signOut()

            val googleSignInIntent = GoogleSignIn.getClient(requireContext(), googleSignInOptions).signInIntent
            startActivityForResult(googleSignInIntent,
                Codes.SING_IN_REQUEST_CODE
            )
        }

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    private fun handleSignInResponse(response: SignInResult) {
        when {
            response.signedInSuccessfully -> {
                handleSuccessfullyLoggedInUser()
            }
            response.customMessage != null -> {
                handleUnsuccessfullyLoggedInUser(response.customMessage)
            }
            response.signInError != SignInError.NOT_INITIALIZED -> {
                handleUnsuccessfullyLoggedInUser(response.signInError.toString())
            }
        }
    }

    private fun createGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Codes.SING_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            task.addOnCompleteListener {
                if (it.isSuccessful) {
                    firebaseAuthenticateWithGoogleAccount(it.result!!)
                    startSignInAnimation()
                }
                else {
                    when (val errorCode = getErrorFirebaseErrorCodeFromString(it.exception?.message)) {
                        Codes.SIGN_IN_CANCELLED_ERROR_CODE -> {
                            openSnackBarLong(getString(R.string.message_sing_in_with_google_cancelled))
                        }
                        Codes.SIGN_IN_NO_INTERNET_ERROR_CODE -> {
                            openSnackBarLong(getString(R.string.message_no_internet_access))
                        }
                        else -> handleUnsuccessfullyLoggedInUser(errorCode.toString())
                    }
                }
            }
        }
    }

    private fun firebaseAuthenticateWithGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                handleSuccessfullyLoggedInUser()
            } else {
                handleUnsuccessfullyLoggedInUser(it.exception?.message)
            }
        }
    }

    private fun handleSuccessfullyLoggedInUser() {
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        startActivity(mainActivityIntent)
        loginViewModel.loggedUser.removeObservers(viewLifecycleOwner)
        removeViewModelObservers()
        requireActivity().finish()
    }

    private fun getErrorFirebaseErrorCodeFromString(value: String?): String? {
        Log.i("schabik", value?.replace(Regex("[^0-9]"), ""))
        return value?.replace(Regex("[^0-9]"), "")
    }

    private fun removeViewModelObservers() {
        loginViewModel.loginError.removeObservers(viewLifecycleOwner)
        loginViewModel.passwordError.removeObservers(viewLifecycleOwner)
        loginViewModel.inputsValidity.removeObservers(viewLifecycleOwner)
        loginViewModel.loggedUser.removeObservers(viewLifecycleOwner)
    }

    private fun handleUnsuccessfullyLoggedInUser(error: String?) {
        stopSignInAnimation()
        openSnackBarLong(error.toString())
    }


    private fun startSignInAnimation() {
        showProgressBar()
        disableAllButtons()
    }

    private fun stopSignInAnimation() {
        hideProgressBar()
        enableAllButtons()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun enableAllButtons() {
        binding.buttonSignIn.isEnabled = true
        binding.buttonSignInWithGoogle.isEnabled = true
        binding.buttonRegister.isEnabled = true
    }

    private fun disableAllButtons() {
        binding.buttonSignIn.isEnabled = false
        binding.buttonSignInWithGoogle.isEnabled = false
        binding.buttonRegister.isEnabled = false
    }

    private fun openSnackBarLong(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}