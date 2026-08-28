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
import com.example.prestamolabctma.model.EstadoEquipo

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(
    equipo: Equipo?,
    onBack: () -> Unit,
    onSolicitar: (Int) -> Unit
) {

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Detalle del equipo")
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

        if (equipo == null) {

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
            ) {

                Text(
                    text =
                        "El equipo solicitado no existe.",

                    style =
                        MaterialTheme.typography.titleLarge
                )
            }

        } else {

            Column(

                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = equipo.nombre,
                    style =
                        MaterialTheme.typography.headlineMedium
                )

                Text(
                    text =
                        "Categoría: ${equipo.categoria}"
                )

                Text(
                    text =
                        "Descripción: ${equipo.descripcion}"
                )

                Text(
                    text =
                        "Estado: ${equipo.estado}"
                )

                if (
                    equipo.estado ==
                    EstadoEquipo.DISPONIBLE
                ) {

                    Button(

                        onClick = {
                            onSolicitar(
                                equipo.id
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "Solicitar préstamo"
                        )
                    }

                } else {

                    Text(
                        text =
                            "Este equipo no está disponible actualmente.",
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}