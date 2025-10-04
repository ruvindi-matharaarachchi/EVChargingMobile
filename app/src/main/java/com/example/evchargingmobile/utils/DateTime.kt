package com.example.evchargingmobile.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTime {
    private val shortFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun now(): Long {
        return System.currentTimeMillis()
    }

    fun formatShort(millis: Long): String {
        // e.g., "2025-10-04 09:30"
        return shortFormat.format(Date(millis))
    }

    fun addDays(from: Long, days: Int): Long {
        return from + (days * 24 * 60 * 60 * 1000L)
    }
}

