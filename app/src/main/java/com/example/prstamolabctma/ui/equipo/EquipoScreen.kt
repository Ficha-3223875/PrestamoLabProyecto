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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.ui.common.EmptyState
import com.example.prstamolabctma.ui.common.ErrorState
import com.example.prstamolabctma.ui.common.LoadingState
import com.example.prstamolabctma.ui.common.UiState
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoScreen(
    equipoId: Int,
    viewModel: PrestamoViewModel,
    onSolicitarClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val equipoFlow = remember(equipoId) { viewModel.obtenerEquipoState(equipoId) }
    val equipoUiState by equipoFlow.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del equipo") }
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

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (val state = equipoUiState) {
                    is UiState.Loading -> {
                        LoadingState(mensaje = "Cargando información del equipo...")
                    }

                    is UiState.Empty -> {
                        EmptyState(mensaje = "El equipo solicitado no existe.")
                    }

                    is UiState.Error -> {
                        ErrorState(mensaje = state.message)
                    }

                    is UiState.Content<Equipo> -> {
                        val equipoActual = state.data

                        Text(
                            text = equipoActual.nombre,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(text = "ID: ${equipoActual.id}")
                        Text(text = "Categoría: ${equipoActual.categoria}")
                        Text(text = "Estado: ${equipoActual.estado}")

                        if (equipoActual.estado == EstadoEquipo.DISPONIBLE) {
                            Button(
                                onClick = { onSolicitarClick(equipoActual.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Solicitar préstamo")
                            }
                        } else {
                            Text(
                                text = "Este equipo no está disponible para préstamo.",
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
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
