package com.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class NoaaRepository {
    suspend fun getLatestKpIndex(): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://services.swpc.noaa.gov/products/noaa-planetary-k-index.json")
            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)
                if (jsonArray.length() > 1) {
                    val latestEntry = jsonArray.getJSONArray(jsonArray.length() - 1)
                    latestEntry.getString(1) // Kp value is at index 1
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
