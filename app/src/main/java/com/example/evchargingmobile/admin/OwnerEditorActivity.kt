package com.example.evchargingmobile.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evchargingmobile.R
import com.example.evchargingmobile.auth.AuthActivity
import com.example.evchargingmobile.auth.AuthValidator
import com.example.evchargingmobile.auth.SessionStore
import com.example.evchargingmobile.common.Toasts
import com.example.evchargingmobile.data.dto.CreateOwnerRequest
import com.example.evchargingmobile.data.dto.UpdateOwnerRequest
import com.example.evchargingmobile.data.repo.AdminRepository
import kotlinx.coroutines.launch

class OwnerEditorActivity : AppCompatActivity() {
    
    private lateinit var sessionStore: SessionStore
    private lateinit var adminRepository: AdminRepository
    private var isEdit = false
    private var ownerNic: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_editor)
        
        sessionStore = SessionStore(this)
        adminRepository = AdminRepository(com.example.evchargingmobile.data.api.AdminApi())
        
        // Check authentication
        if (!sessionStore.isLoggedIn()) {
            navigateToAuth()
            return
        }
        
        isEdit = intent.getBooleanExtra("isEdit", false)
        ownerNic = intent.getStringExtra("ownerNic")
        
        setupUI()
        
        if (isEdit && ownerNic != null) {
            loadOwner()
        }
    }
    
    private fun setupUI() {
        // Set title
        findViewById<android.widget.TextView>(R.id.tvTitle).text = if (isEdit) "Edit Owner" else "Create Owner"
        
        // Disable NIC field for edit mode
        if (isEdit) {
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNic).isEnabled = false
        }
        
        // Save button
        findViewById<android.widget.Button>(R.id.btnSave).setOnClickListener {
            if (isEdit) {
                updateOwner()
            } else {
                createOwner()
            }
        }
        
        // Cancel button
        findViewById<android.widget.Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }
    
    private fun loadOwner() {
        val token = sessionStore.getToken() ?: return
        
        lifecycleScope.launch {
            try {
                when (val result = adminRepository.getOwner(ownerNic!!, token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        val owner = result.data
                        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNic).setText(owner.nic)
                        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).setText(owner.fullName)
                        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).setText(owner.email ?: "")
                        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).setText(owner.phone ?: "")
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@OwnerEditorActivity, "Failed to load owner: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@OwnerEditorActivity, "Failed to load owner: ${e.message}")
            }
        }
    }
    
    private fun createOwner() {
        val nic = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNic).text.toString().trim()
        val fullName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).text.toString().trim()
        val email = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).text.toString().trim()
        val phone = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).text.toString().trim()
        val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPassword).text.toString().trim()
        
        // Validation
        val nicError = AuthValidator.validateNic(nic)
        if (nicError != null) {
            Toasts.showError(this, nicError)
            return
        }
        
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
        
        val passwordError = AuthValidator.validatePassword(password)
        if (passwordError != null) {
            Toasts.showError(this, passwordError)
            return
        }
        
        val token = sessionStore.getToken() ?: return
        
        lifecycleScope.launch {
            try {
                val request = CreateOwnerRequest(
                    nic = nic,
                    fullName = fullName,
                    email = if (email.isEmpty()) null else email,
                    phone = if (phone.isEmpty()) null else phone,
                    password = password
                )
                
                when (val result = adminRepository.createOwner(request, token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        Toasts.showSuccess(this@OwnerEditorActivity, "Owner created successfully")
                        finish()
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@OwnerEditorActivity, "Failed to create owner: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@OwnerEditorActivity, "Failed to create owner: ${e.message}")
            }
        }
    }
    
    private fun updateOwner() {
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
                
                when (val result = adminRepository.updateOwner(ownerNic!!, request, token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        Toasts.showSuccess(this@OwnerEditorActivity, "Owner updated successfully")
                        finish()
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@OwnerEditorActivity, "Failed to update owner: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@OwnerEditorActivity, "Failed to update owner: ${e.message}")
            }
        }
    }
    
    private fun navigateToAuth() {
        val intent = android.content.Intent(this, AuthActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
