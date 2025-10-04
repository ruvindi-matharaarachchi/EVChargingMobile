package com.example.evchargingmobile.user

data class OwnerModel(
    val nic: String,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val status: String, // ACTIVE, DEACTIVATED
    val createdAt: Long,
    val updatedAt: Long
)
