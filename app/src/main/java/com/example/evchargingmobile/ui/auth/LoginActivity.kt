package com.example.evchargingmobile.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.OwnerDao
import com.example.evchargingmobile.ui.owner.OwnerDashboardActivity
import com.example.evchargingmobile.ui.operator.OperatorScanActivity
import com.example.evchargingmobile.ui.admin.AdminDashboardActivity
import com.example.evchargingmobile.utils.Validators
import com.example.evchargingmobile.utils.snack

class LoginActivity : AppCompatActivity() {
    private lateinit var ownerDao: OwnerDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        try {
            val dbHelper = AppDbHelper(this)
            val db = dbHelper.writableDatabase
            ownerDao = OwnerDao(db)

            // Create default admin user if not exists
            createDefaultAdmin()

            findViewById<android.widget.Button>(R.id.btnLogin).setOnClickListener {
                performLogin()
            }

            findViewById<android.widget.Button>(R.id.btnRegister).setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        } catch (e: Exception) {
            // If there's any error, show a simple message and finish
            android.widget.Toast.makeText(this, "App initialization failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun createDefaultAdmin() {
        val adminUsername = "admin"
        if (ownerDao.getByUsername(adminUsername) == null) {
            val admin = com.example.evchargingmobile.domain.Owner(
                nic = "Admin123",
                username = adminUsername,
                fullName = "System Administrator",
                email = "admin@evcharging.com",
                phone = "0112345678",
                password = "admin123",
                role = "ADMIN"
            )
            ownerDao.upsert(admin)
        }
    }

    private fun performLogin() {
        val username = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etUsername).text.toString().trim()
        val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPassword).text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Please enter username and password")
            return
        }

        val owner = ownerDao.authenticate(username, password)
        if (owner == null) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Invalid username or password")
            return
        }

        if (!owner.isActive) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Account is deactivated. Please contact support.")
            return
        }

        // Login successful - redirect based on role
        when (owner.role) {
            "OWNER" -> {
                val intent = Intent(this, OwnerDashboardActivity::class.java)
                intent.putExtra("ownerNic", owner.nic)
                intent.putExtra("ownerName", owner.fullName)
                startActivity(intent)
            }
            "OPERATOR" -> {
                val intent = Intent(this, OperatorScanActivity::class.java)
                intent.putExtra("operatorNic", owner.nic)
                intent.putExtra("operatorName", owner.fullName)
                startActivity(intent)
            }
            "ADMIN" -> {
                val intent = Intent(this, AdminDashboardActivity::class.java)
                intent.putExtra("adminNic", owner.nic)
                intent.putExtra("adminName", owner.fullName)
                startActivity(intent)
            }
            else -> {
                findViewById<android.widget.ScrollView>(R.id.root).snack("Unknown role: ${owner.role}")
                return
            }
        }
        finish()
    }
}

