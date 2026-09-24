package com.example.prstamolabctma.uii

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.ui.state.UiState
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(navController: NavController, equipoId: Int, viewModel: PrestamoViewModel) {
    val equiposState by viewModel.equiposUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle del equipo") }) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            when (val state = equiposState) {
                is UiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Contenido -> {
                    val equipo = state.datos.find { it.id == equipoId }
                    if (equipo != null) {
                        Column {
                            Text("Nombre: ${equipo.nombre}", style = MaterialTheme.typography.titleMedium)
                            Text("Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodyMedium)
                            Text("Estado: ${equipo.estado}", style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { navController.navigate("solicitar/${equipo.id}") },
                                enabled = equipo.estado == EstadoEquipo.DISPONIBLE
                            ) {
                                Text("Reservar equipo")
                            }
                        }
                    } else {
                        Text("Equipo no encontrado")
                    }
                }
                else -> {
                    Text("Equipo no disponible")
                }
            }
        }
    }
}
