package com.wawrzacz.entertainmentassistant.activity_main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationMenu: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var actionBar: Toolbar
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        initializeBindings()
        initializeActionBar()
        initializeBottomSheetBehavior()
    }

    override fun onStart() {
        super.onStart()
        setupMenuNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_open_account_panel) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        bottomNavigationMenu = binding.bottomNavigationView
    }

    private fun setupMenuNavigation() {
        navController = findNavController(R.id.main_fragment_container)
        NavigationUI.setupWithNavController(bottomNavigationMenu, navController)
    }

    private fun initializeActionBar() {
        actionBar = binding.appToolbar
        setSupportActionBar(actionBar)
    }

    private fun initializeBottomSheetBehavior() {
        val fragment = supportFragmentManager.findFragmentById(R.id.backdrop_fragment)

        fragment?.let {
            BottomSheetBehavior.from(it.requireView()).let { bottomSheetBehavior ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                this.bottomSheetBehavior = bottomSheetBehavior
            }
        }
    }

    fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun setActionBarIcon(resource: Int) {
        supportActionBar?.setIcon(resource)
    }

    private fun openToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        else {
            super.onBackPressed()
        }
    }
}
