package com.example.evchargingmobile.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evchargingmobile.R
import com.example.evchargingmobile.auth.AuthActivity
import com.example.evchargingmobile.auth.SessionStore
import com.example.evchargingmobile.common.Toasts
import com.example.evchargingmobile.data.repo.AdminRepository
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {
    
    private lateinit var sessionStore: SessionStore
    private lateinit var adminRepository: AdminRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        
        android.util.Log.d("AdminDashboardActivity", "Activity created successfully")
        
        sessionStore = SessionStore(this)
        adminRepository = AdminRepository(com.example.evchargingmobile.data.api.AdminApi())
        
        // Check authentication
        if (!sessionStore.isLoggedIn()) {
            android.util.Log.d("AdminDashboardActivity", "Not logged in, navigating to auth")
            navigateToAuth()
            return
        }
        
        android.util.Log.d("AdminDashboardActivity", "User is logged in, continuing with setup")
        
        android.util.Log.d("AdminDashboardActivity", "Setting up UI")
        setupUI()
        loadCounts()
    }
    
    private fun setupUI() {
        // Manage Owners button
        findViewById<android.widget.Button>(R.id.btnManageOwners).setOnClickListener {
            val intent = Intent(this, ManageOwnersActivity::class.java)
            startActivity(intent)
        }
        
        // Manage Operators button
        findViewById<android.widget.Button>(R.id.btnManageOperators).setOnClickListener {
            val intent = Intent(this, ManageOperatorsActivity::class.java)
            startActivity(intent)
        }
        
        // Logout button
        findViewById<android.widget.Button>(R.id.btnLogout).setOnClickListener {
            sessionStore.clear()
            navigateToAuth()
        }
    }
    
    private fun loadCounts() {
        val token = sessionStore.getToken() ?: return
        
        lifecycleScope.launch {
            try {
                // Load owners count
                when (val ownersResult = adminRepository.listOwners(token = token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        val ownersCount = ownersResult.data.size
                        findViewById<android.widget.TextView>(R.id.tvOwnersCount).text = "Owners: $ownersCount"
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        findViewById<android.widget.TextView>(R.id.tvOwnersCount).text = "Owners: Error"
                    }
                }
                
                // Load operators count
                when (val operatorsResult = adminRepository.listOperators(token = token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        val operatorsCount = operatorsResult.data.size
                        findViewById<android.widget.TextView>(R.id.tvOperatorsCount).text = "Operators: $operatorsCount"
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        findViewById<android.widget.TextView>(R.id.tvOperatorsCount).text = "Operators: Error"
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@AdminDashboardActivity, "Failed to load counts: ${e.message}")
            }
        }
    }
    
    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
