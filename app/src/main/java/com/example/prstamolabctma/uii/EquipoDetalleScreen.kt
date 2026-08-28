package com.example.prstamolabctma.uii

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(navController: NavController, equipoId: Int, viewModel: PrestamoViewModel) {
    val equipos = viewModel.equipos.collectAsState()
    val equipo = equipos.value.find { it.id == equipoId }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle del equipo") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            if (equipo != null) {
                Text("Nombre: ${equipo.nombre}", style = MaterialTheme.typography.titleMedium)
                Text("Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodyMedium)
                Text("Estado: ${equipo.estado}", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("solicitar/${equipo.id}") },
                    enabled = equipo.estado == EstadoEquipo.DISPONIBLE
                ) {
                    Text("Reservar equipo")
                }
            } else {
                Text("Equipo no encontrado")
            }
        }
    }
}