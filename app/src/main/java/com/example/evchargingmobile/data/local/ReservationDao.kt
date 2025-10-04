package com.example.evchargingmobile.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.evchargingmobile.domain.Reservation

class ReservationDao(private val db: SQLiteDatabase) {

    fun upsert(reservation: Reservation) {
        val values = ContentValues().apply {
            put("id", reservation.id)
            put("owner_nic", reservation.ownerNic)
            put("station_id", reservation.stationId)
            put("slot_time", reservation.slotTime)
            put("status", reservation.status)
            put("qr_token", reservation.qrToken)
        }

        val result = db.update("reservation", values, "id = ?", arrayOf(reservation.id))
        if (result == 0) {
            db.insert("reservation", null, values)
        }
    }

    fun listByOwner(nic: String): List<Reservation> {
        val cursor = db.query(
            "reservation",
            arrayOf("id", "owner_nic", "station_id", "slot_time", "status", "qr_token"),
            "owner_nic = ?",
            arrayOf(nic),
            null, null, "slot_time DESC"
        )

        val reservations = mutableListOf<Reservation>()
        while (cursor.moveToNext()) {
            reservations.add(
                Reservation(
                    id = cursor.getString(0),
                    ownerNic = cursor.getString(1),
                    stationId = cursor.getString(2),
                    slotTime = cursor.getLong(3),
                    status = cursor.getString(4),
                    qrToken = cursor.getString(5)
                )
            )
        }
        cursor.close()
        return reservations
    }

    fun updateTime(id: String, newSlot: Long) {
        val values = ContentValues().apply {
            put("slot_time", newSlot)
        }
        db.update("reservation", values, "id = ?", arrayOf(id))
    }

    fun updateStatus(id: String, status: String) {
        val values = ContentValues().apply {
            put("status", status)
        }
        db.update("reservation", values, "id = ?", arrayOf(id))
    }

    fun delete(id: String) {
        db.delete("reservation", "id = ?", arrayOf(id))
    }

    fun getAllReservations(): List<com.example.evchargingmobile.domain.Reservation> {
        val cursor = db.query(
            "reservation",
            arrayOf("id", "owner_nic", "station_id", "slot_time", "status", "qr_token"),
            null, null, null, null, "slot_time DESC"
        )

        val reservations = mutableListOf<com.example.evchargingmobile.domain.Reservation>()
        while (cursor.moveToNext()) {
            reservations.add(
                com.example.evchargingmobile.domain.Reservation(
                    id = cursor.getString(0),
                    ownerNic = cursor.getString(1),
                    stationId = cursor.getString(2),
                    slotTime = cursor.getLong(3),
                    status = cursor.getString(4),
                    qrToken = cursor.getString(5)
                )
            )
        }
        cursor.close()
        return reservations
    }
}

