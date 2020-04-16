package com.wawrzacz.entertainmentassistant.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.wawrzacz.entertainmentassistant.data.LoginError
import com.wawrzacz.entertainmentassistant.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {

    private val SING_IN_REQUEST_CODE = 1
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var loginWrapper: TextInputLayout
    private lateinit var login: TextInputEditText
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

    override fun onStart() {
        super.onStart()
        checkLoggedUser()
    }

    private fun initializeViewModel() {
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
    }

    private fun initializeBindings() {
        loginWrapper = binding.loginWrapper
        login = binding.login
        passwordWrapper = binding.passwordWrapper
        password = binding.password
        signInButton = binding.buttonSignIn
        signInWithGoogleButton = binding.buttonSignInWithGoogle
        registerButton = binding.buttonRegister
        fragmentTitle = binding.fragmentTitle
    }

    private fun checkLoggedUser() {
        val currentUserFirebase = firebaseAuth.currentUser
        val currentUserGoogle = GoogleSignIn.getLastSignedInAccount(context) //firebaseAuth.currentUse

        if (currentUserFirebase != null) {
            Snackbar.make(
                 requireView(),
                "FUser ${currentUserFirebase.displayName} already signed in",
                Snackbar.LENGTH_LONG
            ).show()
        } else if (currentUserGoogle != null) {
            Snackbar.make(
                requireView(),
                "GUser ${currentUserGoogle.displayName} already signed in",
                Snackbar.LENGTH_LONG
            ).show()
        } else {
            Snackbar.make(requireView(),"No user logged in", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setInputsListeners() {
        login.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                 loginViewModel.loginChanged(login.text.toString())
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
            if (loginError != LoginError.NOT_INITIALIZED)
                loginWrapper.error = loginError?.value
            if (loginError == null)
                loginWrapper.error = null
        })

        loginViewModel.passwordError.observe(viewLifecycleOwner, Observer {
            val passwordError = it
            if (passwordError != LoginError.NOT_INITIALIZED)
                passwordWrapper.error = passwordError?.value
            if (passwordError == null)
                passwordWrapper.error = null
        })

        loginViewModel.inputsValidity.observe(viewLifecycleOwner, Observer {
            val inputsValidity = it
            signInButton.isEnabled = inputsValidity
        })
    }

    private fun setButtonsListeners() {
        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        signInWithGoogleButton.setOnClickListener {
            val googleSignInOptions = createGoogleSignInOptions()

            if (GoogleSignIn.getLastSignedInAccount(context) != null)
                GoogleSignIn.getClient(requireContext(), googleSignInOptions).signOut()

            val googleSignInIntent = GoogleSignIn.getClient(requireContext(), googleSignInOptions).signInIntent
            startActivityForResult(googleSignInIntent, SING_IN_REQUEST_CODE)
        }
    }

    private fun createGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SING_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            task.addOnCompleteListener {
                if (it.isSuccessful) {
                    firebaseAuthWithGoogleAccount(it.result!!)
                } else {
                    handleUnsuccessfullyLoggedInUser()
                }
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                handleSuccessfullyLoggedInUser()
            } else {
                handleUnsuccessfullyLoggedInUser()
            }
        }
    }

    private fun handleSuccessfullyLoggedInUser() {
        Snackbar.make(requireView(), "Hello ${firebaseAuth.currentUser!!.displayName}", Snackbar.LENGTH_LONG).show()
    }

    private fun handleUnsuccessfullyLoggedInUser() {
        Snackbar.make(requireView(), "Authentication failed", Snackbar.LENGTH_LONG).show()
    }
}