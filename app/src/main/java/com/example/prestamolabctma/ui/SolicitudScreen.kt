package com.example.prestamolabctma.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.viewmodel.PrestamoViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(
    equipo: Equipo?,
    viewModel: PrestamoViewModel,
    onBack: () -> Unit,
    onCreated: (Int) -> Unit
) {
    // ✅ Usar collectAsState para observar el StateFlow
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var ambienteDestino by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar préstamo") },
                navigationIcon = {
                    OutlinedButton(onClick = onBack) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (equipo == null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
            ) {
                Text(
                    "El equipo no existe.",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Equipo: ${equipo.nombre}",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = ambienteDestino,
                onValueChange = { ambienteDestino = it },
                label = { Text("Ambiente destino") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = proposito,
                onValueChange = { if (it.length <= 180) proposito = it },
                label = { Text("Propósito") },
                supportingText = { Text("${proposito.length}/180 caracteres") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            OutlinedTextField(
                value = duracion,
                onValueChange = { if (it.all { c -> c.isDigit() }) duracion = it },
                label = { Text("Duración en horas") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val horas = duracion.toIntOrNull()

                    when {
                        ambienteDestino.trim().isEmpty() -> {
                            error = "El ambiente destino es obligatorio."
                            return@Button
                        }
                        proposito.trim().length !in 10..180 -> {
                            error = "El propósito debe tener entre 10 y 180 caracteres."
                            return@Button
                        }
                        horas == null || horas !in 1..8 -> {
                            error = "La duración debe estar entre 1 y 8 horas."
                            return@Button
                        }
                        else -> {
                            val creado = viewModel.crearSolicitud(
                                equipoId = equipo.id,
                                ambienteDestino = ambienteDestino,
                                proposito = proposito,
                                duracionHoras = horas
                            )

                            if (creado) {
                                val nuevaSolicitud = uiState.solicitudes.lastOrNull()
                                nuevaSolicitud?.let { onCreated(it.id) }
                            } else {
                                error = uiState.mensaje
                            }
                        }
                    }
                },
                enabled = !uiState.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.guardando) "Guardando..." else "Guardar solicitud")
            }
        }
    }
}
