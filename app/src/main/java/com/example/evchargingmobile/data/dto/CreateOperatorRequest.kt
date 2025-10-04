package com.example.evchargingmobile.data.dto

data class CreateOperatorRequest(
    val fullName: String,
    val email: String,
    val phone: String?,
    val username: String,
    val password: String
)
