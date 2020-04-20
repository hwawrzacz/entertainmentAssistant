package com.wawrzacz.entertainmentassistant.activity_main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wawrzacz.entertainmentassistant.activity_login.LoginActivity
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationMenu: BottomNavigationView
    private lateinit var actionBar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBindings()
        setupMenuNavigation()
        setUpActionBar()

        initializeViewModel()
        observeViewModelChanges()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_sign_out) {
            signOut()
            openLoginActivity()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeViewModel() {
        mainActivityViewModel = ViewModelProvider(viewModelStore, MainActivityViewModelFactory())
            .get(MainActivityViewModel::class.java)
    }

    private fun initializeBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        bottomNavigationMenu = binding.bottomNavigationView
    }

    private fun setupMenuNavigation() {
        bottomNavigationMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_movies -> {
                    openMoviesFragment()
                    true
                }
                R.id.nav_books -> {
                    openBooksFragment()
                    true
                }
                R.id.nav_games -> {
                    openGamesFragment()
                    true
                }
                else -> false
            }
        }
    }

    private fun setUpActionBar() {
        actionBar = binding.appToolbar
        setSupportActionBar(actionBar)
        supportActionBar?.title = "Movies"
    }

    private fun observeViewModelChanges() {
        mainActivityViewModel.loggedUser.observe(this, Observer {
            if (it != null) {
                openToastLong("Hello ${it?.displayName}")
            }
        })
    }

    private fun openMoviesFragment() {
        setActionBarTitle("Movies")
        openToastLong("Movies should open")
    }

    private fun openBooksFragment() {
        setActionBarTitle("Books")
        openToastLong("Books should open")
    }

    private fun openGamesFragment() {
        setActionBarTitle("Games")
        openToastLong("Games should open")
    }

    private fun signOut() {
        mainActivityViewModel.signOut()
    }

    private fun openLoginActivity() {
        val loginActivityIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginActivityIntent)
    }

    private fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    private fun openToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
