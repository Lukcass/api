package com.example.persistencia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Versión 2: la entidad cambió de Product a Plantilla. Como es solo una CACHÉ que se puede
// volver a descargar, es válido recrear la base en lugar de migrarla.
@Database(entities = [Plantilla::class], version = 2, exportSchema = false)
abstract class CatalogDatabase : RoomDatabase() {

    abstract fun plantillaDao(): PlantillaDao

    companion object {
        @Volatile
        private var INSTANCE: CatalogDatabase? = null

        fun getDatabase(context: Context): CatalogDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CatalogDatabase::class.java,
                    "catalog_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}