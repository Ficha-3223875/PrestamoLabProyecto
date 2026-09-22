package com.example.prstamolabctma.ui.solicitud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(
    equipoId: Int,
    viewModel: PrestamoViewModel,
    onSolicitudCreada: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var equipo by remember { mutableStateOf<Equipo?>(null) }
    var cargandoEquipo by remember { mutableStateOf(true) }

    var ambienteDestino by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracionHoras by remember { mutableStateOf("") }

    // Cargar información del equipo desde Room
    LaunchedEffect(equipoId) {
        cargandoEquipo = true
        viewModel.obtenerEquipo(equipoId) { resultado ->
            equipo = resultado
            cargandoEquipo = false
        }
    }

    // Limpiar mensajes previos al entrar
    LaunchedEffect(Unit) {
        viewModel.limpiarMensaje()
    }

    // Mostrar Snackbar ante mensajes de éxito o error
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            if (mensaje == "Solicitud creada correctamente.") {
                viewModel.limpiarMensaje()
                onSolicitudCreada()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Nueva Solicitud") }
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

            if (cargandoEquipo) {

                Text(
                    text = "Cargando equipo...",
                    style = MaterialTheme.typography.bodyLarge
                )

            } else if (equipo == null) {

                Text(
                    text = "El equipo seleccionado no existe.",
                    style = MaterialTheme.typography.titleLarge
                )

            } else {

                val eq = equipo!!

                Text(
                    text = "Equipo: ${eq.nombre}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Categoría: ${eq.categoria}"
                )

                OutlinedTextField(
                    value = ambienteDestino,
                    onValueChange = { ambienteDestino = it },
                    label = { Text("Ambiente o Destino") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = proposito,
                    onValueChange = { proposito = it },
                    label = { Text("Propósito (10-180 caracteres)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = duracionHoras,
                    onValueChange = { duracionHoras = it },
                    label = { Text("Duración (Horas: 1-8)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Button(
                    onClick = {
                        val horas = duracionHoras.toIntOrNull() ?: 0
                        viewModel.crearSolicitud(
                            equipoId = eq.id,
                            ambienteDestino = ambienteDestino,
                            proposito = proposito,
                            duracionHoras = horas
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.guardando
                ) {
                    Text(if (uiState.guardando) "Guardando..." else "Enviar Solicitud")
                }
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}