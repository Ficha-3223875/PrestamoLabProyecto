package com.example.prstamolabctma.ui.equipo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoScreen(
    equipoId: Int,
    viewModel: PrestamoViewModel,
    onSolicitarClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val equipo = viewModel.obtenerEquipo(equipoId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Detalle del equipo")
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

            if (equipo == null) {

                Text(
                    text = "El equipo no existe.",
                    style = MaterialTheme.typography.titleLarge
                )

            } else {

                Text(
                    text = equipo.nombre,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "ID: ${equipo.id}"
                )

                Text(
                    text = "Categoría: ${equipo.categoria}"
                )

                Text(
                    text = "Estado: ${equipo.estado}"
                )

                if (equipo.estado == EstadoEquipo.DISPONIBLE) {

                    Button(
                        onClick = {
                            onSolicitarClick(equipo.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Solicitar préstamo")
                    }

                } else {

                    Text(
                        text = "Este equipo no está disponible para préstamo."
                    )
                }
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}