package com.example.prstamolabctma.ui.solicitud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.ui.common.EmptyState
import com.example.prstamolabctma.ui.common.ErrorState
import com.example.prstamolabctma.ui.common.EvidenciaUbicacionSection
import com.example.prstamolabctma.ui.common.LoadingState
import com.example.prstamolabctma.ui.common.UiState
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(
    equipoId: Int,
    viewModel: PrestamoViewModel,
    onSolicitudCreada: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val equipoFlow = remember(equipoId) { viewModel.obtenerEquipoState(equipoId) }
    val equipoUiState by equipoFlow.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    var ambienteDestino by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracionHoras by remember { mutableStateOf("") }

    // Estados para almacenar foto y ubicación
    var evidenciaUri by remember { mutableStateOf<String?>(null) }
    var latitud by remember { mutableStateOf<Double?>(null) }
    var longitud by remember { mutableStateOf<Double?>(null) }

    // Control de bloqueo inmediato para prevenir doble clic
    var enviandoLocal by remember { mutableStateOf(false) }

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
            } else {
                // Si fue un mensaje de validación o error, permite reintentar
                enviandoLocal = false
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            when (val state = equipoUiState) {
                is UiState.Loading -> {
                    LoadingState(mensaje = "Cargando equipo...")
                }

                is UiState.Empty -> {
                    EmptyState(mensaje = "El equipo seleccionado no existe.")
                }

                is UiState.Error -> {
                    ErrorState(mensaje = state.message)
                }

                is UiState.Content<Equipo> -> {
                    val eq = state.data

                    Text(
                        text = "Equipo: ${eq.nombre}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(text = "Categoría: ${eq.categoria}")

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

                    // -------------------------------------------------------------
                    // COMPONENTE DE EVIDENCIA, UBICACIÓN Y NOTIFICACIONES
                    // -------------------------------------------------------------
                    EvidenciaUbicacionSection(
                        onEvidenciaCapturada = { uri, lat, lng ->
                            evidenciaUri = uri
                            latitud = lat
                            longitud = lng
                        }
                    )

                    Button(
                        onClick = {
                            if (!enviandoLocal && !uiState.guardando) {
                                enviandoLocal = true
                                val horas = duracionHoras.toIntOrNull() ?: 0
                                viewModel.crearSolicitud(
                                    equipoId = eq.id,
                                    ambienteDestino = ambienteDestino,
                                    proposito = proposito,
                                    duracionHoras = horas,
                                    evidenciaUri = evidenciaUri,
                                    latitud = latitud,
                                    longitud = longitud
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.guardando && !enviandoLocal
                    ) {
                        Text(if (uiState.guardando || enviandoLocal) "Guardando..." else "Enviar Solicitud")
                    }
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