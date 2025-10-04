package com.example.evchargingmobile.data.dto

data class UpdateOwnerRequest(
    val fullName: String,
    val email: String?,
    val phone: String?
)
