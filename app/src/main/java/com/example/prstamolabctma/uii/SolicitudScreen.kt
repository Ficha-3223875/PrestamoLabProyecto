package com.example.prstamolabctma.ui.solicitud

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun SolicitudScreen(navController: NavController, equipoId: Int) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Solicitud de préstamo") }) }
    ) { padding ->
        Text(
            text = "Formulario para solicitar el equipo $equipoId",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        )
    }
}
