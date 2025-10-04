package com.example.evchargingmobile.data.remote

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ApiClient {
    companion object {
        private const val BASE_URL = "http://<set-later>/api" // TODO: Set actual API base URL
        private const val CONNECT_TIMEOUT = 10000 // 10 seconds
        private const val READ_TIMEOUT = 15000 // 15 seconds
    }

    fun get(path: String): String {
        val url = URL("$BASE_URL$path")
        val connection = url.openConnection() as HttpURLConnection
        
        return try {
            connection.apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT
                readTimeout = READ_TIMEOUT
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
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

    fun postJson(path: String, json: JSONObject, token: String? = null): String {
        val url = URL("$BASE_URL$path")
        val connection = url.openConnection() as HttpURLConnection
        
        return try {
            connection.apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = CONNECT_TIMEOUT
                readTimeout = READ_TIMEOUT
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                
                token?.let { setRequestProperty("Authorization", "Bearer $it") }
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(json.toString())
                writer.flush()
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

