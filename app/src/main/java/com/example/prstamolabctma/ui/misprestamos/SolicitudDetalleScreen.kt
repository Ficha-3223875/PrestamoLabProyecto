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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitudId: Int,
    viewModel: PrestamoViewModel,
    onBack: () -> Unit
) {
    var solicitud by remember { mutableStateOf<SolicitudPrestamo?>(null) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(solicitudId) {
        cargando = true
        viewModel.obtenerSolicitud(solicitudId) { resultado ->
            solicitud = resultado
            cargando = false
        }
    }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (cargando) {

                Text(
                    text = "Cargando solicitud...",
                    style = MaterialTheme.typography.bodyLarge
                )

            } else if (solicitud == null) {

                Text(
                    text = "La solicitud no existe.",
                    style = MaterialTheme.typography.titleLarge
                )

            } else {

                val sol = solicitud!!

                Text(
                    text = "Solicitud #${sol.id}",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "ID Equipo: ${sol.equipoId}"
                )

                Text(
                    text = "Ambiente / Destino: ${sol.ambienteDestino}"
                )

                Text(
                    text = "Propósito: ${sol.proposito}"
                )

                Text(
                    text = "Duración: ${sol.duracionHoras} hora(s)"
                )

                Text(
                    text = "Estado: ${sol.estado}"
                )

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

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}