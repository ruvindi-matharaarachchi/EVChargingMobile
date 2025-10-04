package com.example.evchargingmobile.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.auth.AuthActivity

class LoginActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Redirect to new AuthActivity
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}

