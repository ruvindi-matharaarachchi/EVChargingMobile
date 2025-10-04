package com.example.evchargingmobile.ui.common

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Show a simple message first
        android.widget.Toast.makeText(this, "App started successfully!", android.widget.Toast.LENGTH_SHORT).show()
        
        // Add a small delay to see if MainActivity loads
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            try {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Error starting app: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }, 2000) // 2 second delay
    }
}

