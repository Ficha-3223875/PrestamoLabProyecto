package com.example.prstamolabctma.model

data class Evidencia(
    val id: Int = 0,
    val solicitudId: Int,
    val uri: String,
    val tipoMime: String,
    val tamanoBytes: Long,
    val estado: EstadoEvidencia,
    val fechaCreacion: Long = System.currentTimeMillis()
)

enum class EstadoEvidencia {
    LOCAL,
    SUBIENDO,
    SINCRONIZADA,
    FALLIDA
}
