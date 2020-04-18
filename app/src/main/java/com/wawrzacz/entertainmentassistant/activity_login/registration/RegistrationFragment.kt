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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.data.LoginError
import com.wawrzacz.entertainmentassistant.databinding.FragmentRegistrationBinding

class RegistrationFragment: Fragment() {

    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var loginWrapper: TextInputLayout
    private lateinit var login: TextInputEditText
    private lateinit var passwordWrapper: TextInputLayout
    private lateinit var password: TextInputEditText
    private lateinit var repeatPasswordWrapper: TextInputLayout
    private lateinit var repeatPassword: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var cancelButton: Button

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
        loginWrapper = binding.loginWrapper
        login = binding.login
        passwordWrapper = binding.passwordWrapper
        password = binding.password
        repeatPasswordWrapper = binding.repeatPasswordWrapper
        repeatPassword = binding.repeatPassword
        fragmentTitle = binding.fragmentTitle
        cancelButton = binding.buttonCancel
    }

    private fun setInputsListeners() {
        login.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                registrationViewModel.loginChanged(login.text.toString())
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
            if (loginError != LoginError.NOT_INITIALIZED)
                loginWrapper.error = loginError?.value
            if (loginError == null)
                loginWrapper.error = null
        })

        registrationViewModel.passwordError.observe(viewLifecycleOwner, Observer {
            val passwordError = it
            if (passwordError != LoginError.NOT_INITIALIZED)
                passwordWrapper.error = passwordError?.value
            if (passwordError == null)
                passwordWrapper.error = null
        })

        registrationViewModel.repeatPasswordError.observe(viewLifecycleOwner, Observer {
            val repeatPasswordError = it
            if (repeatPasswordError != LoginError.NOT_INITIALIZED)
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
        cancelButton.setOnClickListener{
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
    }
}