package com.example.persistencia.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.persistencia.data.Product
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(vm: ProductViewModel = viewModel()) {
    val products by vm.products.collectAsState()
    val refreshing by vm.isRefreshing.collectAsState()
    val error by vm.error.collectAsState()
    val lastSync by vm.lastSync.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo (nube)") },
                navigationIcon = {
                    IconButton(onClick = { vm.refresh() }, enabled = !refreshing) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (refreshing) LinearProgressIndicator(Modifier.fillMaxWidth())

            error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            lastSync?.let {
                val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(it))
                Text(
                    "Última actualización: $fecha  ·  ${products.size} productos",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (products.isEmpty() && !refreshing) {
                Text(
                    "Aún no hay datos. Conéctate a internet y pulsa actualizar.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products, key = { it.id }) { ProductRow(it) }
                }
            }
        }
    }
}

@Composable
private fun ProductRow(p: Product) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(p.title, style = MaterialTheme.typography.titleMedium)
            Text(p.category, style = MaterialTheme.typography.labelMedium)
            Text(p.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$ ${"%.2f".format(p.price)}", style = MaterialTheme.typography.titleSmall)
                Text("Stock: ${p.stock}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}