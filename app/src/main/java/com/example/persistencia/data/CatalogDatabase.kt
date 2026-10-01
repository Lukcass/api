package com.example.persistencia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class CatalogDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: CatalogDatabase? = null

        fun getDatabase(context: Context): CatalogDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CatalogDatabase::class.java,
                    "catalog_database"
                ).build().also { INSTANCE = it }
            }
    }
}