package com.example.prestamolabctma.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.viewmodel.EstadoOperacion
import com.example.prestamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(
    equipo: Equipo?,
    viewModel: PrestamoViewModel,
    onBack: () -> Unit,
    onCreated: (Int) -> Unit
) {
    // Recolección consciente del ciclo de vida (Semana 7)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var ambienteDestino by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var errorLocal by remember { mutableStateOf<String?>(null) }

    // Reaccionar a cambios en EstadoOperacion (Inactiva, EnCurso, Exitosa, Fallida)
    LaunchedEffect(uiState.estadoOperacion) {
        when (val operacion = uiState.estadoOperacion) {
            is EstadoOperacion.Exitosa -> {
                val nuevaSolicitud = uiState.solicitudes.lastOrNull()
                nuevaSolicitud?.let { onCreated(it.id) }
                viewModel.reiniciarEstadoOperacion()
            }
            is EstadoOperacion.Fallida -> {
                errorLocal = operacion.mensaje
            }
            else -> {}
        }
    }

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
                enabled = uiState.estadoOperacion !is EstadoOperacion.EnCurso,
                singleLine = true
            )

            OutlinedTextField(
                value = proposito,
                onValueChange = { if (it.length <= 180) proposito = it },
                label = { Text("Propósito") },
                supportingText = { Text("${proposito.length}/180 caracteres") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.estadoOperacion !is EstadoOperacion.EnCurso,
                minLines = 4
            )

            OutlinedTextField(
                value = duracion,
                onValueChange = { if (it.all { c -> c.isDigit() }) duracion = it },
                label = { Text("Duración en horas") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.estadoOperacion !is EstadoOperacion.EnCurso,
                singleLine = true
            )

            val mensajeMostrar = errorLocal ?: uiState.mensaje
            if (mensajeMostrar != null) {
                Text(text = mensajeMostrar, color = MaterialTheme.colorScheme.error)
            }

            val estaEnCurso = uiState.estadoOperacion is EstadoOperacion.EnCurso || uiState.guardando

            Button(
                onClick = {
                    errorLocal = null
                    val horas = duracion.toIntOrNull()

                    when {
                        ambienteDestino.trim().isEmpty() -> {
                            errorLocal = "El ambiente destino es obligatorio."
                        }
                        proposito.trim().length !in 10..180 -> {
                            errorLocal = "El propósito debe tener entre 10 y 180 caracteres."
                        }
                        horas == null || horas !in 1..8 -> {
                            errorLocal = "La duración debe estar entre 1 y 8 horas."
                        }
                        else -> {
                            viewModel.crearSolicitud(
                                equipoId = equipo.id,
                                ambienteDestino = ambienteDestino,
                                proposito = proposito,
                                duracionHoras = horas
                            )
                        }
                    }
                },
                enabled = !estaEnCurso,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estaEnCurso) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Procesando operación...")
                    }
                } else {
                    Text("Guardar solicitud")
                }
            }
        }
    }
}
