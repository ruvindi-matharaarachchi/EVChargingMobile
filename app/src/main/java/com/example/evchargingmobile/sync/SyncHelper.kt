package com.example.evchargingmobile.sync

import android.content.Context
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.OwnerDao
import com.example.evchargingmobile.data.local.ReservationDao
import com.example.evchargingmobile.data.remote.OwnerApi
import com.example.evchargingmobile.data.remote.ReservationApi
import com.example.evchargingmobile.domain.Owner
import com.example.evchargingmobile.utils.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SyncHelper {
    
    fun pushOwnerIfDirty(owner: Owner) {
        // TODO: call OwnerApi in background, update last_sync_at
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = OwnerApi.updateOwner(owner)
                if (result is com.example.evchargingmobile.utils.Result.Success) {
                    // Update last_sync_at in database
                    val context = android.app.Application()
                    val dbHelper = AppDbHelper(context)
                    val db = dbHelper.writableDatabase
                    val ownerDao = OwnerDao(db)
                    ownerDao.upsert(owner) // This will update last_sync_at
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun refreshReservations(ownerNic: String) {
        // TODO: call ReservationApi.listByOwner, mirror to SQLite
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = ReservationApi.listByOwner(ownerNic)
                if (result is com.example.evchargingmobile.utils.Result.Success) {
                    // Mirror to SQLite
                    val context = android.app.Application()
                    val dbHelper = AppDbHelper(context)
                    val db = dbHelper.writableDatabase
                    val reservationDao = ReservationDao(db)
                    
                    result.data.forEach { reservation ->
                        reservationDao.upsert(reservation)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

