package com.example.prstamolabctma.ui.equipo

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun EquipoDetalleScreen(navController: NavController, equipoId: Int) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle del equipo $equipoId") }) }
    ) { padding ->
        Text(
            text = "Aquí se mostrará la información del equipo seleccionado",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        )
    }
}
