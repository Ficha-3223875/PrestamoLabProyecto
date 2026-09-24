package com.example.prstamolabctma.uii

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.ui.state.OperationState
import com.example.prstamolabctma.ui.state.UiState
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(navController: NavController, viewModel: PrestamoViewModel) {
    val solicitudesState by viewModel.solicitudesUiState.collectAsStateWithLifecycle()
    val operacionState by viewModel.operacionState.collectAsStateWithLifecycle()
    val refreshState by viewModel.refreshState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(operacionState) {
        when (val state = operacionState) {
            is OperationState.Exitosa -> {
                snackbarHostState.showSnackbar(state.mensaje)
                viewModel.resetearEstadoOperacion()
            }
            is OperationState.Fallida -> {
                snackbarHostState.showSnackbar(state.mensaje)
                viewModel.resetearEstadoOperacion()
            }
            else -> {}
        }
    }

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
                title = { Text("Mis solicitudes") },
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
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = solicitudesState) {
                is UiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Vacio -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tienes solicitudes registradas", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                is UiState.Contenido -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.datos) { solicitud ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable { navController.navigate("solicitudDetalle/${solicitud.id}") }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Solicitud #${solicitud.id} - Equipo ID: ${solicitud.equipoId}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("Ambiente: ${solicitud.ambienteDestino}")
                                    Text("Estado: ${solicitud.estado}")

                                    if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { viewModel.cancelarSolicitud(solicitud.id) },
                                            enabled = operacionState !is OperationState.EnCurso,
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Text("Cancelar solicitud")
                                        }
                                    }
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
