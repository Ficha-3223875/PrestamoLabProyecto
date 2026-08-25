package com.example.prstamolabctma.ui.solicitud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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

    var ambienteDestino by remember {
        mutableStateOf("")
    }

    var proposito by remember {
        mutableStateOf("")
    }

    var duracionHoras by remember {
        mutableStateOf("")
    }

    val equipo = viewModel.obtenerEquipo(equipoId)

    /*
     * Limpiamos el mensaje anterior cada vez que
     * entramos a una nueva pantalla de solicitud.
     *
     * Esto evita que una solicitud anterior provoque
     * una navegación automática.
     */
    LaunchedEffect(Unit) {
        viewModel.limpiarMensaje()
    }

    /*
     * Cuando se crea correctamente una solicitud,
     * navegamos solamente en ese momento.
     */
    LaunchedEffect(uiState.mensaje) {
        if (uiState.mensaje == "Solicitud creada correctamente.") {
            viewModel.limpiarMensaje()
            onSolicitudCreada()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Solicitar préstamo")
                }
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

            if (equipo != null) {

                Text(
                    text = "Equipo: ${equipo.nombre}"
                )

                Text(
                    text = "Categoría: ${equipo.categoria}"
                )

            } else {

                Text(
                    text = "El equipo no existe."
                )
            }

            OutlinedTextField(
                value = ambienteDestino,
                onValueChange = {
                    ambienteDestino = it
                },
                label = {
                    Text("Ambiente o destino")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = proposito,
                onValueChange = {
                    proposito = it
                },
                label = {
                    Text("Propósito del préstamo")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = duracionHoras,
                onValueChange = {
                    duracionHoras = it.filter { character ->
                        character.isDigit()
                    }
                },
                label = {
                    Text("Duración en horas")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Button(
                onClick = {

                    val horas = duracionHoras.toIntOrNull()

                    if (horas != null) {

                        viewModel.crearSolicitud(
                            equipoId = equipoId,
                            ambienteDestino = ambienteDestino,
                            proposito = proposito,
                            duracionHoras = horas
                        )

                    } else {

                        viewModel.crearSolicitud(
                            equipoId = equipoId,
                            ambienteDestino = ambienteDestino,
                            proposito = proposito,
                            duracionHoras = 0
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.guardando && equipo != null
            ) {

                Text(
                    text = if (uiState.guardando) {
                        "Guardando..."
                    } else {
                        "Crear solicitud"
                    }
                )
            }

            /*
             * Mostramos los mensajes de validación.
             *
             * El mensaje de éxito ya no provoca navegación
             * directamente desde este bloque.
             */
            uiState.mensaje?.let { mensaje ->

                Text(
                    text = mensaje
                )
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}