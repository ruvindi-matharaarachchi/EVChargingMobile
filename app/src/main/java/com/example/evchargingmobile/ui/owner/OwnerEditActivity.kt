package com.example.evchargingmobile.ui.owner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.OwnerDao
import com.example.evchargingmobile.domain.Owner
import com.example.evchargingmobile.utils.Validators
import com.example.evchargingmobile.utils.snack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OwnerEditActivity : AppCompatActivity() {
    private lateinit var ownerDao: OwnerDao
    private var ownerNic: String? = null
    private var currentOwner: Owner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_edit)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        ownerDao = OwnerDao(db)

        ownerNic = intent.getStringExtra("ownerNic")
        if (ownerNic == null) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Owner NIC not found")
            finish()
            return
        }

        loadOwnerData()
        setupButtons()
    }

    private fun loadOwnerData() {
        ownerNic?.let { nic ->
            currentOwner = ownerDao.get(nic)
            if (currentOwner != null) {
                findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditNic).setText(currentOwner!!.nic)
                findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditUsername).setText(currentOwner!!.username)
                findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditFullName).setText(currentOwner!!.fullName)
                findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditEmail).setText(currentOwner!!.email ?: "")
                findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditPhone).setText(currentOwner!!.phone ?: "")
                findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditPassword).setText(currentOwner!!.password)
            } else {
                findViewById<android.widget.ScrollView>(R.id.root).snack("Owner not found")
                finish()
            }
        }
    }

    private fun setupButtons() {
        findViewById<android.widget.Button>(R.id.btnUpdate).setOnClickListener {
            updateOwner()
        }

        findViewById<android.widget.Button>(R.id.btnDeactivate).setOnClickListener {
            deactivateOwner()
        }

        findViewById<android.widget.Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }

    private fun updateOwner() {
        val nic = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditNic).text.toString().trim()
        val username = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditUsername).text.toString().trim()
        val fullName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditFullName).text.toString().trim()
        val email = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditEmail).text.toString().trim()
        val phone = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditPhone).text.toString().trim()
        val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditPassword).text.toString().trim()

        if (nic.isEmpty() || username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("NIC, Username, Full Name, and Password are required")
            return
        }

        if (!Validators.isValidNic(nic)) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Invalid NIC format")
            return
        }

        // Check if username is taken by another user
        val existingUser = ownerDao.getByUsername(username)
        if (existingUser != null && existingUser.nic != currentOwner!!.nic) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Username already taken by another user")
            return
        }

        val updatedOwner = currentOwner!!.copy(
            nic = nic,
            username = username,
            fullName = fullName,
            email = if (email.isEmpty()) null else email,
            phone = if (phone.isEmpty()) null else phone,
            password = password
        )

        try {
            ownerDao.upsert(updatedOwner)
            findViewById<android.widget.ScrollView>(R.id.root).snack("Profile updated successfully!")

            // TODO: also call OwnerApi.updateOwner in background
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // OwnerApi.updateOwner(updatedOwner)
                } catch (e: Exception) {
                    // Ignore API errors for now
                }
            }

            // Update current owner reference
            currentOwner = updatedOwner
        } catch (e: Exception) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Error updating profile: ${e.message}")
        }
    }

    private fun deactivateOwner() {
        currentOwner?.let { owner ->
            try {
                ownerDao.deactivate(owner.nic)
                findViewById<android.widget.ScrollView>(R.id.root).snack("Account deactivated successfully!")

                // TODO: also call OwnerApi.updateOwner(deactivate = true) in background
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // OwnerApi.updateOwner(owner, deactivate = true)
                    } catch (e: Exception) {
                        // Ignore API errors for now
                    }
                }

                // Go back to login
                val intent = Intent(this, com.example.evchargingmobile.ui.auth.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                findViewById<android.widget.ScrollView>(R.id.root).snack("Error deactivating account: ${e.message}")
            }
        }
    }
}