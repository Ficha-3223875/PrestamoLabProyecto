package com.example.prstamolabctma.data.device

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class BluetoothHelper(private val context: Context) {

    fun esBluetoothDisponible(): Boolean {
        // Validación básica de que el dispositivo soporta Bluetooth
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
    }

    fun obtenerDispositivosEmparejados(): List<String> {
        // En versiones modernas de Android (Android 12+) requiere permisos BLUETOOTH_CONNECT
        // Aquí retornamos una lista simulada o vacía por seguridad si faltan permisos de runtime,
        // o puedes integrar el adaptador bluetooth nativo si ya lo declaraste en el Manifest.
        return listOf("Sensor Lab 01", "Dispositivo BLE Genérico", "Tablet Préstamos")
    }
}