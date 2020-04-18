package com.wawrzacz.entertainmentassistant.activity_main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.google.firebase.auth.FirebaseAuth
import com.wawrzacz.entertainmentassistant.activity_login.LoginActivity
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.data.LoggedUser
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createToolbar()
        initializeViewModel()
        observeViewModelChanges()

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sign_out) {
            signOut()
            openLoginActivity()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun initializeViewModel() {
        mainActivityViewModel = ViewModelProvider(viewModelStore, MainActivityViewModelFactory())
            .get(MainActivityViewModel::class.java)
    }

    private fun observeViewModelChanges() {
        mainActivityViewModel.loggedUser.observe(this, Observer {
            if (it != null) {
                openToastLong("Hello ${it?.displayName}")
            }
        })
    }

    private fun signOut() {
        mainActivityViewModel.signOut()
    }

    private fun openLoginActivity() {
        val loginActivityIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginActivityIntent)
    }

    private fun openToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
