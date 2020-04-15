package com.wawrzacz.entertainmentassistant.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var usernameWrapper: TextInputLayout
    private lateinit var username: TextInputEditText
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

        initializeBindings()
        setButtonsListeners()

        return binding.root
    }

    private fun initializeBindings() {
        usernameWrapper = binding.loginWrapper
        username = binding.login
        passwordWrapper = binding.passwordWrapper
        password = binding.password
        signInButton = binding.buttonSignIn
        signInWithGoogleButton = binding.buttonSignInWithGoogle
        registerButton = binding.buttonRegister
        fragmentTitle = binding.fragmentTitle
    }

    private fun setButtonsListeners() {
        registerButton.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }
}