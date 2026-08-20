package com.example.prstamolabctma.ui.misprestamos

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun SolicitudDetalleScreen(navController: NavController, solicitudId: Int) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de solicitud $solicitudId") }) }
    ) { padding ->
        Text(
            text = "Aquí se mostrará la información de la solicitud seleccionada",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        )
    }
}
