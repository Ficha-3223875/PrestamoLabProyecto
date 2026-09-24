package com.example.prstamolabctma.uii

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.viewmodel.PrestamoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(navController: NavController, viewModel: PrestamoViewModel) {
    val solicitudes = viewModel.solicitudes.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis solicitudes") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (solicitudes.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No tienes solicitudes registradas")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                items(solicitudes.value) { solicitud ->
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
                                    onClick = {
                                        viewModel.cancelarSolicitud(solicitud.id) { result ->
                                            if (result.isFailure) {
                                                val msg = result.exceptionOrNull()?.message ?: "Error al cancelar"
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(msg)
                                                }
                                            }
                                        }
                                    },
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
    }
}
