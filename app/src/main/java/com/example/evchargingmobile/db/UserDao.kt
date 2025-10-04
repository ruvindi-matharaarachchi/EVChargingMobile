package com.example.evchargingmobile.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.evchargingmobile.user.OwnerModel

class UserDao(private val db: SQLiteDatabase) {

    fun insert(owner: OwnerModel, hashedPassword: String): Long {
        val values = ContentValues().apply {
            put("nic", owner.nic)
            put("full_name", owner.fullName)
            put("email", owner.email)
            put("phone", owner.phone)
            put("hashed_password", hashedPassword)
            put("status", owner.status)
            put("created_at", owner.createdAt)
            put("updated_at", owner.updatedAt)
        }
        return db.insert("users", null, values)
    }

    fun update(owner: OwnerModel): Int {
        val values = ContentValues().apply {
            put("full_name", owner.fullName)
            put("email", owner.email)
            put("phone", owner.phone)
            put("status", owner.status)
            put("updated_at", owner.updatedAt)
        }
        return db.update("users", values, "nic = ?", arrayOf(owner.nic))
    }

    fun updateStatus(nic: String, status: String): Int {
        val values = ContentValues().apply {
            put("status", status)
            put("updated_at", System.currentTimeMillis())
        }
        return db.update("users", values, "nic = ?", arrayOf(nic))
    }

    fun findByNic(nic: String): OwnerModel? {
        val cursor = db.query(
            "users",
            arrayOf("nic", "full_name", "email", "phone", "status", "created_at", "updated_at"),
            "nic = ?",
            arrayOf(nic),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            OwnerModel(
                nic = cursor.getString(0),
                fullName = cursor.getString(1),
                email = cursor.getString(2),
                phone = cursor.getString(3),
                status = cursor.getString(4),
                createdAt = cursor.getLong(5),
                updatedAt = cursor.getLong(6)
            )
        } else {
            null
        }.also { cursor.close() }
    }

    fun existsByNic(nic: String): Boolean {
        val cursor = db.query(
            "users",
            arrayOf("nic"),
            "nic = ?",
            arrayOf(nic),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun deleteByNic(nic: String): Int {
        return db.delete("users", "nic = ?", arrayOf(nic))
    }
}
