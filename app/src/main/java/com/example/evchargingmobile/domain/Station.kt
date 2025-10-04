package com.example.evchargingmobile.domain

data class Station(
    val stationId: String,
    val name: String,
    val type: String,            // AC|DC
    val lat: Double,
    val lng: Double,
    val available: Int,
    val active: Boolean = true
)