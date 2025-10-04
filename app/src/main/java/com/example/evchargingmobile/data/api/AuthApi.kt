package com.example.evchargingmobile.data.api

import com.example.evchargingmobile.common.Network
import com.example.evchargingmobile.utils.Result
import com.example.evchargingmobile.data.dto.LoginRequest
import com.example.evchargingmobile.data.dto.LoginResponse
import org.json.JSONObject

class AuthApi {
    
    // Temporary local admin for testing
    private val testAdmin = mapOf(
        "username" to "admin",
        "password" to "admin123",
        "role" to "BACKOFFICER",
        "backofficerId" to "ADM001"
    )
    
    private val testOwner = mapOf(
        "username" to "owner",
        "password" to "owner123", 
        "role" to "OWNER",
        "nic" to "1234567890"
    )
    
    private val testOperator = mapOf(
        "username" to "operator",
        "password" to "operator123",
        "role" to "OPERATOR", 
        "operatorId" to "OP001"
    )
    
    suspend fun login(body: LoginRequest): Result<LoginResponse> {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)
        
        // Check against test users
        val user = when (body.role) {
            "BACKOFFICER" -> testAdmin
            "OWNER" -> testOwner
            "OPERATOR" -> testOperator
            else -> null
        }
        
        if (user == null) {
            return Result.Error("Invalid role: ${body.role}")
        }
        
        if (user["username"] != body.usernameOrEmail || user["password"] != body.password) {
            return Result.Error("Invalid username or password")
        }
        
        // Generate mock JWT token (expires in 24 hours)
        val mockToken = generateMockJwt(body.role, user)
        
        val response = LoginResponse(
            token = mockToken,
            role = body.role,
            nic = user["nic"],
            backofficerId = user["backofficerId"],
            operatorId = user["operatorId"]
        )
        
        return Result.Success(response)
    }
    
    private fun generateMockJwt(role: String, user: Map<String, String>): String {
        // This is a mock JWT token for testing - in production, this would come from the server
        val header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val payload = android.util.Base64.encodeToString(
            JSONObject().apply {
                put("sub", user["username"])
                put("role", role)
                put("exp", (System.currentTimeMillis() / 1000) + (24 * 60 * 60)) // 24 hours
                user["nic"]?.let { put("nic", it) }
                user["backofficerId"]?.let { put("backofficerId", it) }
                user["operatorId"]?.let { put("operatorId", it) }
            }.toString().toByteArray(),
            android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING
        )
        val signature = "mock_signature_for_testing"
        return "$header.$payload.$signature"
    }
}
