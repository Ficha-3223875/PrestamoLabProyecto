package com.example.prstamolabctma.ui.catalogo

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    viewModel: PrestamoViewModel,
    onEquipoClick: (Int) -> Unit,
    onMisPrestamosClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Catálogo de equipos")
                }
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(uiState.equipos) { equipo ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEquipoClick(equipo.id)
                            }
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = equipo.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Categoría: ${equipo.categoria}"
                            )

                            Text(
                                text = "Estado: ${equipo.estado}"
                            )

                            if (equipo.estado == EstadoEquipo.DISPONIBLE) {
                                Text(
                                    text = "Disponible"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}