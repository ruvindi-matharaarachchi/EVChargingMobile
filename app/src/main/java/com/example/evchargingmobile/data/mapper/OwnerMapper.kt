package com.example.evchargingmobile.data.mapper

import com.example.evchargingmobile.data.dto.OwnerDto
import com.example.evchargingmobile.user.OwnerModel

fun OwnerDto.toModel(now: Long = System.currentTimeMillis()): OwnerModel {
    return OwnerModel(
        nic = nic,
        fullName = fullName,
        email = email,
        phone = phone,
        status = status,
        createdAt = now,
        updatedAt = now
    )
}
