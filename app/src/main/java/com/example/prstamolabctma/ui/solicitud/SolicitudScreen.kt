package com.example.prstamolabctma.ui.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prstamolabctma.navigation.Destino
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(
    viewModel: PrestamoViewModel,
    equipoId: Int,
    navController: NavHostController
) {
    var destino by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    // Estados para mensajes de error locales en UI
    var errorDestino by remember { mutableStateOf<String?>(null) }
    var errorProposito by remember { mutableStateOf<String?>(null) }
    var errorDuracion by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState()

    fun validarFormulario(): Boolean {
        var esValido = true

        // Validar Destino
        if (destino.isBlank()) {
            errorDestino = "El destino es obligatorio."
            esValido = false
        } else {
            errorDestino = null
        }

        // Validar Propósito (10 a 180 caracteres)
        val largoProposito = proposito.trim().length
        if (largoProposito < 10) {
            errorProposito = "El propósito debe tener al menos 10 caracteres."
            esValido = false
        } else if (largoProposito > 180) {
            errorProposito = "El propósito no puede superar los 180 caracteres."
            esValido = false
        } else {
            errorProposito = null
        }

        // Validar Duración (1 a 8 horas)
        val horas = duracion.toIntOrNull()
        if (horas == null || horas !in 1..8) {
            errorDuracion = "La duración debe estar entre 1 y 8 horas."
            esValido = false
        } else {
            errorDuracion = null
        }

        return esValido
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitud de Préstamo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Campo Destino
                    OutlinedTextField(
                        value = destino,
                        onValueChange = {
                            destino = it
                            if (errorDestino != null) errorDestino = null
                        },
                        label = { Text("Ambiente / Destino *") },
                        isError = errorDestino != null,
                        supportingText = {
                            errorDestino?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo Propósito
                    OutlinedTextField(
                        value = proposito,
                        onValueChange = {
                            proposito = it
                            if (errorProposito != null) errorProposito = null
                        },
                        label = { Text("Propósito *") },
                        isError = errorProposito != null,
                        supportingText = {
                            if (errorProposito != null) {
                                Text(errorProposito!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("${proposito.length}/180 caracteres")
                            }
                        },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo Duración
                    OutlinedTextField(
                        value = duracion,
                        onValueChange = {
                            duracion = it
                            if (errorDuracion != null) errorDuracion = null
                        },
                        label = { Text("Duración en horas (1 - 8) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = errorDuracion != null,
                        supportingText = {
                            errorDuracion?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Guardar
            Button(
                onClick = {
                    if (validarFormulario()) {
                        viewModel.crearSolicitud(
                            equipoId = equipoId,
                            destino = destino,
                            proposito = proposito,
                            horasTexto = duracion
                        )
                        navController.navigate(route = Destino.MisSolicitudes.ruta)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar Solicitud", style = MaterialTheme.typography.titleMedium)
            }

            // Botón Volver
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Volver")
            }
        }
    }
}