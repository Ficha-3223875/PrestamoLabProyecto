package com.example.prstamolabctma.uii

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(navController: NavController, viewModel: PrestamoViewModel) {
    val equipos = viewModel.equipos.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Catálogo de equipos") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            equipos.value.forEach { equipo ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("equipoDetalle/${equipo.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(equipo.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("Estado: ${equipo.estado}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}