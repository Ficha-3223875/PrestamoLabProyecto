package com.example.prstamolabctma.ui.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import coil.compose.AsyncImage
import com.example.prstamolabctma.util.NotificationHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File
import java.io.FileOutputStream

// Función auxiliar para copiar la imagen seleccionada al almacenamiento local
fun guardarImagenEnAlmacenamientoInterno(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val archivoSalida = File(context.filesDir, "evidencia_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(archivoSalida)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        archivoSalida.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@SuppressLint("MissingPermission")
@Composable
fun EvidenciaUbicacionSection(
    onEvidenciaCapturada: (uriString: String?, lat: Double?, lng: Double?) -> Unit
) {
    val context = LocalContext.current
    var selectedImageUriString by remember { mutableStateOf<String?>(null) }
    var latitud by remember { mutableStateOf<Double?>(null) }
    var longitud by remember { mutableStateOf<Double?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun obtenerCoordenadasGPS() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    latitud = location.latitude
                    longitud = location.longitude
                    onEvidenciaCapturada(selectedImageUriString, latitud, longitud)
                }
            }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // Guardar copia local permanente
            val rutaArchivoLocal = guardarImagenEnAlmacenamientoInterno(context, uri)
            selectedImageUriString = rutaArchivoLocal ?: uri.toString()
            onEvidenciaCapturada(selectedImageUriString, latitud, longitud)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            obtenerCoordenadasGPS()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.mostrarNotificacion(
                context,
                "PréstamoLab CTMA",
                "Recordatorio: No olvides devolver el equipo a tiempo."
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Capacidades del Dispositivo",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedImageUriString == null) "Adjuntar Evidencia Fotográfica" else "Cambiar Foto")
            }

            selectedImageUriString?.let { uriPath ->
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = uriPath,
                    contentDescription = "Evidencia",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    val tienePermiso = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (tienePermiso) {
                        obtenerCoordenadasGPS()
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (latitud == null) "Obtener Ubicación Actual (GPS)"
                    else "Ubicación: $latitud, $longitud"
                )
            }

            TextButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val tienePermisoNotif = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (tienePermisoNotif) {
                            NotificationHelper.mostrarNotificacion(
                                context,
                                "PréstamoLab CTMA",
                                "Recordatorio: No olvides devolver el equipo a tiempo."
                            )
                        } else {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        NotificationHelper.mostrarNotificacion(
                            context,
                            "PréstamoLab CTMA",
                            "Recordatorio: No olvides devolver el equipo a tiempo."
                        )
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Enviar recordatorio de prueba")
            }
        }
    }
}