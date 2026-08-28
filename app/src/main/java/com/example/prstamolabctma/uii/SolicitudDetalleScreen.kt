package com.example.prstamolabctma.uii

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(navController: NavController, solicitudId: Int, viewModel: PrestamoViewModel) {
    val solicitudes = viewModel.solicitudes.collectAsState()
    val solicitud = solicitudes.value.find { it.id == solicitudId }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de solicitud $solicitudId") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            if (solicitud != null) {
                Text("Equipo ID: ${solicitud.equipoId}")
                Text("Ambiente destino: ${solicitud.ambienteDestino}")
                Text("Propósito: ${solicitud.proposito}")
                Text("Duración: ${solicitud.duracionHoras} horas")
                Text("Estado: ${solicitud.estado}")
            } else {
                Text("Solicitud no encontrada")
            }
        }
    }
}