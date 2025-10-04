package com.example.evchargingmobile.user

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evchargingmobile.R
import com.example.evchargingmobile.auth.AuthActivity
import com.example.evchargingmobile.auth.AuthValidator
import com.example.evchargingmobile.auth.SessionStore
import com.example.evchargingmobile.common.Toasts
import com.example.evchargingmobile.data.dto.UpdateOwnerRequest
import com.example.evchargingmobile.data.repo.UserRepository
import com.example.evchargingmobile.db.UserDbHelper
import com.example.evchargingmobile.db.UserDao
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var sessionStore: SessionStore
    private lateinit var userRepository: UserRepository
    private var ownerNic: String? = null
    private var isDeactivated = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        sessionStore = SessionStore(this)
        
        // Initialize database
        val dbHelper = UserDbHelper(this)
        val db = dbHelper.writableDatabase
        val userDao = UserDao(db)
        val userApi = com.example.evchargingmobile.data.api.UserApi()
        userRepository = UserRepository(userApi, userDao)
        
        // Check authentication
        if (!sessionStore.isLoggedIn()) {
            navigateToAuth()
            return
        }
        
        ownerNic = intent.getStringExtra("ownerNic") ?: sessionStore.getOwnerNic()
        if (ownerNic == null) {
            Toasts.showError(this, "Owner NIC not found")
            navigateToAuth()
            return
        }
        
        setupUI()
        loadProfile()
    }
    
    private fun setupUI() {
        // Save button
        findViewById<android.widget.Button>(R.id.btnSave).setOnClickListener {
            if (!isDeactivated) {
                updateProfile()
            }
        }
        
        // Deactivate button
        findViewById<android.widget.Button>(R.id.btnDeactivate).setOnClickListener {
            if (!isDeactivated) {
                deactivateProfile()
            }
        }
        
        // Logout button
        findViewById<android.widget.Button>(R.id.btnLogout).setOnClickListener {
            sessionStore.clear()
            navigateToAuth()
        }
    }
    
    private fun loadProfile() {
        val cachedOwner = userRepository.getCachedOwner(ownerNic!!)
        if (cachedOwner != null) {
            displayProfile(cachedOwner)
        } else {
            // Load from server
            val token = sessionStore.getToken() ?: return
            
            lifecycleScope.launch {
                try {
                    when (val result = userRepository.getOwner(ownerNic!!, token)) {
                        is com.example.evchargingmobile.utils.Result.Success -> {
                            val owner = result.data
                            userRepository.cacheOwner(owner, "cached") // Cache the profile
                            displayProfile(owner)
                        }
                        is com.example.evchargingmobile.utils.Result.Error -> {
                            Toasts.showError(this@ProfileActivity, "Failed to load profile: ${result.message}")
                        }
                    }
                } catch (e: Exception) {
                    Toasts.showError(this@ProfileActivity, "Failed to load profile: ${e.message}")
                }
            }
        }
    }
    
    private fun displayProfile(owner: OwnerModel) {
        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNic).setText(owner.nic)
        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).setText(owner.fullName)
        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).setText(owner.email ?: "")
        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).setText(owner.phone ?: "")
        
        // Check if deactivated
        isDeactivated = owner.status == "DEACTIVATED"
        
        if (isDeactivated) {
            // Disable inputs and show banner
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).isEnabled = false
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).isEnabled = false
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).isEnabled = false
            findViewById<android.widget.Button>(R.id.btnSave).isEnabled = false
            findViewById<android.widget.Button>(R.id.btnDeactivate).isEnabled = false
            
            findViewById<android.widget.TextView>(R.id.tvDeactivatedBanner).visibility = android.view.View.VISIBLE
            findViewById<android.widget.TextView>(R.id.tvDeactivatedBanner).text = "Account deactivated. Reactivation by Backoffice required."
        }
    }
    
    private fun updateProfile() {
        val fullName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).text.toString().trim()
        val email = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).text.toString().trim()
        val phone = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).text.toString().trim()
        
        // Validation
        val fullNameError = AuthValidator.validateFullName(fullName)
        if (fullNameError != null) {
            Toasts.showError(this, fullNameError)
            return
        }
        
        val emailError = AuthValidator.validateEmail(email)
        if (emailError != null) {
            Toasts.showError(this, emailError)
            return
        }
        
        val phoneError = AuthValidator.validatePhone(phone)
        if (phoneError != null) {
            Toasts.showError(this, phoneError)
            return
        }
        
        val token = sessionStore.getToken() ?: return
        
        lifecycleScope.launch {
            try {
                val request = UpdateOwnerRequest(
                    fullName = fullName,
                    email = if (email.isEmpty()) null else email,
                    phone = if (phone.isEmpty()) null else phone
                )
                
                when (val result = userRepository.updateOwner(ownerNic!!, request, token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        Toasts.showSuccess(this@ProfileActivity, "Profile updated successfully")
                        loadProfile() // Refresh the display
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@ProfileActivity, "Failed to update profile: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@ProfileActivity, "Failed to update profile: ${e.message}")
            }
        }
    }
    
    private fun deactivateProfile() {
        val token = sessionStore.getToken() ?: return
        
        lifecycleScope.launch {
            try {
                when (val result = userRepository.deactivateOwner(ownerNic!!, token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        Toasts.showSuccess(this@ProfileActivity, "Account deactivated successfully")
                        loadProfile() // Refresh the display
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@ProfileActivity, "Failed to deactivate account: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@ProfileActivity, "Failed to deactivate account: ${e.message}")
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
