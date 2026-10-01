package com.example.persistencia.data

import androidx.room.withTransaction
import com.example.persistencia.remote.MockApi
import kotlinx.coroutines.flow.Flow

/** Room es la fuente de verdad: la UI lee de aquí y la red solo actualiza la tabla. */
class PlantillaRepository(private val db: CatalogDatabase) {

    private val dao = db.plantillaDao()

    val plantillas: Flow<List<Plantilla>> = dao.observeAll()
    val lastSync: Flow<Long?> = dao.observeLastUpdate()

    suspend fun refresh() {
        val remote = MockApi.fetchPlantillas()
        db.withTransaction {
            if (remote.isEmpty()) {
                dao.clear()
            } else {
                dao.upsertAll(remote)
                dao.deleteNotIn(remote.map { it.id })
            }
        }
    }
}