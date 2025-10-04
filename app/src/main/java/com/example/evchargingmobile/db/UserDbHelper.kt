package com.example.evchargingmobile.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "ev_users.db"
        private const val DB_VERSION = 1

        // Users table - only for caching logged-in Owner
        private const val CREATE_TABLE_USERS = """
            CREATE TABLE users (
                nic TEXT PRIMARY KEY,
                full_name TEXT NOT NULL,
                email TEXT,
                phone TEXT,
                hashed_password TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'ACTIVE',
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_USERS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }
}
