package com.example.persistencia.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.persistencia.data.Plantilla
import com.example.persistencia.ui.components.EmptyTasksView
import com.example.persistencia.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CatalogScreen(
    onAddToTasks: (titulo: String, descripcion: String) -> Unit,
    vm: CatalogViewModel = viewModel()
) {
    val plantillas by vm.plantillas.collectAsState()
    val refreshing by vm.isRefreshing.collectAsState()
    val error by vm.error.collectAsState()
    val lastSync by vm.lastSync.collectAsState()
    var added by remember { mutableStateOf(setOf<Int>()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { vm.refresh() }, enabled = !refreshing) {
                Icon(Icons.Filled.Refresh, contentDescription = "Actualizar", tint = AppColors.AccentStrong)
            }
            Text(
                text = "Plantillas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = AppColors.Ink
            )
        }

        if (refreshing) LinearProgressIndicator(Modifier.fillMaxWidth(), color = AppColors.Accent)

        val fecha = lastSync?.let { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(it)) }
        Text(
            text = if (fecha != null) "Ideas de tareas de la nube · guardadas el $fecha"
            else "Ideas de tareas de la nube",
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
            if (plantillas.isEmpty() && !refreshing) {
                EmptyTasksView("Aún no hay plantillas. Conéctate a internet y pulsa actualizar.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(plantillas, key = { it.id }) { p ->
                        PlantillaCard(
                            p = p,
                            isAdded = p.id in added,
                            onAdd = {
                                onAddToTasks(p.titulo, p.descripcion)
                                added = added + p.id
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlantillaCard(p: Plantilla, isAdded: Boolean, onAdd: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = AppColors.CardWhite) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Surface(shape = RoundedCornerShape(50), color = AppColors.AccentSoft) {
                Text(
                    p.categoria,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.AccentStrong,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(p.titulo, style = MaterialTheme.typography.titleMedium, color = AppColors.Ink)
            if (p.descripcion.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(p.descripcion, fontSize = 13.sp, color = AppColors.InkSoft)
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onAdd,
                enabled = !isAdded,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.AccentStrong,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    disabledContainerColor = AppColors.AccentSoft,
                    disabledContentColor = AppColors.AccentStrong
                )
            ) {
                Text(if (isAdded) "Agregada ✓" else "Agregar a mis tareas", fontSize = 12.sp)
            }
        }
    }
}