package com.example.prstamolabctma.uii

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.ui.state.OperationState
import com.example.prstamolabctma.ui.state.UiState
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(navController: NavController, viewModel: PrestamoViewModel) {
    val equiposState by viewModel.equiposUiState.collectAsStateWithLifecycle()
    val filtroSeleccionado by viewModel.categoriaFiltro.collectAsStateWithLifecycle()
    val refreshState by viewModel.refreshState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(refreshState) {
        when (val state = refreshState) {
            is OperationState.Exitosa -> {
                snackbarHostState.showSnackbar(state.mensaje)
                viewModel.resetearEstadoRefresh()
            }
            is OperationState.Fallida -> {
                snackbarHostState.showSnackbar(state.mensaje)
                viewModel.resetearEstadoRefresh()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de equipos") },
                actions = {
                    TextButton(
                        onClick = { viewModel.refrescarDatos() },
                        enabled = refreshState !is OperationState.EnCurso
                    ) {
                        if (refreshState is OperationState.EnCurso) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Sincronizar ↻")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Filtros de categoría
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroSeleccionado == null,
                    onClick = { viewModel.seleccionarCategoriaFiltro(null) },
                    label = { Text("TODAS") }
                )
                CategoriaEquipo.entries.forEach { categoria ->
                    FilterChip(
                        selected = filtroSeleccionado == categoria,
                        onClick = { viewModel.seleccionarCategoriaFiltro(categoria) },
                        label = { Text(categoria.name) }
                    )
                }
            }

            when (val state = equiposState) {
                is UiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Vacio -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay equipos disponibles en esta categoría", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                is UiState.Contenido -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.datos) { equipo ->
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
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.mensaje}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
