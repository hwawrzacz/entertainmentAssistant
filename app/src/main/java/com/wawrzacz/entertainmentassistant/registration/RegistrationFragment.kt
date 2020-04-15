package com.wawrzacz.entertainmentassistant.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.databinding.FragmentRegistrationBinding

class RegistrationFragment: Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var usernameWrapper: TextInputLayout
    private lateinit var username: TextInputEditText
    private lateinit var passwordWrapper: TextInputLayout
    private lateinit var password: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var cancelButton: Button

    private lateinit var fragmentTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_registration, container, false)

        initializeBindings()
        setButtonsListeners()

        return binding.root
    }

    private fun initializeBindings() {
        registerButton = binding.buttonRegister
        usernameWrapper = binding.loginWrapper
        username = binding.login
        passwordWrapper = binding.passwordWrapper
        password = binding.password
        fragmentTitle = binding.fragmentTitle
        cancelButton = binding.buttonCancel
    }

    private fun setButtonsListeners() {
        cancelButton.setOnClickListener{
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
    }
}