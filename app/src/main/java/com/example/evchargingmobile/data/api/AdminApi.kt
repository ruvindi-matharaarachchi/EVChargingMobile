package com.example.evchargingmobile.data.api

import com.example.evchargingmobile.utils.Result
import com.example.evchargingmobile.data.dto.*

class AdminApi {
    
    // Mock data for testing
    private val mockOwners = listOf(
        OwnerDto("1234567890", "John Doe", "john@example.com", "+1234567890", "ACTIVE"),
        OwnerDto("0987654321", "Jane Smith", "jane@example.com", "+0987654321", "ACTIVE"),
        OwnerDto("1122334455", "Bob Johnson", "bob@example.com", "+1122334455", "DEACTIVATED")
    )
    
    private val mockOperators = listOf(
        OperatorDto("OP001", "Alice Brown", "alice@example.com", "+1111111111", "ACTIVE"),
        OperatorDto("OP002", "Charlie Wilson", "charlie@example.com", "+2222222222", "ACTIVE"),
        OperatorDto("OP003", "Diana Davis", "diana@example.com", "+3333333333", "DEACTIVATED")
    )
    
    // Owners
    suspend fun listOwners(query: String? = null, token: String): Result<List<OwnerDto>> {
        kotlinx.coroutines.delay(500)
        
        val filteredOwners = if (query.isNullOrEmpty()) {
            mockOwners
        } else {
            mockOwners.filter { 
                it.fullName.contains(query, ignoreCase = true) || 
                it.nic.contains(query, ignoreCase = true) ||
                it.email?.contains(query, ignoreCase = true) == true
            }
        }
        
        return Result.Success(filteredOwners)
    }
    
    suspend fun getOwner(nic: String, token: String): Result<OwnerDto> {
        kotlinx.coroutines.delay(300)
        
        val owner = mockOwners.find { it.nic == nic }
        return if (owner != null) {
            Result.Success(owner)
        } else {
            Result.Error("Owner not found")
        }
    }
    
    suspend fun createOwner(body: CreateOwnerRequest, token: String): Result<OwnerDto> {
        kotlinx.coroutines.delay(800)
        
        val newOwner = OwnerDto(
            nic = body.nic,
            fullName = body.fullName,
            email = body.email,
            phone = body.phone,
            status = "ACTIVE"
        )
        
        return Result.Success(newOwner)
    }
    
    suspend fun updateOwner(nic: String, body: UpdateOwnerRequest, token: String): Result<OwnerDto> {
        kotlinx.coroutines.delay(600)
        
        val existingOwner = mockOwners.find { it.nic == nic }
        return if (existingOwner != null) {
            val updatedOwner = OwnerDto(
                nic = nic,
                fullName = body.fullName,
                email = body.email,
                phone = body.phone,
                status = existingOwner.status
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
    
    // Operators
    suspend fun listOperators(query: String? = null, token: String): Result<List<OperatorDto>> {
        kotlinx.coroutines.delay(500)
        
        val filteredOperators = if (query.isNullOrEmpty()) {
            mockOperators
        } else {
            mockOperators.filter { 
                it.fullName.contains(query, ignoreCase = true) || 
                it.id.contains(query, ignoreCase = true) ||
                it.email?.contains(query, ignoreCase = true) == true
            }
        }
        
        return Result.Success(filteredOperators)
    }
    
    suspend fun getOperator(id: String, token: String): Result<OperatorDto> {
        kotlinx.coroutines.delay(300)
        
        val operator = mockOperators.find { it.id == id }
        return if (operator != null) {
            Result.Success(operator)
        } else {
            Result.Error("Operator not found")
        }
    }
    
    suspend fun createOperator(body: CreateOperatorRequest, token: String): Result<OperatorDto> {
        kotlinx.coroutines.delay(800)
        
        val newOperator = OperatorDto(
            id = "OP${(mockOperators.size + 1).toString().padStart(3, '0')}",
            fullName = body.fullName,
            email = body.email,
            phone = body.phone,
            status = "ACTIVE"
        )
        
        return Result.Success(newOperator)
    }
    
    suspend fun updateOperator(id: String, body: UpdateOperatorRequest, token: String): Result<OperatorDto> {
        kotlinx.coroutines.delay(600)
        
        val existingOperator = mockOperators.find { it.id == id }
        return if (existingOperator != null) {
            val updatedOperator = OperatorDto(
                id = id,
                fullName = body.fullName,
                email = body.email,
                phone = body.phone,
                status = existingOperator.status
            )
            Result.Success(updatedOperator)
        } else {
            Result.Error("Operator not found")
        }
    }
    
    suspend fun deactivateOperator(id: String, token: String): Result<Unit> {
        kotlinx.coroutines.delay(500)
        return Result.Success(Unit)
    }
}