package com.example.prestamolabctma.model

enum class EstadoSolicitud {
    SOLICITADA,
    APROBADA,
    ENTREGADA,
    DEVUELTA,
    CANCELADA,
    RECHAZADA
}

data class SolicitudPrestamo(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud,
    val evidenciaUri: String? = null,
    val estadoEvidencia: String = "Local" // Local, Subiendo, Sincronizada, Fallida
)
