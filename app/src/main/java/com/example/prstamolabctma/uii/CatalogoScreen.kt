package com.example.prstamolabctma.ui.catalogo

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun CatalogoScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Catálogo de equipos") }) }
    ) { padding ->
        Text(
            text = "Aquí se mostrará la lista de equipos disponibles",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        )
    }
}
