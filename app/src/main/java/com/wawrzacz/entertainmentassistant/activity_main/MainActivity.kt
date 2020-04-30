package com.wawrzacz.entertainmentassistant.activity_main

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.account.AccountFragment
import com.wawrzacz.entertainmentassistant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val accountFragment = AccountFragment()
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        initializeBindings()
        initializeActionBar()
    }

    override fun onStart() {
        super.onStart()
        setupBottomNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)

        val accountMenuItem = menu.findItem(R.id.nav_open_account_panel)
        val searchItem = menu.findItem(R.id.nav_search)
        searchView = searchItem?.actionView as SearchView?

        searchView?.setOnSearchClickListener { accountMenuItem.isVisible = false }

        searchView?.setOnCloseListener {
            accountMenuItem.isVisible = true
            false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_open_account_panel) {
            expandAccountFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private fun setupBottomNavigation() {
        navController = findNavController(R.id.main_fragment_container)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

    fun initializeActionBar() {
        setSupportActionBar(binding.appToolbar)
    }

    fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun setActionBarIcon(resource: Int) {
        supportActionBar?.setIcon(resource)
    }
    
    private fun expandAccountFragment(){
        if (!accountFragment.isAdded) accountFragment.show(supportFragmentManager, accountFragment.tag)
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    fun cleanToolbar() {
        binding.appToolbar.clearFocus()
    }

    fun hideBottomNavbar() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    fun showBottomNavbar() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    fun clearFocusFromSearchView() {
        searchView?.clearFocus()
        initializeActionBar()
    }
}
