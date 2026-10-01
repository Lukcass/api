package com.example.persistencia.ui.community

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.persistencia.remote.MockApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommunityViewModel(application: Application) : AndroidViewModel(application) {

    private val _tasks = MutableStateFlow<List<MockApi.CommunityTask>>(emptyList())
    val tasks: StateFlow<List<MockApi.CommunityTask>> = _tasks.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        if (_loading.value) return
        viewModelScope.launch {
            _loading.value = true
            try {
                _tasks.value = MockApi.fetchCommunityTasks()
                _error.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo cargar (${e.message ?: "sin conexión"})."
            } finally {
                _loading.value = false
            }
        }
    }
}