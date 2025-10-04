package com.example.evchargingmobile.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.OwnerDao
import com.example.evchargingmobile.domain.Owner
import com.example.evchargingmobile.utils.Validators
import com.example.evchargingmobile.utils.snack

class RegisterActivity : AppCompatActivity() {
    private lateinit var ownerDao: OwnerDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        ownerDao = OwnerDao(db)

        findViewById<android.widget.Button>(R.id.btnRegister).setOnClickListener {
            performRegistration()
        }

        findViewById<android.widget.Button>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun performRegistration() {
        val username = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etUsername).text.toString().trim()
        val nic = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNic).text.toString().trim()
        val fullName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).text.toString().trim()
        val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPassword).text.toString().trim()
        val confirmPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etConfirmPassword).text.toString().trim()
        val email = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).text.toString().trim()
        val phone = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).text.toString().trim()
        val role = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etRole).text.toString().trim()

        if (username.isEmpty() || nic.isEmpty() || fullName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Username, NIC, Full Name, and Passwords are required")
            return
        }

        if (!Validators.isValidNic(nic)) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Invalid NIC format")
            return
        }

        if (password.length < 6) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Passwords do not match")
            return
        }

        // Check if username already exists
        if (ownerDao.getByUsername(username) != null) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Username already exists")
            return
        }

        // Check if NIC already exists
        if (ownerDao.get(nic) != null) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Account with this NIC already exists")
            return
        }

        val owner = Owner(
            nic = nic,
            username = username,
            fullName = fullName,
            email = if (email.isEmpty()) null else email,
            phone = if (phone.isEmpty()) null else phone,
            password = password,
            role = role
        )

        try {
            ownerDao.upsert(owner)
            findViewById<android.widget.ScrollView>(R.id.root).snack("Registration successful! Please login.")
            
            // Clear all fields
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etUsername).text?.clear()
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNic).text?.clear()
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).text?.clear()
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPassword).text?.clear()
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etConfirmPassword).text?.clear()
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).text?.clear()
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).text?.clear()
            
        } catch (e: Exception) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Registration failed: ${e.message}")
        }
    }
}
