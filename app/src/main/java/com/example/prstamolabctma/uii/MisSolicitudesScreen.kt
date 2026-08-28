package com.example.prstamolabctma.uii

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prstamolabctma.viewmodel.PrestamoViewModel
import com.example.prstamolabctma.model.EstadoSolicitud

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(navController: NavController, viewModel: PrestamoViewModel) {
    val solicitudes = viewModel.solicitudes.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis solicitudes") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            solicitudes.value.forEach { solicitud ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("solicitudDetalle/${solicitud.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Solicitud #${solicitud.id} - Equipo ${solicitud.equipoId}")
                        Text("Estado: ${solicitud.estado}")

                        if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.cancelarSolicitud(solicitud.id) }) {
                                Text("Cancelar")
                            }
                        }
                    }
                }
            }
        }
    }
}