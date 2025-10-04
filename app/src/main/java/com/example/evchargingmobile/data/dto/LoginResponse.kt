package com.example.evchargingmobile.data.dto

data class LoginResponse(
    val token: String,
    val role: String,
    val nic: String?, // present when role=OWNER
    val backofficerId: String?, // optional
    val operatorId: String? // optional
)
