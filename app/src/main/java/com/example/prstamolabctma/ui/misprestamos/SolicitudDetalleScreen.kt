package com.example.prstamolabctma.ui.misprestamos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitudId: Int,
    viewModel: PrestamoViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val solicitud = viewModel.obtenerSolicitud(solicitudId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Detalle de solicitud")
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            if (solicitud == null) {

                Text(
                    text = "La solicitud no existe."
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = onBack
                ) {
                    Text("Volver")
                }

            } else {

                Text(
                    text = "Solicitud #${solicitud.id}",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "Equipo ID: ${solicitud.equipoId}"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Destino: ${solicitud.ambienteDestino}"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Propósito: ${solicitud.proposito}"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Duración: ${solicitud.duracionHoras} horas"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Estado: ${solicitud.estado}"
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                if (solicitud.estado == EstadoSolicitud.SOLICITADA) {

                    Button(
                        onClick = {
                            viewModel.cancelarSolicitud(solicitud.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar solicitud")
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }

                uiState.mensaje?.let { mensaje ->

                    Text(
                        text = mensaje
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
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
}