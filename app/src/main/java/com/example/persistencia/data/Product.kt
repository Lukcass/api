package com.example.persistencia.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Copia liviana de un producto remoto. El id es el de la API (no autogenerado),
 * para poder actualizar el mismo registro en cada sincronización.
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val thumbnail: String,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)