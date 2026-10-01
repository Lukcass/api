package com.example.persistencia.data

import androidx.room.withTransaction
import com.example.persistencia.remote.ProductApi
import kotlinx.coroutines.flow.Flow

/**
 * Fuente única de verdad = Room. La UI nunca lee directamente de la red:
 * la red solo actualiza la base local y la UI reacciona al Flow.
 */
class ProductRepository(private val db: CatalogDatabase) {

    private val dao = db.productDao()

    val products: Flow<List<Product>> = dao.observeAll()
    val lastSync: Flow<Long?> = dao.observeLastUpdate()

    /** Descarga el catálogo y reemplaza la copia local en una sola transacción. */
    suspend fun refresh() {
        val remote = ProductApi.fetchProducts()
        db.withTransaction {
            if (remote.isEmpty()) {
                dao.clear()
            } else {
                dao.upsertAll(remote)
                dao.deleteNotIn(remote.map { it.id }) // elimina los que ya no existen en la nube
            }
        }
    }
}