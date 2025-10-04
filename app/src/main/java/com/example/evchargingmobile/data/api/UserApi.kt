package com.example.evchargingmobile.data.api

import com.example.evchargingmobile.utils.Result
import com.example.evchargingmobile.data.dto.OwnerDto
import com.example.evchargingmobile.data.dto.UpdateOwnerRequest

class UserApi {
    
    // Mock owner data for testing
    private val mockOwner = OwnerDto(
        nic = "1234567890",
        fullName = "John Doe",
        email = "john@example.com",
        phone = "+1234567890",
        status = "ACTIVE"
    )
    
    suspend fun getOwner(nic: String, token: String): Result<OwnerDto> {
        kotlinx.coroutines.delay(500)
        
        return if (nic == mockOwner.nic) {
            Result.Success(mockOwner)
        } else {
            Result.Error("Owner not found")
        }
    }
    
    suspend fun updateOwner(nic: String, body: UpdateOwnerRequest, token: String): Result<OwnerDto> {
        kotlinx.coroutines.delay(600)
        
        return if (nic == mockOwner.nic) {
            val updatedOwner = OwnerDto(
                nic = nic,
                fullName = body.fullName,
                email = body.email,
                phone = body.phone,
                status = mockOwner.status
            )
            Result.Success(updatedOwner)
        } else {
            Result.Error("Owner not found")
        }
    }
    
    suspend fun deactivateOwner(nic: String, token: String): Result<Unit> {
        kotlinx.coroutines.delay(500)
        return Result.Success(Unit)
    }
}