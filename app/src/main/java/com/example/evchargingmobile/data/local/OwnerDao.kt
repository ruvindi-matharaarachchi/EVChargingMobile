package com.example.evchargingmobile.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.evchargingmobile.domain.Owner

class OwnerDao(private val db: SQLiteDatabase) {

    fun upsert(owner: Owner) {
        val values = ContentValues().apply {
            put("nic", owner.nic)
            put("username", owner.username)
            put("full_name", owner.fullName)
            put("email", owner.email)
            put("phone", owner.phone)
            put("password", owner.password)
            put("role", owner.role)
            put("is_active", if (owner.isActive) 1 else 0)
            put("last_sync_at", System.currentTimeMillis())
        }

        val result = db.update("owner_user", values, "nic = ?", arrayOf(owner.nic))
        if (result == 0) {
            db.insert("owner_user", null, values)
        }
    }

    fun get(nic: String): Owner? {
        val cursor = db.query(
            "owner_user",
            arrayOf("nic", "username", "full_name", "email", "phone", "password", "role", "is_active"),
            "nic = ?",
            arrayOf(nic),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            Owner(
                nic = cursor.getString(0),
                username = cursor.getString(1),
                fullName = cursor.getString(2),
                email = cursor.getString(3),
                phone = cursor.getString(4),
                password = cursor.getString(5),
                role = cursor.getString(6),
                isActive = cursor.getInt(7) == 1
            )
        } else {
            null
        }.also { cursor.close() }
    }

    fun getByUsername(username: String): Owner? {
        val cursor = db.query(
            "owner_user",
            arrayOf("nic", "username", "full_name", "email", "phone", "password", "role", "is_active"),
            "username = ?",
            arrayOf(username),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            Owner(
                nic = cursor.getString(0),
                username = cursor.getString(1),
                fullName = cursor.getString(2),
                email = cursor.getString(3),
                phone = cursor.getString(4),
                password = cursor.getString(5),
                role = cursor.getString(6),
                isActive = cursor.getInt(7) == 1
            )
        } else {
            null
        }.also { cursor.close() }
    }

    fun authenticate(username: String, password: String): Owner? {
        val owner = getByUsername(username)
        return if (owner != null && owner.password == password) {
            owner
        } else {
            null
        }
    }

    fun deactivate(nic: String) {
        val values = ContentValues().apply {
            put("is_active", 0)
            put("last_sync_at", System.currentTimeMillis())
        }
        db.update("owner_user", values, "nic = ?", arrayOf(nic))
    }

    fun getAllUsers(): List<com.example.evchargingmobile.domain.Owner> {
        val cursor = db.query(
            "owner_user",
            arrayOf("nic", "username", "full_name", "email", "phone", "password", "role", "is_active"),
            null, null, null, null, "full_name ASC"
        )

        val users = mutableListOf<com.example.evchargingmobile.domain.Owner>()
        while (cursor.moveToNext()) {
            users.add(
                com.example.evchargingmobile.domain.Owner(
                    nic = cursor.getString(0),
                    username = cursor.getString(1),
                    fullName = cursor.getString(2),
                    email = cursor.getString(3),
                    phone = cursor.getString(4),
                    password = cursor.getString(5),
                    role = cursor.getString(6),
                    isActive = cursor.getInt(7) == 1
                )
            )
        }
        cursor.close()
        return users
    }
}

