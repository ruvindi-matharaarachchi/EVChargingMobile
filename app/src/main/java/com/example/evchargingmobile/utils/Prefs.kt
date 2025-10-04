package com.example.evchargingmobile.utils

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ev_charging_prefs", Context.MODE_PRIVATE)

    fun getOperatorToken(): String? {
        return prefs.getString("operator_token", null)
    }

    fun setOperatorToken(token: String) {
        prefs.edit().putString("operator_token", token).apply()
    }

    fun clearOperatorToken() {
        prefs.edit().remove("operator_token").apply()
    }

    fun getLastSyncTime(): Long {
        return prefs.getLong("last_sync_time", 0L)
    }

    fun setLastSyncTime(time: Long) {
        prefs.edit().putLong("last_sync_time", time).apply()
    }
}

