package com.example.persistencia.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    @ColumnInfo(name = "estado_completado")
    val estadoCompletado: Boolean = false,
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false,
    // Dueño de la tarea (nombre de usuario). "" = tarea creada antes del login.
    @ColumnInfo(name = "username")
    val username: String = "",
    // id de la tarea en MockAPI (null = aún no subida)
    @ColumnInfo(name = "remote_id")
    val remoteId: String? = null,
    // Borrado local pendiente de reflejarse en la nube
    @ColumnInfo(name = "pending_delete")
    val pendingDelete: Boolean = false
)