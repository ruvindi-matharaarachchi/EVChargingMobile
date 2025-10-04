package com.example.evchargingmobile.data.dto

data class CreateOwnerRequest(
    val nic: String,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val password: String
)
