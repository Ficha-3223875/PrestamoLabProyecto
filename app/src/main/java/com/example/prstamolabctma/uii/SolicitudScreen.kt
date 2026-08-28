package com.example.prstamolabctma.uii

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(navController: NavController, equipoId: Int, viewModel: PrestamoViewModel) {
    var ambiente by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Solicitud de préstamo") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = ambiente,
                onValueChange = { ambiente = it },
                label = { Text("Ambiente/Destino") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = proposito,
                onValueChange = { proposito = it },
                label = { Text("Propósito") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración (horas)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val solicitud = SolicitudPrestamo(
                    id = viewModel.solicitudes.value.size + 1,
                    equipoId = equipoId,
                    ambienteDestino = ambiente,
                    proposito = proposito,
                    duracionHoras = duracion.toIntOrNull() ?: 1,
                    estado = EstadoSolicitud.SOLICITADA
                )
                viewModel.crearSolicitud(solicitud)
                navController.navigate("misSolicitudes")
            }) {
                Text("Guardar solicitud")
            }
        }
    }
}