package com.example.prstamolabctma.model

data class SolicitudPrestamo(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud,
    val evidenciaUri: String? = null,         // 👈 Añadido para la Guía 9 (Evidencia fotográfica por URI)
    val dispositivoBluetooth: String? = null   // 👈 Añadido para la Guía 9 (Capacidad física Bluetooth)
)