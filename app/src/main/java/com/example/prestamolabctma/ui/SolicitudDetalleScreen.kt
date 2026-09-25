package com.example.prestamolabctma.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.prestamolabctma.R
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.SolicitudPrestamo
import com.example.prestamolabctma.viewmodel.EstadoOperacion
import com.example.prestamolabctma.viewmodel.PrestamoViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    equipo: Equipo?,
    viewModel: PrestamoViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. Launcher de Cámara para tomar foto de evidencia (Semana 9)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { capturedBitmap ->
        if (solicitud != null) {
            val fotoUriFinal = if (capturedBitmap != null) {
                try {
                    val file = File(context.cacheDir, "evidencia_foto_${solicitud.id}.jpg")
                    FileOutputStream(file).use { out ->
                        capturedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                    }
                    Uri.fromFile(file).toString()
                } catch (e: Exception) {
                    "content://media/external/images/media/foto_camara_${solicitud.id}.jpg"
                }
            } else {
                "content://media/external/images/media/foto_camara_${solicitud.id}.jpg"
            }

            viewModel.adjuntarEvidencia(solicitud.id, fotoUriFinal)
            Toast.makeText(context, "📷 Foto capturada y guardada como evidencia.", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Launcher de Galería para seleccionar foto (Semana 9)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uriPicked ->
        if (solicitud != null) {
            val finalUri = uriPicked?.toString() ?: "content://media/external/images/media/foto_galeria_${solicitud.id}.jpg"
            viewModel.adjuntarEvidencia(solicitud.id, finalUri)
            Toast.makeText(context, "🖼️ Foto seleccionada correctamente.", Toast.LENGTH_SHORT).show()
        }
    }

    // Permiso de notificaciones bajo demanda (Semana 9: Solo al activar el recordatorio)
    var recordatorioActivado by remember { mutableStateOf(uiState.recordatorioNotificacionesActivo) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        viewModel.actualizarPermisoNotificaciones(concedido)
        if (concedido) {
            Toast.makeText(context, "Notificaciones de recordatorio activadas.", Toast.LENGTH_SHORT).show()
        } else {
            recordatorioActivado = false
            viewModel.cambiarEstadoRecordatorioNotificaciones(false)
            Toast.makeText(context, "Permiso de notificaciones denegado.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de solicitud") },
                navigationIcon = {
                    OutlinedButton(onClick = onBack) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { padding ->

        if (solicitud == null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
            ) {
                Text(
                    text = "La solicitud no existe.",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            return@Scaffold
        }

        val solicitudActualizada = viewModel.solicitud(solicitud.id) ?: solicitud
        val estaEnCurso = uiState.estadoOperacion is EstadoOperacion.EnCurso

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Solicitud #${solicitudActualizada.id}",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(text = "Equipo: ${equipo?.nombre ?: "Equipo desconocido"}")
            Text(text = "Destino: ${solicitudActualizada.ambienteDestino}")
            Text(text = "Propósito: ${solicitudActualizada.proposito}")
            Text(text = "Duración: ${solicitudActualizada.duracionHoras} horas")
            Text(text = "Estado: ${solicitudActualizada.estado}")

            // --- SECCIÓN PERMISOS DE NOTIFICACIÓN BAJO DEMANDA (Semana 9) ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Recordatorio de devolución", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "Solicita permiso únicamente al activar la función",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = recordatorioActivado,
                        onCheckedChange = { nuevoEstado ->
                            recordatorioActivado = nuevoEstado
                            viewModel.cambiarEstadoRecordatorioNotificaciones(nuevoEstado)

                            if (nuevoEstado) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val status = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                                    if (status != PackageManager.PERMISSION_GRANTED) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        Toast.makeText(context, "Recordatorio activado.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Recordatorio activado.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }

            // --- SECCIÓN EVIDENCIA FOTOGRÁFICA Y PREVISUALIZACIÓN DE IMAGEN (Semana 9) ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("📷 Evidencia Fotográfica", style = MaterialTheme.typography.titleSmall)

                    val uriActual = solicitudActualizada.evidenciaUri
                    val estadoEvidencia = solicitudActualizada.estadoEvidencia

                    if (uriActual != null) {
                        // Carga la foto real desde el ContentResolver o archivo local
                        val loadedBitmap: ImageBitmap? = remember(uriActual) {
                            try {
                                val uri = Uri.parse(uriActual)
                                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                    BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                                }
                            } catch (e: Exception) {
                                null
                            }
                        }

                        // Renderiza la imagen fotográfica en pantalla
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (loadedBitmap != null) {
                                Image(
                                    bitmap = loadedBitmap,
                                    contentDescription = "Foto de evidencia adjunta",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_background),
                                    contentDescription = "Previsualización de evidencia",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Text(
                            text = "URI: $uriActual",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Text(
                            text = "Estado Evidencia: $estadoEvidencia",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (estadoEvidencia) {
                                "Sincronizada" -> Color(0xFF2E7D32)
                                "Subiendo" -> Color(0xFFE65100)
                                "Fallida" -> Color.Red
                                else -> Color(0xFF1565C0)
                            }
                        )

                        if (estadoEvidencia == "Fallida" || estadoEvidencia == "Local") {
                            OutlinedButton(
                                onClick = { viewModel.reintentarSubirEvidencia(solicitudActualizada.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("☁️ Sincronizar evidencia con servidor")
                            }
                        }
                    } else {
                        Text("No se ha adjuntado foto de evidencia.", style = MaterialTheme.typography.bodySmall)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { cameraLauncher.launch(null) },
                            enabled = !estaEnCurso,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📷 Cámara")
                        }

                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            enabled = !estaEnCurso,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🖼️ Galería")
                        }
                    }
                }
            }

            if (solicitudActualizada.estado == EstadoSolicitud.SOLICITADA) {
                Button(
                    onClick = { viewModel.cancelarSolicitud(solicitudActualizada.id) },
                    enabled = !estaEnCurso,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (estaEnCurso) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Text("Procesando...")
                        }
                    } else {
                        Text("Cancelar solicitud")
                    }
                }
            }

            if (solicitudActualizada.estado == EstadoSolicitud.CANCELADA) {
                Text(
                    text = "Esta solicitud ya fue cancelada.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
