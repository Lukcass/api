package com.example.persistencia.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.persistencia.data.CatalogDatabase
import com.example.persistencia.data.Plantilla
import com.example.persistencia.data.PlantillaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlantillaRepository(CatalogDatabase.getDatabase(application))

    // Lo cacheado se emite de inmediato, sin esperar a la red
    val plantillas: StateFlow<List<Plantilla>> = repository.plantillas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lastSync: StateFlow<Long?> = repository.lastSync
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        refresh() // al abrir: se muestra la caché y se actualiza en segundo plano
    }

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.refresh()
                _error.value = null
            } catch (e: CancellationException) {
                throw e
            }
            catch (e: Exception) {
                _error.value = "No se pudo actualizar (${e.message ?: "sin conexión"}). Mostrando datos guardados."
            } catch (e: Exception) {
                android.util.Log.e("Catalog", "refresh falló", e)
                _error.value = "No se pudo actualizar (${e.message ?: e.javaClass.simpleName}). Mostrando datos guardados."
            }

            finally {
                _isRefreshing.value = false
            }
        }
    }
}