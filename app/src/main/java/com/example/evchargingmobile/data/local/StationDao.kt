package com.example.evchargingmobile.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.evchargingmobile.domain.Station

class StationDao(private val db: SQLiteDatabase) {

    fun bulkUpsert(stations: List<Station>) {
        stations.forEach { station ->
            val values = ContentValues().apply {
                put("station_id", station.stationId)
                put("name", station.name)
                put("type", station.type)
                put("lat", station.lat)
                put("lng", station.lng)
                put("available_slots", station.available)
                put("is_active", if (station.active) 1 else 0)
            }
            val result = db.update("station_cache", values, "station_id = ?", arrayOf(station.stationId))
            if (result == 0) {
                db.insert("station_cache", null, values)
            }
        }
    }

    fun listAll(): List<Station> {
        val cursor = db.query(
            "station_cache",
            arrayOf("station_id", "name", "type", "lat", "lng", "available_slots", "is_active"),
            null, null, null, null, "name ASC"
        )

        val stations = mutableListOf<Station>()
        while (cursor.moveToNext()) {
            stations.add(
                Station(
                    stationId = cursor.getString(0),
                    name = cursor.getString(1),
                    type = cursor.getString(2),
                    lat = cursor.getDouble(3),
                    lng = cursor.getDouble(4),
                    available = cursor.getInt(5),
                    active = cursor.getInt(6) == 1
                )
            )
        }
        cursor.close()
        return stations
    }

    fun getById(stationId: String): Station? {
        val cursor = db.query(
            "station_cache",
            arrayOf("station_id", "name", "type", "lat", "lng", "available_slots", "is_active"),
            "station_id = ?",
            arrayOf(stationId),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            Station(
                stationId = cursor.getString(0),
                name = cursor.getString(1),
                type = cursor.getString(2),
                lat = cursor.getDouble(3),
                lng = cursor.getDouble(4),
                available = cursor.getInt(5),
                active = cursor.getInt(6) == 1
            )
        } else {
            null
        }.also { cursor.close() }
    }
}