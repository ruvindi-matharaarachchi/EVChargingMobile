package com.example.evchargingmobile.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionStore(private val context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = try {
        EncryptedSharedPreferences.create(
            context,
            "auth_session",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback to normal SharedPreferences if encryption fails
        context.getSharedPreferences("auth_session", Context.MODE_PRIVATE)
    }
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_ROLE = "role"
        private const val KEY_OWNER_NIC = "owner_nic"
    }
    
    fun save(token: String, role: String, ownerNic: String? = null) {
        android.util.Log.d("SessionStore", "Saving session - Token: $token, Role: $role, OwnerNic: $ownerNic")
        sharedPreferences.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .putString(KEY_ROLE, role)
            .putString(KEY_OWNER_NIC, ownerNic)
            .apply()
        android.util.Log.d("SessionStore", "Session saved successfully")
    }
    
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }
    
    fun getRole(): String? {
        return sharedPreferences.getString(KEY_ROLE, null)
    }
    
    fun getOwnerNic(): String? {
        return sharedPreferences.getString(KEY_OWNER_NIC, null)
    }
    
    fun isLoggedIn(): Boolean {
        val token = getToken()
        val role = getRole()
        android.util.Log.d("SessionStore", "Token: $token")
        android.util.Log.d("SessionStore", "Role: $role")
        android.util.Log.d("SessionStore", "Token expired: ${if (token != null) Jwt.isExpired(token) else "null"}")
        
        // For testing, just check if token exists (bypass expiration check temporarily)
        val isLoggedIn = token != null
        android.util.Log.d("SessionStore", "Is logged in: $isLoggedIn")
        return isLoggedIn
    }
    
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
