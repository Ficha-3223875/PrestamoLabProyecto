package com.example.prstamolabctma.uii

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prstamolabctma.model.EstadoEvidencia
import com.example.prstamolabctma.ui.state.OperationState
import com.example.prstamolabctma.ui.state.UiState
import com.example.prstamolabctma.util.EvidenciaValidator
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(navController: NavController, solicitudId: Int, viewModel: PrestamoViewModel) {
    val context = LocalContext.current
    val solicitudesState by viewModel.solicitudesUiState.collectAsStateWithLifecycle()
    val evidenciasState by viewModel.obtenerEvidenciasPorSolicitudFlow(solicitudId).collectAsStateWithLifecycle(initialValue = UiState.Cargando)
    val operacionState by viewModel.operacionState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var fotoCamaraTempUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para Photo Picker (Galería - Mínimo Privilegio)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.adjuntarEvidencia(solicitudId, context, uri)
        }
    }

    // Launcher para Cámara externa con FileProvider
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && fotoCamaraTempUri != null) {
            viewModel.adjuntarEvidencia(solicitudId, context, fotoCamaraTempUri!!)
        }
    }

    // Launcher para Permiso de Notificaciones (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.activarRecordatorioNotificacion(context, solicitudId)
        } else {
            viewModel.resetearEstadoOperacion()
        }
    }

    LaunchedEffect(operacionState) {
        when (val state = operacionState) {
            is OperationState.Exitosa -> {
                snackbarHostState.showSnackbar(state.mensaje)
                viewModel.resetearEstadoOperacion()
            }
            is OperationState.Fallida -> {
                snackbarHostState.showSnackbar(state.mensaje)
                viewModel.resetearEstadoOperacion()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de solicitud #$solicitudId") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección Detalle Solicitud
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        when (val state = solicitudesState) {
                            is UiState.Contenido -> {
                                val solicitud = state.datos.find { it.id == solicitudId }
                                if (solicitud != null) {
                                    Text("Equipo ID: ${solicitud.equipoId}", style = MaterialTheme.typography.titleMedium)
                                    Text("Ambiente destino: ${solicitud.ambienteDestino}")
                                    Text("Propósito: ${solicitud.proposito}")
                                    Text("Duración: ${solicitud.duracionHoras} horas")
                                    Text("Estado: ${solicitud.estado}")
                                } else {
                                    Text("Solicitud no encontrada")
                                }
                            }
                            else -> Text("Cargando detalle...")
                        }
                    }
                }
            }

            // Sección Recordatorios / Notificaciones
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Recordatorio de Notificación", style = MaterialTheme.typography.titleSmall)
                            Text("Recibir alerta en el dispositivo", style = MaterialTheme.typography.bodySmall)
                        }
                        Button(onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    viewModel.activarRecordatorioNotificacion(context, solicitudId)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                viewModel.activarRecordatorioNotificacion(context, solicitudId)
                            }
                        }) {
                            Text("Activar 🔔")
                        }
                    }
                }
            }

            // Sección Evidencia Fotográfica
            item {
                Text("Evidencia Fotográfica", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Adjunta una imagen demostrativa (Máx. 5 MB - JPG/PNG/WEBP)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val uri = EvidenciaValidator.crearUriFotoCamara(context)
                            fotoCamaraTempUri = uri
                            cameraLauncher.launch(uri)
                        }
                    ) {
                        Text("Cámara 📷")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Text("Galería 🖼")
                    }
                }
            }

            // Lista de Evidencias adjuntas
            when (val state = evidenciasState) {
                is UiState.Cargando -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is UiState.Vacio -> {
                    item {
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("No hay evidencia fotográfica adjunta")
                            }
                        }
                    }
                }
                is UiState.Contenido -> {
                    items(state.datos) { evidencia ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = evidencia.uri,
                                        contentDescription = "Vista previa de evidencia",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Tipo: ${evidencia.tipoMime}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Tamaño: ${evidencia.tamanoBytes / 1024} KB", style = MaterialTheme.typography.bodySmall)
                                        Text("Estado: ${evidencia.estado}", style = MaterialTheme.typography.labelMedium)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (evidencia.estado == EstadoEvidencia.FALLIDA) {
                                        TextButton(onClick = { viewModel.reintentarSubidaEvidencia(evidencia.id) }) {
                                            Text("Reintentar ↻")
                                        }
                                    }

                                    IconButton(onClick = { viewModel.eliminarEvidencia(evidencia.id) }) {
                                        Text("🗑")
                                    }
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    item {
                        Text("Error al cargar evidencias: ${state.mensaje}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
