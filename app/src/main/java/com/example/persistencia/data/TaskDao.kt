package com.example.persistencia.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY fecha_creacion DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM tasks WHERE is_synced = 0")
    suspend fun getUnsyncedTasks(): List<Task>

    // ---- Consultas por usuario (login) ----

    @Query("SELECT * FROM tasks WHERE username = :user AND pending_delete = 0 ORDER BY fecha_creacion DESC")
    fun observeByUser(user: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE username = :user")
    suspend fun getAllByUser(user: String): List<Task>

    @Query("SELECT * FROM tasks WHERE username = :user AND is_synced = 0")
    suspend fun getUnsyncedByUser(user: String): List<Task>

    // Las tareas creadas antes del login pasan a pertenecer al usuario que entra
    @Query("UPDATE tasks SET username = :user, is_synced = 0 WHERE username = ''")
    suspend fun adoptOrphans(user: String)
}