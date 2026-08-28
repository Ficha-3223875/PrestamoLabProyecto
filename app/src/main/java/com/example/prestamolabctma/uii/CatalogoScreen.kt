package com.example.prestamolabctma.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.viewmodel.PrestamoViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    viewModel: PrestamoViewModel,
    onEquipoClick: (Int) -> Unit,
    onMisSolicitudes: () -> Unit
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

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Button(

                onClick = onMisSolicitudes,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
            ) {

                Text("Mis solicitudes")
            }

            LazyColumn(

                modifier = Modifier.fillMaxSize(),

                contentPadding = PaddingValues(
                    16.dp
                ),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    uiState.equipos
                ) { equipo ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = equipo.nombre,
                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text =
                                    "Categoría: ${equipo.categoria}",
                                modifier =
                                    Modifier.padding(
                                        top = 4.dp
                                    )
                            )

                            Text(
                                text =
                                    equipo.descripcion,
                                modifier =
                                    Modifier.padding(
                                        top = 4.dp
                                    )
                            )

                            Text(
                                text =
                                    "Estado: ${equipo.estado}",
                                modifier =
                                    Modifier.padding(
                                        top = 8.dp
                                    )
                            )

                            if (
                                equipo.estado ==
                                EstadoEquipo.DISPONIBLE
                            ) {

                                Button(

                                    onClick = {
                                        onEquipoClick(
                                            equipo.id
                                        )
                                    },

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 12.dp
                                            )
                                ) {

                                    Text(
                                        "Ver equipo"
                                    )
                                }

                            } else {

                                OutlinedButton(

                                    onClick = {
                                        onEquipoClick(
                                            equipo.id
                                        )
                                    },

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 12.dp
                                            )
                                ) {

                                    Text(
                                        "Ver detalle"
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