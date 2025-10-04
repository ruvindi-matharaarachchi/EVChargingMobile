package com.example.evchargingmobile.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evchargingmobile.R
import com.example.evchargingmobile.auth.AuthActivity
import com.example.evchargingmobile.auth.AuthValidator
import com.example.evchargingmobile.auth.SessionStore
import com.example.evchargingmobile.common.Toasts
import com.example.evchargingmobile.data.dto.CreateOperatorRequest
import com.example.evchargingmobile.data.dto.UpdateOperatorRequest
import com.example.evchargingmobile.data.repo.AdminRepository
import kotlinx.coroutines.launch

class OperatorEditorActivity : AppCompatActivity() {
    
    private lateinit var sessionStore: SessionStore
    private lateinit var adminRepository: AdminRepository
    private var isEdit = false
    private var operatorId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator_editor)
        
        sessionStore = SessionStore(this)
        adminRepository = AdminRepository(com.example.evchargingmobile.data.api.AdminApi())
        
        // Check authentication
        if (!sessionStore.isLoggedIn()) {
            navigateToAuth()
            return
        }
        
        isEdit = intent.getBooleanExtra("isEdit", false)
        operatorId = intent.getStringExtra("operatorId")
        
        setupUI()
        
        if (isEdit && operatorId != null) {
            loadOperator()
        }
    }
    
    private fun setupUI() {
        // Set title
        findViewById<android.widget.TextView>(R.id.tvTitle).text = if (isEdit) "Edit Operator" else "Create Operator"
        
        // Save button
        findViewById<android.widget.Button>(R.id.btnSave).setOnClickListener {
            if (isEdit) {
                updateOperator()
            } else {
                createOperator()
            }
        }
        
        // Cancel button
        findViewById<android.widget.Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }
    
    private fun loadOperator() {
        val token = sessionStore.getToken() ?: return
        
        lifecycleScope.launch {
            try {
                when (val result = adminRepository.getOperator(operatorId!!, token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        val operator = result.data
                        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).setText(operator.fullName)
                        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).setText(operator.email ?: "")
                        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).setText(operator.phone ?: "")
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@OperatorEditorActivity, "Failed to load operator: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@OperatorEditorActivity, "Failed to load operator: ${e.message}")
            }
        }
    }
    
    private fun createOperator() {
        val fullName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).text.toString().trim()
        val email = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).text.toString().trim()
        val phone = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).text.toString().trim()
        val username = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etUsername).text.toString().trim()
        val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPassword).text.toString().trim()
        
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
        
        val usernameError = AuthValidator.validateUsername(username)
        if (usernameError != null) {
            Toasts.showError(this, usernameError)
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
                val request = CreateOperatorRequest(
                    fullName = fullName,
                    email = email,
                    phone = if (phone.isEmpty()) null else phone,
                    username = username,
                    password = password
                )
                
                when (val result = adminRepository.createOperator(request, token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        Toasts.showSuccess(this@OperatorEditorActivity, "Operator created successfully")
                        finish()
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@OperatorEditorActivity, "Failed to create operator: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@OperatorEditorActivity, "Failed to create operator: ${e.message}")
            }
        }
    }
    
    private fun updateOperator() {
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
                val request = UpdateOperatorRequest(
                    fullName = fullName,
                    email = email,
                    phone = if (phone.isEmpty()) null else phone
                )
                
                when (val result = adminRepository.updateOperator(operatorId!!, request, token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        Toasts.showSuccess(this@OperatorEditorActivity, "Operator updated successfully")
                        finish()
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@OperatorEditorActivity, "Failed to update operator: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@OperatorEditorActivity, "Failed to update operator: ${e.message}")
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
