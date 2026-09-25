package com.example.prestamolabctma.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.SolicitudPrestamo
import com.example.prestamolabctma.viewmodel.EstadoOperacion
import com.example.prestamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    equipo: Equipo?,
    viewModel: PrestamoViewModel,
    onBack: () -> Unit
) {
    // Recolección consciente del ciclo de vida (Semana 7)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de solicitud") },
                navigationIcon = {
                    OutlinedButton(onClick = onBack) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { padding ->

        if (solicitud == null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
            ) {
                Text(
                    text = "La solicitud no existe.",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            return@Scaffold
        }

        val solicitudActualizada = viewModel.solicitud(solicitud.id) ?: solicitud
        val estaEnCurso = uiState.estadoOperacion is EstadoOperacion.EnCurso

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Solicitud #${solicitudActualizada.id}",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Equipo: ${equipo?.nombre ?: "Equipo desconocido"}"
            )

            Text(
                text = "Destino: ${solicitudActualizada.ambienteDestino}"
            )

            Text(
                text = "Propósito: ${solicitudActualizada.proposito}"
            )

            Text(
                text = "Duración: ${solicitudActualizada.duracionHoras} horas"
            )

            Text(
                text = "Estado: ${solicitudActualizada.estado}"
            )

            if (solicitudActualizada.estado == EstadoSolicitud.SOLICITADA) {
                Button(
                    onClick = {
                        viewModel.cancelarSolicitud(solicitudActualizada.id)
                    },
                    enabled = !estaEnCurso,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (estaEnCurso) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Text("Cancelando...")
                        }
                    } else {
                        Text("Cancelar solicitud")
                    }
                }
            }

            if (solicitudActualizada.estado == EstadoSolicitud.CANCELADA) {
                Text(
                    text = "Esta solicitud ya fue cancelada.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
