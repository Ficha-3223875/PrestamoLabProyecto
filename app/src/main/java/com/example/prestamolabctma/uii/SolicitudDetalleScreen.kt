package com.example.prestamolabctma.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.SolicitudPrestamo
import com.example.prestamolabctma.viewmodel.PrestamoViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(

    solicitud: SolicitudPrestamo?,

    equipo: Equipo?,

    viewModel: PrestamoViewModel,

    onBack: () -> Unit

) {

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Detalle de solicitud")
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

        if (solicitud == null) {

            Column(

                modifier =
                    Modifier
                        .padding(padding)
                        .padding(20.dp)
            ) {

                Text(

                    text =
                        "La solicitud no existe.",

                    style =
                        MaterialTheme.typography.titleLarge
                )
            }

            return@Scaffold
        }

        Column(

            modifier =
                Modifier
                    .padding(padding)
                    .padding(20.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Text(

                text =
                    "Solicitud #${solicitud.id}",

                style =
                    MaterialTheme.typography.headlineMedium
            )

            Text(
                text =
                    "Equipo: ${
                        equipo?.nombre
                            ?: "Equipo desconocido"
                    }"
            )

            Text(
                text =
                    "Destino: ${solicitud.ambienteDestino}"
            )

            Text(
                text =
                    "Propósito: ${solicitud.proposito}"
            )

            Text(
                text =
                    "Duración: ${solicitud.duracionHoras} horas"
            )

            Text(
                text =
                    "Estado: ${solicitud.estado}"
            )

            if (
                solicitud.estado ==
                EstadoSolicitud.SOLICITADA
            ) {

                Button(

                    onClick = {

                        viewModel.cancelarSolicitud(
                            solicitud.id
                        )

                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(
                        "Cancelar solicitud"
                    )
                }
            }

            if (
                solicitud.estado ==
                EstadoSolicitud.CANCELADA
            ) {

                Text(

                    text =
                        "Esta solicitud ya fue cancelada.",

                    color =
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}