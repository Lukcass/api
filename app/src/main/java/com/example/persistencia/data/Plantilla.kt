package com.example.persistencia.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Copia liviana de una plantilla de tarea del catálogo en la nube. */
@Entity(tableName = "plantillas")
data class Plantilla(
    @PrimaryKey val id: Int,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)