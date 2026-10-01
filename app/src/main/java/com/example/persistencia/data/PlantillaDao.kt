package com.example.persistencia.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantillaDao {

    @Query("SELECT * FROM plantillas ORDER BY categoria COLLATE NOCASE, titulo COLLATE NOCASE")
    fun observeAll(): Flow<List<Plantilla>>

    @Query("SELECT MAX(last_updated) FROM plantillas")
    fun observeLastUpdate(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<Plantilla>)

    @Query("DELETE FROM plantillas WHERE id NOT IN (:ids)")
    suspend fun deleteNotIn(ids: List<Int>)

    @Query("DELETE FROM plantillas")
    suspend fun clear()
}