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
        // Respuesta vacía (por ejemplo un 404 por URL mal escrita): se conserva la caché local
        if (remote.isEmpty()) return
        db.withTransaction {
            dao.upsertAll(remote)
            dao.deleteNotIn(remote.map { it.id })
        }
    }
}