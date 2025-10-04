package com.example.evchargingmobile.data.remote

import com.example.evchargingmobile.domain.Reservation
import com.example.evchargingmobile.utils.Result
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object ReservationApi {
    private const val BASE = "http://<set-later>/api"
    private const val CONNECT_TIMEOUT = 10000 // 10 seconds
    private const val READ_TIMEOUT = 15000 // 15 seconds

    fun listByOwner(nic: String): Result<List<Reservation>> {
        return try {
            // TODO: Implement actual API call
            // For now, return empty list
            Result.Success(emptyList())
        } catch (e: Exception) {
            Result.Error("Failed to fetch reservations: ${e.message}")
        }
    }

    fun create(ownerNic: String, stationId: String, slotTime: Long): Result<Reservation> {
        return try {
            // TODO: Implement actual API call
            // For now, create mock reservation
            val mockReservation = Reservation(
                id = UUID.randomUUID().toString(),
                ownerNic = ownerNic,
                stationId = stationId,
                slotTime = slotTime,
                status = "PENDING"
            )
            Result.Success(mockReservation)
        } catch (e: Exception) {
            Result.Error("Failed to create reservation: ${e.message}")
        }
    }

    fun reschedule(id: String, slotTime: Long): Result<Unit> {
        return try {
            // TODO: Implement actual API call
            // For now, mock success
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to reschedule reservation: ${e.message}")
        }
    }

    fun cancel(id: String): Result<Unit> {
        return try {
            // TODO: Implement actual API call
            // For now, mock success
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to cancel reservation: ${e.message}")
        }
    }

    private fun makeRequest(method: String, path: String, jsonData: JSONObject? = null): String {
        val url = URL("$BASE$path")
        val connection = url.openConnection() as HttpURLConnection
        
        return try {
            connection.apply {
                requestMethod = method
                connectTimeout = CONNECT_TIMEOUT
                readTimeout = READ_TIMEOUT
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
            }

            jsonData?.let { data ->
                connection.doOutput = true
                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(data.toString())
                    writer.flush()
                }
            }

            val responseCode = connection.responseCode
            val inputStream = if (responseCode >= 200 && responseCode < 300) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readText()
            }
        } finally {
            connection.disconnect()
        }
    }
}

