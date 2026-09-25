package com.example.prstamolabctma.uii

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PinAuthDialog(
    titulo: String = "Confirmación de Seguridad",
    subtitulo: String = "Ingresa tu PIN de autorización (PIN por defecto: 1234)",
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    var pinIngresado by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Text(subtitulo, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pinIngresado,
                    onValueChange = {
                        pinIngresado = it
                        mensajeError = null
                    },
                    label = { Text("PIN / Contraseña") },
                    placeholder = { Text("1234") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = mensajeError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (mensajeError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mensajeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pinIngresado == "1234") {
                        onConfirmar()
                    } else {
                        mensajeError = "PIN incorrecto. Ingresa 1234 para continuar."
                    }
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
