package com.example.persistencia.remote

import com.example.persistencia.data.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Cliente REST mínimo (GET). Se ejecuta en Dispatchers.IO para no bloquear el hilo principal.
 * `select` y `limit` piden solo lo necesario: así la copia local es liviana.
 */
object ProductApi {

    // Para usar MockAPI cambia esta URL por la tuya, ej.: "https://XXXX.mockapi.io/api/v1/products"
    private const val URL_PRODUCTS =
        "https://dummyjson.com/products?limit=30&select=title,description,category,price,stock,thumbnail"

    suspend fun fetchProducts(): List<Product> = withContext(Dispatchers.IO) {
        val conn = URL(URL_PRODUCTS).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "GET"
            conn.connectTimeout = 8_000
            conn.readTimeout = 8_000
            conn.setRequestProperty("Accept", "application/json")

            val code = conn.responseCode
            if (code !in 200..299) throw IOException("Error HTTP $code")

            val body = conn.inputStream.bufferedReader().use { it.readText() }
            parse(body)
        } finally {
            conn.disconnect()
        }
    }

    /** Acepta tanto {"products":[...]} (DummyJSON) como un arreglo [...] en la raíz (MockAPI). */
    private fun parse(body: String): List<Product> {
        val text = body.trim()
        val array = if (text.startsWith("[")) JSONArray(text)
        else JSONObject(text).getJSONArray("products")

        val now = System.currentTimeMillis()
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            Product(
                id = o.getInt("id"),
                title = o.optString("title", o.optString("name", "Sin nombre")),
                description = o.optString("description", ""),
                category = o.optString("category", ""),
                price = o.optDouble("price", 0.0),
                stock = o.optInt("stock", 0),
                thumbnail = o.optString("thumbnail", o.optString("imageUrl", "")),
                lastUpdated = now
            )
        }
    }
}