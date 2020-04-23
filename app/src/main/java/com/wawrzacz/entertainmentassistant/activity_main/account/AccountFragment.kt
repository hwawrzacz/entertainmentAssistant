package com.wawrzacz.entertainmentassistant.activity_main.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_login.LoginActivity
import com.wawrzacz.entertainmentassistant.databinding.FragmentAccountBinding


class AccountFragment: MyBottomSheetDialogFragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_account, container, false)

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
        binding.buttonSignOut.setOnClickListener { handleSignOut() }
        binding.buttonChangeEmail.setOnClickListener { handleEmailChange() }
        binding.buttonChangePassword.setOnClickListener { handlePasswordChange() }
        binding.buttonDeleteAccount.setOnClickListener { handleDeleteAccount() }
    }

    private fun observeViewModelChanges() {
        viewModel.loggedUser.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.username.text = it.displayName
            }
        })
    }

    private fun handleSignOut() {
        signOut()
        openLoginActivity()
        requireActivity().finish()
    }

    private fun handleEmailChange() {
        showToastLong("Email change is not available yet")
    }

    private fun handlePasswordChange() {
        showToastLong("Password change is not available yet")
    }

    private fun handleDeleteAccount() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.warning_24)
            .setTitle(R.string.title_delete_account)
            .setMessage(R.string.confirmation_message_delete_account_alert)
            .setPositiveButton(R.string.answer_yes) { _, _ -> run {
                deleteAccount()
            } }
            .setNegativeButton(R.string.answer_no, null)
            .show()
    }

    private fun openLoginActivity() {
        val loginActivityIntent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(loginActivityIntent)
    }

    private fun signOut() {
        viewModel.signOut()
    }

    private fun deleteAccount() {
        viewModel.deleteAccount().observe(viewLifecycleOwner, Observer {
            if (it) {
                openLoginActivity()
                requireActivity().finish()
            }
            else showToastLong("Error while deleting user")
        })
    }

    private fun showToastLong(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
