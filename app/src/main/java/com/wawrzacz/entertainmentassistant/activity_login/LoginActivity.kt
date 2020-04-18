package com.wawrzacz.entertainmentassistant.activity_login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wawrzacz.entertainmentassistant.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeActionBar()
        hideActionBar()
    }

    private fun initializeActionBar() {
        val actionBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.app_toolbar)
        setSupportActionBar(actionBar)
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
    }
}
