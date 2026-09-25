package com.example.prstamolabctma.uii

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.ui.state.OperationState
import com.example.prstamolabctma.util.BiometricHelper
import com.example.prstamolabctma.viewmodel.PrestamoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(navController: NavController, equipoId: Int, viewModel: PrestamoViewModel) {
    var ambiente by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    var mostrarPinDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val operacionState by viewModel.operacionState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun procesarCreacion() {
        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = equipoId,
            ambienteDestino = ambiente,
            proposito = proposito,
            duracionHoras = duracion.toIntOrNull() ?: 0,
            estado = EstadoSolicitud.SOLICITADA
        )
        viewModel.crearSolicitud(solicitud)
    }

    if (mostrarPinDialog) {
        PinAuthDialog(
            titulo = "Firma de Solicitud (PIN / Contraseña)",
            subtitulo = "Ingresa tu PIN de seguridad (PIN por defecto: 1234)",
            onConfirmar = {
                mostrarPinDialog = false
                procesarCreacion()
            },
            onDismiss = {
                mostrarPinDialog = false
            }
        )
    }

    LaunchedEffect(operacionState) {
        when (val state = operacionState) {
            is OperationState.Exitosa -> {
                snackbarHostState.showSnackbar(state.mensaje)
                viewModel.resetearEstadoOperacion()
                ambiente = ""
                proposito = ""
                duracion = ""
            }
            is OperationState.Fallida -> {
                snackbarHostState.showSnackbar(state.mensaje)
                viewModel.resetearEstadoOperacion()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Solicitud de préstamo") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = ambiente,
                onValueChange = { ambiente = it },
                label = { Text("Ambiente/Destino") },
                modifier = Modifier.fillMaxWidth(),
                enabled = operacionState !is OperationState.EnCurso
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = proposito,
                onValueChange = { proposito = it },
                label = { Text("Propósito (10-180 caracteres)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = operacionState !is OperationState.EnCurso
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración (1-8 horas)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = operacionState !is OperationState.EnCurso
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (activity != null && BiometricHelper.canAuthenticate(activity)) {
                        BiometricHelper.showBiometricPrompt(
                            activity = activity,
                            titulo = "Firma Biométrica de Solicitud",
                            subtitulo = "Confirma con tu huella/rostro para enviar la solicitud",
                            onSuccess = { procesarCreacion() },
                            onError = {
                                // Si cancela o no reconoce la huella, solicita PIN 1234
                                mostrarPinDialog = true
                            }
                        )
                    } else {
                        // Si no hay biometría en el dispositivo/emulador, solicita el PIN 1234
                        mostrarPinDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = operacionState !is OperationState.EnCurso
            ) {
                if (operacionState is OperationState.EnCurso) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar solicitud 🔒")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { navController.navigate("misSolicitudes") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver mis solicitudes 📋")
            }
        }
    }
}
