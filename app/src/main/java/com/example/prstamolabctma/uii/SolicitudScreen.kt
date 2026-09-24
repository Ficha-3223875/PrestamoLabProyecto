package com.example.prstamolabctma.uii

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.ui.state.OperationState
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(navController: NavController, equipoId: Int, viewModel: PrestamoViewModel) {
    var ambiente by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    val operacionState by viewModel.operacionState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(operacionState) {
        when (val state = operacionState) {
            is OperationState.Exitosa -> {
                viewModel.resetearEstadoOperacion()
                navController.navigate("misSolicitudes") {
                    popUpTo("catalogo") { inclusive = false }
                }
            }
            is OperationState.Fallida -> {
                snackbarHostState.showSnackbar(state.mensaje)
                viewModel.resetearEstadoOperacion()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Solicitud de préstamo") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = ambiente,
                onValueChange = { ambiente = it },
                label = { Text("Ambiente/Destino") },
                modifier = Modifier.fillMaxWidth(),
                enabled = operacionState !is OperationState.EnCurso
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = proposito,
                onValueChange = { proposito = it },
                label = { Text("Propósito (10-180 caracteres)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = operacionState !is OperationState.EnCurso
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración (1-8 horas)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = operacionState !is OperationState.EnCurso
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val solicitud = SolicitudPrestamo(
                        id = 0,
                        equipoId = equipoId,
                        ambienteDestino = ambiente,
                        proposito = proposito,
                        duracionHoras = duracion.toIntOrNull() ?: 0,
                        estado = EstadoSolicitud.SOLICITADA
                    )
                    viewModel.crearSolicitud(solicitud)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = operacionState !is OperationState.EnCurso
            ) {
                if (operacionState is OperationState.EnCurso) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar solicitud")
                }
            }
        }
    }
}
