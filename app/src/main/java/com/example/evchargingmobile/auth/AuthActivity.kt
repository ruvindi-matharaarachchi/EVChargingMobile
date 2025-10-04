package com.example.evchargingmobile.auth

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evchargingmobile.R
import com.example.evchargingmobile.admin.AdminDashboardActivity
import com.example.evchargingmobile.common.Hashing
import com.example.evchargingmobile.common.Toasts
import com.example.evchargingmobile.data.api.AuthApi
import com.example.evchargingmobile.data.api.UserApi
import com.example.evchargingmobile.data.dto.LoginRequest
import com.example.evchargingmobile.data.mapper.toModel
import com.example.evchargingmobile.data.repo.UserRepository
import com.example.evchargingmobile.db.UserDbHelper
import com.example.evchargingmobile.db.UserDao
import com.example.evchargingmobile.operator.OperatorHomeActivity
import com.example.evchargingmobile.user.ProfileActivity
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {
    
    private lateinit var sessionStore: SessionStore
    private lateinit var authApi: AuthApi
    private lateinit var userRepository: UserRepository
    
    companion object {
        private const val ALLOW_SELF_SIGNUP = false // Set to false as per requirements
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        
        // Initialize components
        sessionStore = SessionStore(this)
        authApi = AuthApi()
        
        // Initialize database for Owner caching
        val dbHelper = UserDbHelper(this)
        val db = dbHelper.writableDatabase
        val userDao = UserDao(db)
        val userApi = UserApi()
        userRepository = UserRepository(userApi, userDao)
        
        // Check if already logged in
        if (sessionStore.isLoggedIn()) {
            navigateToRoleHome()
            return
        }
        
        setupUI()
    }
    
    private fun setupUI() {
        // Login button
        findViewById<android.widget.Button>(R.id.btnLogin).setOnClickListener {
            performLogin()
        }
        
        // Hide register button if self-signup is disabled
        if (!ALLOW_SELF_SIGNUP) {
            findViewById<android.widget.Button>(R.id.btnRegister)?.visibility = android.view.View.GONE
        }
    }
    
    private fun performLogin() {
        val usernameOrEmail = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etUsernameOrEmail).text.toString().trim()
        val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPassword).text.toString().trim()
        
        // Validation
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            Toasts.showError(this, "Please enter username/email and password")
            return
        }
        
        // Auto-detect role based on username
        val role = when (usernameOrEmail.lowercase()) {
            "admin" -> "BACKOFFICER"
            "owner" -> "OWNER"
            "operator" -> "OPERATOR"
            else -> {
                // Try to determine role by checking credentials
                when {
                    usernameOrEmail.contains("admin") -> "BACKOFFICER"
                    usernameOrEmail.contains("owner") -> "OWNER"
                    usernameOrEmail.contains("operator") -> "OPERATOR"
                    else -> "OWNER" // Default to OWNER if unclear
                }
            }
        }
        
        // Show loading
        findViewById<android.widget.Button>(R.id.btnLogin).isEnabled = false
        findViewById<android.widget.ProgressBar>(R.id.progressBar)?.visibility = android.view.View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val loginRequest = LoginRequest(usernameOrEmail, password, role)
                when (val result = authApi.login(loginRequest)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        val response = result.data
                        android.util.Log.d("AuthActivity", "Login successful for role: ${response.role}")
                        Toasts.showSuccess(this@AuthActivity, "Login successful! Role: ${response.role}")
                        
                        // Save session
                        sessionStore.save(response.token, response.role, response.nic)
                        
                        // Navigate based on role
                        when (response.role) {
                            "BACKOFFICER" -> {
                                try {
                                    android.util.Log.d("AuthActivity", "Navigating to AdminDashboardActivity")
                                    val intent = Intent(this@AuthActivity, AdminDashboardActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } catch (e: Exception) {
                                    android.util.Log.e("AuthActivity", "Error navigating to AdminDashboardActivity", e)
                                    Toasts.showError(this@AuthActivity, "Navigation error: ${e.message}")
                                }
                            }
                            "OWNER" -> {
                                // Fetch and cache owner profile
                                response.nic?.let { nic ->
                                    when (val ownerResult = userRepository.getOwner(nic, response.token)) {
                                        is com.example.evchargingmobile.utils.Result.Success -> {
                                            val ownerModel = ownerResult.data
                                            // Cache with a dummy hashed password (not used for auth)
                                            userRepository.cacheOwner(ownerModel, Hashing.sha256("cached"))
                                            
                                            android.util.Log.d("AuthActivity", "Navigating to ProfileActivity")
                                            val intent = Intent(this@AuthActivity, ProfileActivity::class.java)
                                            intent.putExtra("ownerNic", nic)
                                            startActivity(intent)
                                            finish()
                                        }
                                        is com.example.evchargingmobile.utils.Result.Error -> {
                                            Toasts.showError(this@AuthActivity, "Failed to load profile: ${ownerResult.message}")
                                            return@launch
                                        }
                                    }
                                }
                            }
                            "OPERATOR" -> {
                                android.util.Log.d("AuthActivity", "Navigating to OperatorHomeActivity")
                                val intent = Intent(this@AuthActivity, OperatorHomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@AuthActivity, result.message)
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@AuthActivity, "Login failed: ${e.message}")
            } finally {
                findViewById<android.widget.Button>(R.id.btnLogin).isEnabled = true
                findViewById<android.widget.ProgressBar>(R.id.progressBar)?.visibility = android.view.View.GONE
            }
        }
    }
    
    private fun navigateToRoleHome() {
        val role = sessionStore.getRole()
        when (role) {
            "BACKOFFICER" -> {
                val intent = Intent(this, AdminDashboardActivity::class.java)
                startActivity(intent)
            }
            "OWNER" -> {
                val intent = Intent(this, ProfileActivity::class.java)
                sessionStore.getOwnerNic()?.let { intent.putExtra("ownerNic", it) }
                startActivity(intent)
            }
            "OPERATOR" -> {
                val intent = Intent(this, OperatorHomeActivity::class.java)
                startActivity(intent)
            }
        }
        finish()
    }
}
