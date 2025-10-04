package com.example.evchargingmobile.ui.common

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.OwnerDao
import com.example.evchargingmobile.data.local.ReservationDao
import com.example.evchargingmobile.data.local.StationDao

class DatabaseViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_viewer)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        val ownerDao = OwnerDao(db)
        val reservationDao = ReservationDao(db)
        val stationDao = StationDao(db)

        val tvDatabaseInfo = findViewById<TextView>(R.id.tvDatabaseInfo)
        
        val databaseInfo = buildString {
            appendLine("📊 DATABASE INFORMATION")
            appendLine("========================")
            appendLine("Database Path: ${db.path}")
            appendLine("Database Version: ${db.version}")
            appendLine("")
            
            // Check if tables exist
            val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
            appendLine("📋 TABLES:")
            while (cursor.moveToNext()) {
                appendLine("  • ${cursor.getString(0)}")
            }
            cursor.close()
            appendLine("")
            
            // Count records in each table
            appendLine("📈 RECORD COUNTS:")
            try {
                val ownerCount = db.rawQuery("SELECT COUNT(*) FROM owner_user", null)
                if (ownerCount.moveToFirst()) {
                    appendLine("  • Owners: ${ownerCount.getInt(0)}")
                }
                ownerCount.close()
                
                val reservationCount = db.rawQuery("SELECT COUNT(*) FROM reservation", null)
                if (reservationCount.moveToFirst()) {
                    appendLine("  • Reservations: ${reservationCount.getInt(0)}")
                }
                reservationCount.close()
                
                val stationCount = db.rawQuery("SELECT COUNT(*) FROM station_cache", null)
                if (stationCount.moveToFirst()) {
                    appendLine("  • Stations: ${stationCount.getInt(0)}")
                }
                stationCount.close()
            } catch (e: Exception) {
                appendLine("  Error counting records: ${e.message}")
            }
        }
        
        tvDatabaseInfo.text = databaseInfo
    }
}

