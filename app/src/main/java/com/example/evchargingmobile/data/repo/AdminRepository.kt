package com.example.evchargingmobile.data.repo

import com.example.evchargingmobile.utils.Result
import com.example.evchargingmobile.data.api.AdminApi
import com.example.evchargingmobile.data.dto.*

class AdminRepository(private val adminApi: AdminApi) {
    
    // Owners
    suspend fun listOwners(query: String? = null, token: String): Result<List<OwnerDto>> {
        return adminApi.listOwners(query, token)
    }
    
    suspend fun getOwner(nic: String, token: String): Result<OwnerDto> {
        return adminApi.getOwner(nic, token)
    }
    
    suspend fun createOwner(request: CreateOwnerRequest, token: String): Result<OwnerDto> {
        return adminApi.createOwner(request, token)
    }
    
    suspend fun updateOwner(nic: String, request: UpdateOwnerRequest, token: String): Result<OwnerDto> {
        return adminApi.updateOwner(nic, request, token)
    }
    
    suspend fun deactivateOwner(nic: String, token: String): Result<Unit> {
        return adminApi.deactivateOwner(nic, token)
    }
    
    // Operators
    suspend fun listOperators(query: String? = null, token: String): Result<List<OperatorDto>> {
        return adminApi.listOperators(query, token)
    }
    
    suspend fun getOperator(id: String, token: String): Result<OperatorDto> {
        return adminApi.getOperator(id, token)
    }
    
    suspend fun createOperator(request: CreateOperatorRequest, token: String): Result<OperatorDto> {
        return adminApi.createOperator(request, token)
    }
    
    suspend fun updateOperator(id: String, request: UpdateOperatorRequest, token: String): Result<OperatorDto> {
        return adminApi.updateOperator(id, request, token)
    }
    
    suspend fun deactivateOperator(id: String, token: String): Result<Unit> {
        return adminApi.deactivateOperator(id, token)
    }
}
