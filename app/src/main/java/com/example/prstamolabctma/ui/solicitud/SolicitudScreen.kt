package com.example.prstamolabctma.ui.solicitud

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prstamolabctma.navigation.Destino
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@Composable
fun SolicitudScreen(
    viewModel: PrestamoViewModel,
    equipoId: Int,
    navController: NavHostController   // 👈 agregado
) {
    var destino by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Solicitud de préstamo")

        OutlinedTextField(
            value = destino,
            onValueChange = { destino = it },
            label = { Text("Ambiente/Destino") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = proposito,
            onValueChange = { proposito = it },
            label = { Text("Propósito") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = duracion,
            onValueChange = { duracion = it },
            label = { Text("Duración (horas)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                val horas = duracion.toIntOrNull() ?: 0
                viewModel.crearSolicitud(equipoId, destino, proposito, horas)

                // 👇 ahora sí funciona
                navController.navigate(Destino.MisSolicitudes.ruta)
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Guardar")


        }
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Volver")
        }
    }
}
