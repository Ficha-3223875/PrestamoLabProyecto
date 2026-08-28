package com.example.prestamolabctma.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.viewmodel.PrestamoViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    viewModel: PrestamoViewModel,
    onBack: () -> Unit,
    onSolicitudClick: (Int) -> Unit
) {

    val uiState by
    viewModel.uiState.collectAsState()

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Mis solicitudes")
                },

                navigationIcon = {

                    OutlinedButton(
                        onClick = onBack
                    ) {

                        Text("Volver")
                    }
                }
            )
        }

    ) { padding ->

        if (
            uiState.solicitudes.isEmpty()
        ) {

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
            ) {

                Text(
                    text =
                        "No tienes solicitudes registradas.",

                    style =
                        MaterialTheme.typography.titleLarge
                )
            }

        } else {

            LazyColumn(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),

                contentPadding =
                    PaddingValues(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    uiState.solicitudes
                ) { solicitud ->

                    val equipo =
                        viewModel.equipo(
                            solicitud.equipoId
                        )

                    Card(

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Column(

                            modifier =
                                Modifier.padding(16.dp),

                            verticalArrangement =
                                Arrangement.spacedBy(6.dp)
                        ) {

                            Text(
                                text =
                                    "Solicitud #${solicitud.id}",

                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text =
                                    "Equipo: ${
                                        equipo?.nombre
                                            ?: "Desconocido"
                                    }"
                            )

                            Text(
                                text =
                                    "Destino: ${solicitud.ambienteDestino}"
                            )

                            Text(
                                text =
                                    "Duración: ${solicitud.duracionHoras} horas"
                            )

                            Text(
                                text =
                                    "Estado: ${solicitud.estado}"
                            )

                            OutlinedButton(

                                onClick = {

                                    onSolicitudClick(
                                        solicitud.id
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                Text(
                                    "Ver solicitud"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}