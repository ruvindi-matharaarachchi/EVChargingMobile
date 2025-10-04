package com.example.evchargingmobile.ui.owner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.OwnerDao
import com.example.evchargingmobile.data.remote.OwnerApi
import com.example.evchargingmobile.domain.Owner
import com.example.evchargingmobile.utils.Validators
import com.example.evchargingmobile.utils.snack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OwnerRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_register)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        val ownerDao = OwnerDao(db)

            findViewById<android.widget.Button>(R.id.btnSave).setOnClickListener {
                val nic = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNic).text.toString().trim()
                val fullName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).text.toString().trim()
                val email = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).text.toString().trim()
                val phone = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).text.toString().trim()

                if (nic.isEmpty() || fullName.isEmpty()) {
                    findViewById<android.widget.ScrollView>(R.id.root).snack("NIC and Full Name are required")
                    return@setOnClickListener
                }

                if (!Validators.isValidNic(nic)) {
                    findViewById<android.widget.ScrollView>(R.id.root).snack("Invalid NIC format")
                    return@setOnClickListener
                }

                // Generate username from NIC for old registration flow
                val username = "user_${nic}"

                val owner = Owner(
                    nic = nic,
                    username = username,
                    fullName = fullName,
                    email = if (email.isEmpty()) null else email,
                    phone = if (phone.isEmpty()) null else phone,
                    password = "default123", // Default password for old registration flow
                    role = "OWNER"
                )

                try {
                    ownerDao.upsert(owner)
                    findViewById<android.widget.ScrollView>(R.id.root).snack("Saved locally ✔")

                    // TODO: also call OwnerApi.createOwner in background (ignore error for now)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            OwnerApi.createOwner(owner)
                        } catch (e: Exception) {
                            // Ignore API errors for now
                        }
                    }

                    finish()
                } catch (e: Exception) {
                    findViewById<android.widget.ScrollView>(R.id.root).snack("Error saving: ${e.message}")
                }
            }
    }
}

