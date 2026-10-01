package com.example.persistencia.ui.auth

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.persistencia.data.SessionManager
import com.example.persistencia.remote.MockApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, REGISTER }

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val username: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val session = SessionManager(application)

    private val _currentUser = MutableStateFlow(session.username)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun setMode(mode: AuthMode) {
        uiState = uiState.copy(mode = mode, error = null)
    }

    fun onUsernameChange(value: String) {
        val clean = value.lowercase()
            .filter { it in 'a'..'z' || it in '0'..'9' || it == '_' || it == '.' }
            .take(20)
        uiState = uiState.copy(username = clean, error = null)
    }

    fun submit() {
        if (uiState.loading) return
        val name = uiState.username.trim()
        if (!USERNAME_REGEX.matches(name)) {
            uiState = uiState.copy(error = "Usa de 3 a 20 caracteres: letras, números, _ o .")
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            try {
                val exists = MockApi.userExists(name)
                if (uiState.mode == AuthMode.LOGIN) {
                    if (!exists) {
                        uiState = uiState.copy(loading = false, error = "Ese usuario no existe. Puedes crearlo.")
                        return@launch
                    }
                } else {
                    if (exists) {
                        uiState = uiState.copy(loading = false, error = "Ese nombre ya está en uso. Elige otro.")
                        return@launch
                    }
                    MockApi.createUser(name)
                }
                session.save(name)
                uiState = AuthUiState()
                _currentUser.value = name
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = "No se pudo conectar. Revisa tu internet e inténtalo de nuevo."
                )
            }
        }
    }

    fun logout() {
        session.clear()
        _currentUser.value = null
    }

    private companion object {
        val USERNAME_REGEX = Regex("^[a-z0-9_.]{3,20}$")
    }
}