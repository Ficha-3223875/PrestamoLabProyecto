package com.example.prstamolabctma.ui.catalogo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun CatalogoScreen(
    viewModel: PrestamoViewModel,
    onEquipoClick: (Int) -> Unit,
    onMisPrestamosClick: () -> Unit
) {
    val equiposUiState by viewModel.equiposState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de equipos") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Button(
                onClick = onMisPrestamosClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mis préstamos")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (val state = equiposUiState) {
                    is UiState.Loading -> {
                        LoadingState(mensaje = "Cargando catálogo de equipos...")
                    }

                    is UiState.Empty -> {
                        EmptyState(mensaje = "No hay equipos registrados en el laboratorio.")
                    }

                    is UiState.Error -> {
                        ErrorState(
                            mensaje = state.message,
                            onRetry = null
                        )
                    }

                    is UiState.Content<List<Equipo>> -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.data) { equipo ->
                                val esDisponible = equipo.estado == EstadoEquipo.DISPONIBLE

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = esDisponible) { onEquipoClick(equipo.id) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = equipo.nombre,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(text = "Categoría: ${equipo.categoria}")

                                        if (esDisponible) {
                                            Text(
                                                text = "Estado: Disponible",
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        } else {
                                            Text(
                                                text = "Estado: ${equipo.estado} (No disponible)",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
