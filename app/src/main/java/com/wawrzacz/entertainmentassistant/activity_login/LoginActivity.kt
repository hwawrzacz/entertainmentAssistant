package com.wawrzacz.entertainmentassistant.activity_login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wawrzacz.entertainmentassistant.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}
