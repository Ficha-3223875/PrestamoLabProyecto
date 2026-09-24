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
import com.example.prstamolabctma.ui.state.UiState
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(navController: NavController, solicitudId: Int, viewModel: PrestamoViewModel) {
    val solicitudesState by viewModel.solicitudesUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de solicitud $solicitudId") }) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            when (val state = solicitudesState) {
                is UiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Contenido -> {
                    val solicitud = state.datos.find { it.id == solicitudId }
                    if (solicitud != null) {
                        Column {
                            Text("Equipo ID: ${solicitud.equipoId}", style = MaterialTheme.typography.titleMedium)
                            Text("Ambiente destino: ${solicitud.ambienteDestino}")
                            Text("Propósito: ${solicitud.proposito}")
                            Text("Duración: ${solicitud.duracionHoras} horas")
                            Text("Estado: ${solicitud.estado}")
                        }
                    } else {
                        Text("Solicitud no encontrada")
                    }
                }
                else -> {
                    Text("Detalle no disponible")
                }
            }
        }
    }
}
