package com.example.prstamolabctma.uii

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(navController: NavController, viewModel: PrestamoViewModel) {
    val equipos = viewModel.equipos.collectAsState()
    val filtroSeleccionado = viewModel.categoriaFiltro.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Catálogo de equipos") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Filtros de categoría persistentes con DataStore
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroSeleccionado.value == null,
                    onClick = { viewModel.seleccionarCategoriaFiltro(null) },
                    label = { Text("TODAS") }
                )
                CategoriaEquipo.entries.forEach { categoria ->
                    FilterChip(
                        selected = filtroSeleccionado.value == categoria,
                        onClick = { viewModel.seleccionarCategoriaFiltro(categoria) },
                        label = { Text(categoria.name) }
                    )
                }
            }

            if (equipos.value.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No hay equipos disponibles en esta categoría")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(equipos.value) { equipo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { navController.navigate("equipoDetalle/${equipo.id}") }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(equipo.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodySmall)
                                Text("Estado: ${equipo.estado}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
