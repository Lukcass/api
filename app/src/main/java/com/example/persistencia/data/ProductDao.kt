package com.example.persistencia.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY title COLLATE NOCASE")
    fun observeAll(): Flow<List<Product>>

    @Query("SELECT MAX(last_updated) FROM products")
    fun observeLastUpdate(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<Product>)

    @Query("DELETE FROM products WHERE id NOT IN (:ids)")
    suspend fun deleteNotIn(ids: List<Int>)

    @Query("DELETE FROM products")
    suspend fun clear()
}