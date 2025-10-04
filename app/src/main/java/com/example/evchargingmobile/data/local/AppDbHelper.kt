package com.example.evchargingmobile.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "ev.db"
        private const val DB_VERSION = 3

            // Owner table
            private const val CREATE_TABLE_OWNER = """
                CREATE TABLE owner_user(
                    nic TEXT PRIMARY KEY,
                    username TEXT UNIQUE NOT NULL,
                    full_name TEXT,
                    email TEXT,
                    phone TEXT,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL DEFAULT 'OWNER',
                    is_active INTEGER NOT NULL DEFAULT 1,
                    last_sync_at INTEGER
                )
            """

            // Reservation table
            private const val CREATE_TABLE_RESERVATION = """
                CREATE TABLE reservation(
                    id TEXT PRIMARY KEY,
                    owner_nic TEXT NOT NULL,
                    station_id TEXT NOT NULL,
                    slot_time INTEGER NOT NULL,
                    status TEXT NOT NULL CHECK(status IN ('PENDING','APPROVED','CANCELLED','COMPLETED')),
                    qr_token TEXT,
                    FOREIGN KEY(owner_nic) REFERENCES owner_user(nic) ON DELETE CASCADE
                )
            """

            // Station cache table
            private const val CREATE_TABLE_STATION_CACHE = """
                CREATE TABLE station_cache(
                    station_id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL CHECK(type IN ('AC','DC')),
                    lat REAL NOT NULL,
                    lng REAL NOT NULL,
                    available_slots INTEGER NOT NULL DEFAULT 0,
                    is_active INTEGER NOT NULL DEFAULT 1
                )
            """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_OWNER)
        db.execSQL(CREATE_TABLE_RESERVATION)
        db.execSQL(CREATE_TABLE_STATION_CACHE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO: Implement database migration strategy
        // For now, drop and recreate tables
        db.execSQL("DROP TABLE IF EXISTS reservation")
        db.execSQL("DROP TABLE IF EXISTS station_cache")
        db.execSQL("DROP TABLE IF EXISTS owner_user")
        onCreate(db)
    }
}

