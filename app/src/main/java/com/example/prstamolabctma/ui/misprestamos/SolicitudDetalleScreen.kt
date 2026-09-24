package com.example.prstamolabctma.ui.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.ui.common.EmptyState
import com.example.prstamolabctma.ui.common.ErrorState
import com.example.prstamolabctma.ui.common.LoadingState
import com.example.prstamolabctma.ui.common.UiState
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitudId: Int,
    viewModel: PrestamoViewModel,
    onBack: () -> Unit
) {
    val solicitudFlow = remember(solicitudId) { viewModel.obtenerSolicitudState(solicitudId) }
    val solicitudUiState by solicitudFlow.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de solicitud") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = solicitudUiState) {
                is UiState.Loading -> {
                    LoadingState(mensaje = "Cargando detalle...")
                }

                is UiState.Empty -> {
                    EmptyState(mensaje = "La solicitud seleccionada no existe.")
                }

                is UiState.Error -> {
                    ErrorState(mensaje = state.message)
                }

                is UiState.Content<SolicitudPrestamo> -> {
                    val sol = state.data

                    Text(
                        text = "Solicitud #${sol.id}",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(text = "ID Equipo: ${sol.equipoId}")

                    Text(text = "Ambiente / Destino: ${sol.ambienteDestino}")

                    Text(text = "Propósito: ${sol.proposito}")

                    Text(text = "Duración: ${sol.duracionHoras} hora(s)")

                    Text(text = "Estado: ${sol.estado}")

                    // -------------------------------------------------------------
                    // EVIDENCIA FOTOGRÁFICA Y UBICACIÓN GUARDADAS EN ROOM
                    // -------------------------------------------------------------
                    if (sol.evidenciaUri != null || (sol.latitud != null && sol.longitud != null)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Evidencia y Ubicación Registradas",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                sol.evidenciaUri?.let { uriString ->
                                    Text(
                                        text = "Foto adjunta:",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    AsyncImage(
                                        model = uriString,
                                        contentDescription = "Foto de evidencia guardada",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                if (sol.latitud != null && sol.longitud != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Coordenadas GPS guardadas:",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Latitud: ${sol.latitud}\nLongitud: ${sol.longitud}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.cancelarSolicitud(sol.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar solicitud")
                    }
                }
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}