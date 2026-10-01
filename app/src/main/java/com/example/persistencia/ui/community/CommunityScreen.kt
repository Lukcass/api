package com.example.persistencia.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.persistencia.remote.MockApi
import com.example.persistencia.ui.components.EmptyTasksView
import com.example.persistencia.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommunityScreen(vm: CommunityViewModel = viewModel()) {
    val tasks by vm.tasks.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { vm.refresh() }, enabled = !loading) {
                Icon(Icons.Filled.Refresh, contentDescription = "Actualizar", tint = AppColors.AccentStrong)
            }
            Text(
                text = "Comunidad",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = AppColors.Ink
            )
        }

        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = AppColors.Accent)

        Text(
            text = "Tareas recientes de todos los usuarios",
            fontSize = 12.sp,
            color = AppColors.InkSoft,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        error?.let {
            Text(
                text = it,
                fontSize = 13.sp,
                color = AppColors.Danger,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (tasks.isEmpty() && !loading) {
                EmptyTasksView(
                    if (error != null) "No se pudo cargar la comunidad."
                    else "Aún no hay tareas en la comunidad."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tasks, key = { it.remoteId }) { CommunityCard(it) }
                }
            }
        }
    }
}

@Composable
private fun CommunityCard(t: MockApi.CommunityTask) {
    Surface(shape = RoundedCornerShape(16.dp), color = AppColors.CardWhite) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "@${t.username}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Accent
                )
                Spacer(Modifier.weight(1f))
                Text(
                    if (t.estadoCompletado) "Hecha" else "Pendiente",
                    fontSize = 12.sp,
                    color = if (t.estadoCompletado) AppColors.Accent else AppColors.InkSoft
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                t.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.Ink,
                textDecoration = if (t.estadoCompletado) TextDecoration.LineThrough else null
            )
            if (t.fechaCreacion > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(t.fechaCreacion)),
                    fontSize = 11.sp,
                    color = AppColors.InkFaint
                )
            }
        }
    }
}