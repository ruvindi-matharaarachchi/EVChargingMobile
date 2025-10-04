package com.example.evchargingmobile.domain

data class Owner(
    val nic: String,
    val username: String,  // NEW: Username for login
    val fullName: String,
    val email: String?,
    val phone: String?,
    val password: String,
    val role: String = "OWNER", // OWNER, OPERATOR, ADMIN
    val isActive: Boolean = true
)

