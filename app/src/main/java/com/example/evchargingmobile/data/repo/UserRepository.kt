package com.example.evchargingmobile.data.repo

import com.example.evchargingmobile.utils.Result
import com.example.evchargingmobile.data.api.UserApi
import com.example.evchargingmobile.data.dto.OwnerDto
import com.example.evchargingmobile.data.dto.UpdateOwnerRequest
import com.example.evchargingmobile.data.mapper.toModel
import com.example.evchargingmobile.db.UserDao
import com.example.evchargingmobile.user.OwnerModel

class UserRepository(
    private val userApi: UserApi,
    private val userDao: UserDao
) {
    
    suspend fun getOwner(nic: String, token: String): Result<OwnerModel> {
        return when (val result = userApi.getOwner(nic, token)) {
            is Result.Success -> {
                val ownerModel = result.data.toModel()
                Result.Success(ownerModel)
            }
            is Result.Error -> result
        }
    }
    
    suspend fun updateOwner(nic: String, request: UpdateOwnerRequest, token: String): Result<OwnerModel> {
        return when (val result = userApi.updateOwner(nic, request, token)) {
            is Result.Success -> {
                val ownerModel = result.data.toModel()
                userDao.update(ownerModel)
                Result.Success(ownerModel)
            }
            is Result.Error -> result
        }
    }
    
    suspend fun deactivateOwner(nic: String, token: String): Result<Unit> {
        return when (val result = userApi.deactivateOwner(nic, token)) {
            is Result.Success -> {
                userDao.updateStatus(nic, "DEACTIVATED")
                Result.Success(Unit)
            }
            is Result.Error -> result
        }
    }
    
    fun getCachedOwner(nic: String): OwnerModel? {
        return userDao.findByNic(nic)
    }
    
    fun cacheOwner(owner: OwnerModel, hashedPassword: String) {
        if (userDao.existsByNic(owner.nic)) {
            userDao.update(owner)
        } else {
            userDao.insert(owner, hashedPassword)
        }
    }
}
