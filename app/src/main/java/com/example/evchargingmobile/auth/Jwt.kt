package com.example.evchargingmobile.auth

import android.util.Base64
import org.json.JSONObject

object Jwt {
    
    fun parseClaims(token: String): Map<String, Any> {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                return emptyMap()
            }
            
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING)
            val payloadJson = String(decodedBytes)
            val jsonObject = JSONObject(payloadJson)
            
            val claims = mutableMapOf<String, Any>()
            jsonObject.keys().forEach { key ->
                claims[key] = jsonObject.get(key)
            }
            claims
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    fun expEpochSeconds(token: String): Long? {
        val claims = parseClaims(token)
        return claims["exp"] as? Long
    }
    
    fun isExpired(token: String, nowSec: Long = System.currentTimeMillis() / 1000): Boolean {
        val exp = expEpochSeconds(token) ?: return true
        return exp < nowSec
    }
    
    fun getRole(token: String): String? {
        val claims = parseClaims(token)
        return claims["role"] as? String
    }
    
    fun getNic(token: String): String? {
        val claims = parseClaims(token)
        return claims["nic"] as? String
    }
}
