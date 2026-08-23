package com.example.prstamolabctma.ui.catalogo

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prstamolabctma.navigation.Destino
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@Composable
fun CatalogoScreen(
    viewModel: PrestamoViewModel,
    navController: NavHostController,
    onEquipoClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn {
        items(state.equipos) { equipo ->
            Button(
                onClick = { onEquipoClick(equipo.id) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Ver detalle de ${equipo.nombre}")
            }

        }

        // ✅ Botón para ver solicitudes
        item {
            Button(
                onClick = { navController.navigate(Destino.MisSolicitudes.ruta) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Ver mis préstamos")
            }
        }
    }
}
