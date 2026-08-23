package com.example.prstamolabctma.ui.misprestamos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.navigation.Destino
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@Composable
fun MisSolicitudesScreen(
    viewModel: PrestamoViewModel,
    navController: NavHostController
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mis solicitudes")

        // Botón fijo de volver al inicio (siempre visible)
        Button(
            onClick = { navController.navigate(Destino.Catalogo.ruta) },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Volver al inicio")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            items(state.solicitudes) { solicitud ->
                val equipo = state.equipos.find { it.id == solicitud.equipoId }

                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Solicitud #${solicitud.id}")
                    Text("Producto: ${equipo?.nombre ?: "Desconocido"}")
                    Text("Destino: ${solicitud.ambienteDestino}")
                    Text("Propósito: ${solicitud.proposito}")
                    Text("Duración: ${solicitud.duracionHoras} horas")
                    Text("Estado: ${solicitud.estado}")

                    if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                        Button(
                            onClick = { viewModel.cancelarSolicitud(solicitud.id) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}
