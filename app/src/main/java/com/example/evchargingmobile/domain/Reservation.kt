package com.example.evchargingmobile.domain

data class Reservation(
    val id: String,              // server GUID or UUID (local temp ok)
    val ownerNic: String,
    val stationId: String,
    val slotTime: Long,          // epoch millis
    val status: String,          // PENDING|APPROVED|CANCELLED|COMPLETED
    val qrToken: String? = null
)

