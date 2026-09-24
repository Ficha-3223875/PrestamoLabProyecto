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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(navController: NavController, equipoId: Int, viewModel: PrestamoViewModel) {
    var ambiente by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Solicitud de préstamo") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = ambiente,
                onValueChange = { ambiente = it },
                label = { Text("Ambiente/Destino") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = proposito,
                onValueChange = { proposito = it },
                label = { Text("Propósito (10-180 caracteres)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración (1-8 horas)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val solicitud = SolicitudPrestamo(
                        id = 0, // Generado por Room @PrimaryKey(autoGenerate = true)
                        equipoId = equipoId,
                        ambienteDestino = ambiente,
                        proposito = proposito,
                        duracionHoras = duracion.toIntOrNull() ?: 0,
                        estado = EstadoSolicitud.SOLICITADA
                    )
                    viewModel.crearSolicitud(solicitud) { result ->
                        if (result.isSuccess) {
                            navController.navigate("misSolicitudes") {
                                popUpTo("catalogo") { inclusive = false }
                            }
                        } else {
                            val errorMsg = result.exceptionOrNull()?.message ?: "Error al crear la solicitud"
                            scope.launch {
                                snackbarHostState.showSnackbar(errorMsg)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar solicitud")
            }
        }
    }
}
