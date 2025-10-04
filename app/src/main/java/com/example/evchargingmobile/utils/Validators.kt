package com.example.evchargingmobile.utils

object Validators {
    
    fun isValidNic(nic: String): Boolean {
        // Basic NIC format check - 9-12 characters, alphanumeric
        return nic.isNotBlank() && nic.length >= 9 && nic.length <= 12 && nic.matches(Regex("[A-Za-z0-9]+"))
    }
    
    fun isWithin7Days(nowMillis: Long, slotMillis: Long): Boolean {
        // slot-now <= 7d
        return slotMillis - nowMillis <= 7L * 24 * 60 * 60 * 1000L
    }
    
    fun canModifyOrCancel(nowMillis: Long, slotMillis: Long): Boolean {
        // (slot-now) >= 12h
        return (slotMillis - nowMillis) >= 12 * 60 * 60 * 1000L
    }
}

