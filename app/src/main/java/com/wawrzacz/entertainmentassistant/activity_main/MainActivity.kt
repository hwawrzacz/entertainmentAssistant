package com.wawrzacz.entertainmentassistant.activity_main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wawrzacz.entertainmentassistant.activity_login.LoginActivity
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.account.AccountFragment
import com.wawrzacz.entertainmentassistant.activity_main.books.BooksFragment
import com.wawrzacz.entertainmentassistant.activity_main.games.GamesFragment
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesFragment
import com.wawrzacz.entertainmentassistant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationMenu: BottomNavigationView
    private lateinit var actionBar: Toolbar
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBindings()
        setupMenuNavigation()
        setUpActionBar()

//        initializeBottomSheetFragment()
        initializeBottomSheetBehavior()

        initializeViewModel()
        observeViewModelChanges()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_open_account_panel) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            signOut()
//            openLoginActivity()
//            finish()
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
//        bottomNavigationMenu = findViewById(R.id.bottom_navigation_view)
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
        setActionBarTitle(getString(R.string.label_movies))
        setActionBarIcon(R.drawable.movies_rounded)
    }

    private fun initializeBottomSheetFragment() {
        val accountFragment = AccountFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.backdrop_fragment, accountFragment)
            .commit()
    }

    private fun initializeBottomSheetBehavior() {
        val fragment = supportFragmentManager.findFragmentById(R.id.backdrop_fragment)

        Log.i("schab", "Initialize: ${fragment?.view}")

        fragment?.let {
            Log.i("schab", "Fragment success")
            BottomSheetBehavior.from(it.requireView()).let { bottomSheetBehavior ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                this.bottomSheetBehavior = bottomSheetBehavior
            }
        }
    }

    private fun observeViewModelChanges() {
        mainActivityViewModel.loggedUser.observe(this, Observer {
            if (it != null) {
                openToastLong("Hello ${it.displayName}")
            }
        })
    }

    //#region Navigation
    private fun openMoviesFragment() {
        setActionBarTitle(getString(R.string.label_movies))
        setActionBarIcon(R.drawable.movies_rounded)
        val moviesFragment = MoviesFragment()
        replaceFragment(moviesFragment)
    }

    private fun openBooksFragment() {
        setActionBarTitle(getString(R.string.label_books))
        setActionBarIcon(R.drawable.books_rounded)
        val booksFragment = BooksFragment()
        replaceFragment(booksFragment)
    }

    private fun openGamesFragment() {
        setActionBarTitle(getString(R.string.label_games))
        setActionBarIcon(R.drawable.games_rounded)
        val gamesFragment = GamesFragment()
        replaceFragment(gamesFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()
    }

    private fun openLoginActivity() {
        val loginActivityIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginActivityIntent)
    }
    //#endregion

    private fun signOut() {
        mainActivityViewModel.signOut()
    }

    private fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    private fun setActionBarIcon(resource: Int) {
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
