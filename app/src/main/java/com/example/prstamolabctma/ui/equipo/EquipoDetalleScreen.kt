package com.example.prstamolabctma.ui.equipo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prstamolabctma.navigation.Destino
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun EquipoDetalleScreen(
    viewModel: PrestamoViewModel,
    equipoId: Int,
    navController: NavHostController
) {
    val state by viewModel.uiState.collectAsState()  // ✅

    val equipo = state.equipos.find { it.id == equipoId }

    Column(modifier = Modifier.padding(16.dp)) {
        if (equipo != null) {
            Text("Equipo: ${equipo.nombre}")
            Text("Categoría: ${equipo.categoria}")
            Text("Estado: ${equipo.estado}")

            Button(onClick = {
                navController.navigate(Destino.Solicitud.crearRuta(equipoId))
            }) {
                Text("Solicitar préstamo")
            }
        } else {
            Text("Equipo no encontrado")
        }
        Button(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }

    }
}
