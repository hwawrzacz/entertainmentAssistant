package com.wawrzacz.entertainmentassistant.activity_login.registration

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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.data.model.RegistrationResult
import com.wawrzacz.entertainmentassistant.data.response_statuses.FormValidationState
import com.wawrzacz.entertainmentassistant.data.response_statuses.RegistrationError
import com.wawrzacz.entertainmentassistant.databinding.FragmentRegistrationBinding

class RegistrationFragment: Fragment() {

    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var emailWrapper: TextInputLayout
    private lateinit var email: TextInputEditText
    private lateinit var passwordWrapper: TextInputLayout
    private lateinit var password: TextInputEditText
    private lateinit var repeatPasswordWrapper: TextInputLayout
    private lateinit var repeatPassword: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var backButton: Button

    private lateinit var fragmentTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_registration, container, false)

        initializeViewModel()
        initializeBindings()

        setInputsListeners()
        observeViewModelChanges()
        setButtonsListeners()

        return binding.root
    }

    private fun initializeViewModel() {
        registrationViewModel = ViewModelProvider(viewModelStore, RegistrationViewModelFactory())
            .get(RegistrationViewModel::class.java)
    }

    private fun initializeBindings() {
        registerButton = binding.buttonRegister
        emailWrapper = binding.emailWrapper
        email = binding.email
        passwordWrapper = binding.passwordWrapper
        password = binding.password
        repeatPasswordWrapper = binding.repeatPasswordWrapper
        repeatPassword = binding.repeatPassword
        fragmentTitle = binding.fragmentTitle
        backButton = binding.buttonBack
    }

    private fun setInputsListeners() {
        email.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                registrationViewModel.loginChanged(email.text.toString())
            }
        })

        password.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                registrationViewModel.passwordChanged(password.text.toString())
            }
        })

        repeatPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                registrationViewModel.repeatPasswordChanged(password.text.toString(), repeatPassword.text.toString())
            }
        })
    }

    private fun observeViewModelChanges() {
        registrationViewModel.loginError.observe(viewLifecycleOwner, Observer {
            val loginError = it
            if (loginError != FormValidationState.NOT_INITIALIZED)
                emailWrapper.error = loginError?.value
            if (loginError == null)
                emailWrapper.error = null
        })

        registrationViewModel.passwordError.observe(viewLifecycleOwner, Observer {
            val passwordError = it
            if (passwordError != FormValidationState.NOT_INITIALIZED)
                passwordWrapper.error = passwordError?.value
            if (passwordError == null)
                passwordWrapper.error = null
        })

        registrationViewModel.repeatPasswordError.observe(viewLifecycleOwner, Observer {
            val repeatPasswordError = it
            if (repeatPasswordError != FormValidationState.NOT_INITIALIZED)
                repeatPasswordWrapper.error = repeatPasswordError?.value
            if (repeatPasswordError == null)
                repeatPasswordWrapper.error = null
        })

        registrationViewModel.inputsValidity.observe(viewLifecycleOwner, Observer {
            val inputsValidity = it
            registerButton.isEnabled = inputsValidity
        })
    }

    private fun setButtonsListeners() {
        backButton.setOnClickListener{
            findNavController().popBackStack()
        }

        registerButton.setOnClickListener {
            registrationViewModel.register(email.text.toString(), password.text.toString())
                .observe(viewLifecycleOwner, Observer {
                    handleRegisterResponse(it)
                })
        }
    }

    private fun handleRegisterResponse(response: RegistrationResult) {
        when {
            response.registeredSuccessfully -> {
                openSnackBarLong(getString(R.string.message_registered_successfully))
            }
            response.customMessage != null -> {
                openSnackBarLong(response.customMessage)
            }
            response.error != RegistrationError.NOT_INITIALIZED -> {
                openSnackBarLong(response.error.toString())
            }
        }
    }

    private fun openSnackBarLong(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}