package com.example.prestamolabctma.evaluacion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 4. Crea una Route composable que obtenga uiState mediante collectAsStateWithLifecycle
 * y entregue estado/callbacks a un Content stateless.
 */
@Composable
fun CrearReporteRoute(
    viewModel: CrearReporteViewModel
) {
    // Recolección consciente del ciclo de vida de Android
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CrearReporteContent(
        uiState = uiState,
        onTituloChange = { viewModel.actualizarTitulo(it) },
        onGuardarClick = { viewModel.guardar() }
    )
}

/**
 * Componente stateless (sin estado interno) que renderiza la interfaz.
 */
@Composable
fun CrearReporteContent(
    uiState: CrearUiState,
    onTituloChange: (String) -> Unit,
    onGuardarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Crear Nuevo Reporte (Evaluación)")
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.titulo,
            onValueChange = onTituloChange,
            label = { Text("Título del reporte") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.errorTitulo != null,
            supportingText = {
                if (uiState.errorTitulo != null) {
                    Text(text = uiState.errorTitulo, color = Color.Red)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.guardando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onGuardarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Guardar Reporte")
            }
        }

        uiState.guardadoId?.let { id ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "¡Guardado exitosamente con ID: $id!", color = Color.Green)
        }
    }
}
