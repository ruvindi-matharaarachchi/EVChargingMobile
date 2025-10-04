package com.example.evchargingmobile.data.dto

data class OperatorDto(
    val id: String,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val status: String // ACTIVE, DEACTIVATED
)
