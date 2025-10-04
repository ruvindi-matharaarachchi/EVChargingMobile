package com.example.evchargingmobile.data.dto

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String,
    val role: String // BACKOFFICER, OWNER, OPERATOR
)
