package com.example.prstamolabctma.ui.misprestamos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prstamolabctma.model.EstadoSolicitud
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (val state = solicitudUiState) {
                    is UiState.Loading -> {
                        LoadingState(mensaje = "Cargando detalle de la solicitud...")
                    }

                    is UiState.Empty -> {
                        EmptyState(mensaje = "La solicitud requerida no existe.")
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

                        if (sol.estado == EstadoSolicitud.SOLICITADA) {
                            Button(
                                onClick = {
                                    viewModel.cancelarSolicitud(sol.id)
                                    onBack()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancelar solicitud")
                            }
                        }
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
