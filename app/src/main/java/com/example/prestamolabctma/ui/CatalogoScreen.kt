package com.example.prestamolabctma.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.viewmodel.EstadoOperacion
import com.example.prestamolabctma.viewmodel.EstadoPantalla
import com.example.prestamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    viewModel: PrestamoViewModel,
    onEquipoClick: (Int) -> Unit,
    onMisSolicitudes: () -> Unit
) {
    // Recolección consciente del ciclo de vida (Semana 7 y 8)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de equipos") },
                actions = {
                    Button(
                        onClick = { viewModel.sincronizarConServidor() },
                        enabled = uiState.estadoOperacion !is EstadoOperacion.EnCurso,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Sincronizar API")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMisSolicitudes,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Mis solicitudes")
                }
                OutlinedButton(
                    onClick = { viewModel.verificarBluetooth() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Bluetooth Estado")
                }
            }

            if (uiState.bluetoothDispositivos.isNotEmpty() || uiState.bluetoothEstado.contains("Bluetooth")) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "📡 Capacidad del Dispositivo - Bluetooth", style = MaterialTheme.typography.titleSmall)
                        Text(text = "Estado: ${uiState.bluetoothEstado}", style = MaterialTheme.typography.bodyMedium)
                        if (uiState.bluetoothDispositivos.isNotEmpty()) {
                            Text(text = "Dispositivos vinculados:", style = MaterialTheme.typography.bodySmall)
                            uiState.bluetoothDispositivos.forEach { disp ->
                                Text(text = "• $disp", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // --- ESTADO DE ACTUALIZACIÓN DESDE API (Semana 8: Resiliencia y separación de contenido local) ---
            when (val op = uiState.estadoOperacion) {
                is EstadoOperacion.EnCurso -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Text("Sincronizando catálogo con la API REST...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                is EstadoOperacion.Fallida -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = op.mensaje,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            OutlinedButton(
                                onClick = { viewModel.sincronizarConServidor() },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                is EstadoOperacion.Exitosa -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = op.mensaje,
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                else -> {}
            }

            // --- FILTROS VISUALES PERSISTENTES CON DATASTORE ---
            Text(
                text = "Filtrar por categoría (Persistido con DataStore):",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categoriasFiltro = listOf("TODOS", "ELECTRONICA", "COMPUTO", "MEDICION", "AUDIOVISUAL", "HERRAMIENTA")
                items(categoriasFiltro) { cat ->
                    val esSeleccionado = uiState.filtroCategoria == cat
                    if (esSeleccionado) {
                        Button(onClick = { viewModel.cambiarFiltroCategoria(cat) }) {
                            Text(cat)
                        }
                    } else {
                        OutlinedButton(onClick = { viewModel.cambiarFiltroCategoria(cat) }) {
                            Text(cat)
                        }
                    }
                }
            }

            // --- REPRESENTACIÓN EXPLÍCITA DE ESTADOS DE PANTALLA: Cargando / Contenido / Vacío / Error ---
            when (val estado = uiState.estadoPantalla) {
                is EstadoPantalla.Cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text(
                                text = "Cargando catálogo...",
                                modifier = Modifier.padding(top = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                is EstadoPantalla.Vacio -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = estado.mensaje,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is EstadoPantalla.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Error: ${estado.mensaje}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(
                                onClick = { viewModel.cambiarFiltroCategoria(uiState.filtroCategoria) },
                                modifier = Modifier.padding(top = 12.dp)
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                is EstadoPantalla.Contenido -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.equipos) { equipo ->
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = equipo.nombre,
                                        style = MaterialTheme.typography.titleLarge
                                    )

                                    Text(
                                        text = "Categoría: ${equipo.categoria}",
                                        modifier = Modifier.padding(top = 4.dp)
                                    )

                                    Text(
                                        text = equipo.descripcion,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )

                                    Text(
                                        text = "Estado: ${equipo.estado}",
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    if (equipo.estado == EstadoEquipo.DISPONIBLE) {
                                        Button(
                                            onClick = { onEquipoClick(equipo.id) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 12.dp)
                                        ) {
                                            Text("Ver equipo")
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = { onEquipoClick(equipo.id) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 12.dp)
                                        ) {
                                            Text("Ver detalle")
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
