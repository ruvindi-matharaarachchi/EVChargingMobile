package com.example.evchargingmobile.data.remote

import com.example.evchargingmobile.domain.Owner
import com.example.evchargingmobile.utils.Result
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object OwnerApi {
    private const val BASE = "http://<set-later>/api"
    private const val CONNECT_TIMEOUT = 10000 // 10 seconds
    private const val READ_TIMEOUT = 15000 // 15 seconds

    fun createOwner(o: Owner): Result<Unit> {
        return try {
            // TODO: Implement actual API call
            // For now, mock success
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to create owner: ${e.message}")
        }
    }

    fun updateOwner(o: Owner, deactivate: Boolean = false): Result<Unit> {
        return try {
            // TODO: Implement actual API call
            // For now, mock success
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to update owner: ${e.message}")
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

