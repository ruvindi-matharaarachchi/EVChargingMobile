package com.example.evchargingmobile.operator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.auth.AuthActivity
import com.example.evchargingmobile.auth.SessionStore

class OperatorHomeActivity : AppCompatActivity() {
    
    private lateinit var sessionStore: SessionStore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator_home)
        
        sessionStore = SessionStore(this)
        
        // Check authentication
        if (!sessionStore.isLoggedIn()) {
            navigateToAuth()
            return
        }
        
        setupUI()
    }
    
    private fun setupUI() {
        // Logout button
        findViewById<android.widget.Button>(R.id.btnLogout).setOnClickListener {
            sessionStore.clear()
            navigateToAuth()
        }
        
        // Placeholder for future operator functionality
        findViewById<android.widget.TextView>(R.id.tvPlaceholder).text = 
            "Operator functionality will be implemented by other team members.\n\n" +
            "This screen will include:\n" +
            "- QR code scanning\n" +
            "- Reservation finalization\n" +
            "- Station management"
    }
    
    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
