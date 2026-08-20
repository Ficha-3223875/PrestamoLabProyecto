package com.example.prstamolabctma.ui.misprestamos

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun MisSolicitudesScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis solicitudes") }) }
    ) { padding ->
        Text(
            text = "Aquí se listarán las solicitudes realizadas",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        )
    }
}
