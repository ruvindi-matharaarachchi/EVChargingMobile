package com.example.evchargingmobile.common

import android.util.Log
import com.example.evchargingmobile.utils.Result
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object Network {
    // TODO: Update this with your actual API base URL
    const val BASE_URL = "https://your-api-domain.com/api/"
    
    private const val TAG = "Network"
    
    suspend fun httpGet(path: String, token: String? = null): Result<String> {
        return try {
            val url = URL("$BASE_URL$path")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            
            if (token != null) {
                connection.setRequestProperty("Authorization", "Bearer $token")
            }
            
            val responseCode = connection.responseCode
            val inputStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            
            val response = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            
            if (responseCode in 200..299) {
                Result.Success(response)
            } else {
                val errorMessage = try {
                    val json = JSONObject(response)
                    json.optString("message", "HTTP $responseCode")
                } catch (e: Exception) {
                    "HTTP $responseCode"
                }
                Result.Error(errorMessage, responseCode)
            }
        } catch (e: Exception) {
            Log.e(TAG, "HTTP GET error", e)
            Result.Error("Network error: ${e.message}")
        }
    }
    
    suspend fun httpPost(path: String, body: String? = null, token: String? = null): Result<String> {
        return try {
            val url = URL("$BASE_URL$path")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            
            if (token != null) {
                connection.setRequestProperty("Authorization", "Bearer $token")
            }
            
            connection.doOutput = true
            
            if (body != null) {
                OutputStreamWriter(connection.outputStream).use { it.write(body) }
            }
            
            val responseCode = connection.responseCode
            val inputStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            
            val response = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            
            if (responseCode in 200..299) {
                Result.Success(response)
            } else {
                val errorMessage = try {
                    val json = JSONObject(response)
                    json.optString("message", "HTTP $responseCode")
                } catch (e: Exception) {
                    "HTTP $responseCode"
                }
                Result.Error(errorMessage, responseCode)
            }
        } catch (e: Exception) {
            Log.e(TAG, "HTTP POST error", e)
            Result.Error("Network error: ${e.message}")
        }
    }
    
    suspend fun httpPut(path: String, body: String? = null, token: String? = null): Result<String> {
        return try {
            val url = URL("$BASE_URL$path")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "PUT"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            
            if (token != null) {
                connection.setRequestProperty("Authorization", "Bearer $token")
            }
            
            connection.doOutput = true
            
            if (body != null) {
                OutputStreamWriter(connection.outputStream).use { it.write(body) }
            }
            
            val responseCode = connection.responseCode
            val inputStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            
            val response = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            
            if (responseCode in 200..299) {
                Result.Success(response)
            } else {
                val errorMessage = try {
                    val json = JSONObject(response)
                    json.optString("message", "HTTP $responseCode")
                } catch (e: Exception) {
                    "HTTP $responseCode"
                }
                Result.Error(errorMessage, responseCode)
            }
        } catch (e: Exception) {
            Log.e(TAG, "HTTP PUT error", e)
            Result.Error("Network error: ${e.message}")
        }
    }
}
