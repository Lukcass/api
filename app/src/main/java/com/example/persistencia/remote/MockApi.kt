package com.example.persistencia.remote

import com.example.persistencia.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** Cliente REST de MockAPI (usuarios y tareas). Todo corre en Dispatchers.IO. */
object MockApi {

    // TODO: pega tu URL base de MockAPI, sin "/" al final
    const val BASE_URL = "https://6abdb448c4d5ac54830105d1.mockapi.io/api/v1"

    data class RemoteTask(
        val remoteId: String,
        val titulo: String,
        val descripcion: String,
        val estadoCompletado: Boolean,
        val fechaCreacion: Long
    )

    data class CommunityTask(
        val remoteId: String,
        val username: String,
        val titulo: String,
        val estadoCompletado: Boolean,
        val fechaCreacion: Long
    )

    // ---------- Usuarios ----------

    suspend fun userExists(username: String): Boolean {
        // MockAPI filtra por coincidencia parcial, por eso se compara el nombre exacto
        val body = call("GET", "/users?username=${enc(username)}") ?: return false
        val arr = JSONArray(body)
        return (0 until arr.length()).any {
            arr.getJSONObject(it).optString("username").equals(username, ignoreCase = true)
        }
    }

    suspend fun createUser(username: String) {
        call("POST", "/users", JSONObject().put("username", username))
    }

    // ---------- Tareas ----------

    suspend fun fetchTasks(username: String): List<RemoteTask> {
        val body = call("GET", "/tasks?username=${enc(username)}") ?: return emptyList()
        val arr = JSONArray(body)
        return (0 until arr.length())
            .map { arr.getJSONObject(it) }
            .filter { it.optString("username").equals(username, ignoreCase = true) }
            .map {
                RemoteTask(
                    remoteId = it.getString("id"),
                    titulo = it.optString("titulo", ""),
                    descripcion = it.optString("descripcion", ""),
                    estadoCompletado = it.optBoolean("estadoCompletado", false),
                    fechaCreacion = it.optLong("fechaCreacion", System.currentTimeMillis())
                )
            }
    }

    /** Últimas 50 tareas de TODOS los usuarios (solo lectura). No incluye la descripción. */
    suspend fun fetchCommunityTasks(): List<CommunityTask> {
        val body = call("GET", "/tasks?sortBy=fechaCreacion&order=desc&page=1&limit=50")
            ?: return emptyList()
        val arr = JSONArray(body)
        return (0 until arr.length()).map { arr.getJSONObject(it) }.map {
            CommunityTask(
                remoteId = it.getString("id"),
                username = it.optString("username", ""),
                titulo = it.optString("titulo", ""),
                estadoCompletado = it.optBoolean("estadoCompletado", false),
                fechaCreacion = it.optLong("fechaCreacion", 0L)
            )
        }
    }

    /** @return el id asignado por MockAPI */
    suspend fun createTask(t: Task): String {
        val body = call("POST", "/tasks", toJson(t)) ?: throw IOException("Respuesta vacía del servidor")
        return JSONObject(body).getString("id")
    }

    /** @return false si la tarea ya no existe en la nube (404) */
    suspend fun updateTask(remoteId: String, t: Task): Boolean =
        call("PUT", "/tasks/$remoteId", toJson(t)) != null

    suspend fun deleteTask(remoteId: String) {
        call("DELETE", "/tasks/$remoteId") // 404 = ya estaba borrada, no es error
    }

    // ---------- HTTP ----------

    private fun toJson(t: Task) = JSONObject()
        .put("username", t.username)
        .put("titulo", t.titulo)
        .put("descripcion", t.descripcion)
        .put("estadoCompletado", t.estadoCompletado)
        .put("fechaCreacion", t.fechaCreacion)

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")

    /** Devuelve el cuerpo de la respuesta, o null si es 404. Lanza IOException en otros errores. */
    private suspend fun call(method: String, path: String, json: JSONObject? = null): String? =
        withContext(Dispatchers.IO) {
            val conn = URL(BASE_URL + path).openConnection() as HttpURLConnection
            try {
                conn.requestMethod = method
                conn.connectTimeout = 8_000
                conn.readTimeout = 8_000
                conn.setRequestProperty("Accept", "application/json")
                if (json != null) {
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    conn.outputStream.use { it.write(json.toString().toByteArray(Charsets.UTF_8)) }
                }
                val code = conn.responseCode
                when {
                    code == 404 -> null // MockAPI responde 404 cuando un filtro no encuentra nada
                    code in 200..299 -> conn.inputStream.bufferedReader().use { it.readText() }
                    else -> throw IOException("Error HTTP $code")
                }
            } finally {
                conn.disconnect()
            }
        }
}