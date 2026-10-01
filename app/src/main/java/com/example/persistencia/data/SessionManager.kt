package com.example.persistencia.data

import android.content.Context

/** Guarda en el dispositivo quién tiene la sesión abierta. */
class SessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("session", Context.MODE_PRIVATE)

    val username: String?
        get() = prefs.getString(KEY_USER, null)

    fun save(username: String) = prefs.edit().putString(KEY_USER, username).apply()

    fun clear() = prefs.edit().remove(KEY_USER).apply()

    private companion object {
        const val KEY_USER = "username"
    }
}