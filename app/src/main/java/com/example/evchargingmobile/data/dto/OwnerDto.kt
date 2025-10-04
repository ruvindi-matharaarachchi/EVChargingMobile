package com.example.evchargingmobile.data.dto

data class OwnerDto(
    val nic: String,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val status: String // ACTIVE, DEACTIVATED
)
