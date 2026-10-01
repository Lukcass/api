package com.example.persistencia.ui.theme

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.persistencia.data.Task
import com.example.persistencia.data.TaskDatabase
import com.example.persistencia.data.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application,
    private val username: String
) : AndroidViewModel(application) {

    private val repository =
        TaskRepository(TaskDatabase.getDatabase(application).taskDao(), username)

    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val connectivityManager =
        application.getSystemService(ConnectivityManager::class.java)

    private val isOnline = MutableStateFlow(false)

    // CONFLATED: si piden sincronizar mientras ya hay una en curso, queda UNA solicitud
    // pendiente que se atiende al terminar. Así no se pierden cambios hechos durante la subida.
    private val syncRequests = Channel<Unit>(Channel.CONFLATED)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            isOnline.value = true
            syncTasks() // volvió la red: sincronizar lo pendiente
        }

        override fun onLost(network: Network) {
            isOnline.value = false
        }
    }

    init {
        isOnline.value = hasInternet()
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        viewModelScope.launch {
            repository.adoptLocalTasks() // tareas previas al login pasan a este usuario
            syncRequests.trySend(Unit)
            for (request in syncRequests) {
                if (!isOnline.value) continue
                try {
                    repository.sync()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Sin red o error del servidor: las tareas siguen "pendientes" y se reintentan luego
                }
            }
        }
    }

    private fun hasInternet(): Boolean {
        val caps = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun addTask(titulo: String, descripcion: String) {
        if (titulo.isBlank()) return
        viewModelScope.launch {
            repository.insert(Task(titulo = titulo, descripcion = descripcion))
            syncTasks()
        }
    }

    fun toggleTaskState(task: Task) {
        viewModelScope.launch {
            repository.update(task.id) { it.copy(estadoCompletado = !it.estadoCompletado) }
            syncTasks()
        }
    }

    fun updateTask(task: Task, nuevoTitulo: String, nuevaDescripcion: String) {
        if (nuevoTitulo.isBlank()) return
        viewModelScope.launch {
            repository.update(task.id) { it.copy(titulo = nuevoTitulo, descripcion = nuevaDescripcion) }
            syncTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task.id)
            syncTasks()
        }
    }

    fun syncTasks() {
        syncRequests.trySend(Unit)
    }

    override fun onCleared() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        super.onCleared()
    }
}