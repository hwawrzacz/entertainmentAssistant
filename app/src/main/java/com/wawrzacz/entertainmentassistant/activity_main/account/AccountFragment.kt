package com.wawrzacz.entertainmentassistant.activity_main.account

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_login.LoginActivity
import com.wawrzacz.entertainmentassistant.databinding.FragmentAccountBinding


class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_account, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, AccountViewModelFactory())
            .get(AccountViewModel::class.java)

        observeViewModelChanges()
        addButtonsListeners()
    }

    private fun addButtonsListeners() {
        binding.buttonSignOut.setOnClickListener {
            signOut()
            openLoginActivity()
            requireActivity().finish()
        }
        binding.buttonChangePassword.setOnClickListener { showToastLong("Change password mock") }
        binding.buttonDeleteAccount.setOnClickListener { showToastLong("Delete account mock") }
    }

    private fun observeViewModelChanges() {
        viewModel.loggedUser.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.username.text = it.displayName
            }
        })
    }



    private fun openLoginActivity() {
        val loginActivityIntent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(loginActivityIntent)
    }

    private fun signOut() {
        viewModel.signOut()
    }

    private fun showToastLong(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
