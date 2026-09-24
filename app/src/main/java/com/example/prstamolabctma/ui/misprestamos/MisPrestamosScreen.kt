package com.example.prstamolabctma.ui.misprestamos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.ui.common.EmptyState
import com.example.prstamolabctma.ui.common.ErrorState
import com.example.prstamolabctma.ui.common.LoadingState
import com.example.prstamolabctma.ui.common.UiState
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPrestamosScreen(
    viewModel: PrestamoViewModel,
    onSolicitudClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val solicitudesUiState by viewModel.solicitudesState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis préstamos") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                when (val state = solicitudesUiState) {
                    is UiState.Loading -> {
                        LoadingState(mensaje = "Cargando tus solicitudes...")
                    }

                    is UiState.Empty -> {
                        EmptyState(mensaje = "No tienes solicitudes de préstamo registradas.")
                    }

                    is UiState.Error -> {
                        ErrorState(
                            mensaje = state.message,
                            onRetry = null
                        )
                    }

                    is UiState.Content<List<SolicitudPrestamo>> -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.data) { solicitud ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSolicitudClick(solicitud.id) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Solicitud #${solicitud.id}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(text = "Equipo ID: ${solicitud.equipoId}")
                                        Text(text = "Destino: ${solicitud.ambienteDestino}")
                                        Text(text = "Duración: ${solicitud.duracionHoras} hora(s)")
                                        Text(text = "Estado: ${solicitud.estado}")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al catálogo")
            }
        }
    }
}
