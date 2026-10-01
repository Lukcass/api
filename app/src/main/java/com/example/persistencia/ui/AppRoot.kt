package com.example.persistencia.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.persistencia.ui.auth.AuthViewModel
import com.example.persistencia.ui.auth.LoginScreen
import com.example.persistencia.ui.navigation.TaskNavHost

/** Si no hay sesión se muestra el login; si la hay, la app de tareas del usuario. */
@Composable
fun AppRoot(authVm: AuthViewModel = viewModel()) {
    val user by authVm.currentUser.collectAsState()
    val current = user
    if (current == null) {
        LoginScreen(authVm)
    } else {
        TaskNavHost(username = current, onLogout = authVm::logout)
    }
}