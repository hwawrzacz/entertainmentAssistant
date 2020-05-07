package com.wawrzacz.entertainmentassistant.activity_main

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.account.AccountFragment
import com.wawrzacz.entertainmentassistant.activity_main.movies.movie_creation.MovieCreationFragment
import com.wawrzacz.entertainmentassistant.data.enums.MediaCategory
import com.wawrzacz.entertainmentassistant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val accountFragment = AccountFragment()
    private var searchView: SearchView? = null
    private var currentCategory = MediaCategory.BROWSE

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
        binding.fabOpenCreationFragment.setOnClickListener {
            openCreationDialog()
        }
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

    fun clearFocusFromSearchView() {
        searchView?.clearFocus()
    }

    fun changeCurrentCategory(category: MediaCategory) {
        when (category) {
            MediaCategory.BROWSE -> binding.fabOpenCreationFragment.hide()
            else -> binding.fabOpenCreationFragment.show()
        }
        currentCategory = category
    }

    private fun openCreationDialog() {
        when (currentCategory) {
            MediaCategory.BROWSE -> {}
            MediaCategory.MOVIES -> openMovieCreationFragment()
            MediaCategory.SERIES -> openSeriesCreationFragment()
            MediaCategory.GAMES -> openGameCreationFragment()
        }
    }

    private fun openMovieCreationFragment() {
        val fragmentManager = supportFragmentManager
        val fragment = MovieCreationFragment()
        fragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(android.R.id.content, fragment, "CREATION_FRAGMENT")
        }.commit()
    }

    private fun openSeriesCreationFragment() {
        openSnackbarShort("Create new series")
    }

    private fun openGameCreationFragment() {
        openSnackbarShort("Create new game")
    }

    private fun openSnackbarShort(message: String) {
        Snackbar.make(binding.bottomNavigationView, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        // Handle details DialogFragment close
        var dialogFragmentClosed = false
        for (fragmentTag in listOf("DETAILS_FRAGMENT", "CREATION_FRAGMENT")) {
            val fragment = supportFragmentManager.findFragmentByTag(fragmentTag) as? DialogFragment
            if (fragment != null && (fragment.isInLayout || fragment.isAdded || fragment.isVisible)) {
                dialogFragmentClosed = true
                fragment.dismiss()
                initializeActionBar()
            }
        }
        if (!dialogFragmentClosed)  super.onBackPressed()
    }

    override fun onDestroy() {
        viewModelStore.clear()
        super.onDestroy()
    }
}
