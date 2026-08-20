package com.example.miformacionctma.model

enum class CategoriaEquipo {
    ELECTRONICA,
    COMPUTACION,
    HERRAMIENTAS,
    AUDIOVISUAL,
    OTROS
}

enum class EstadoEquipo {
    DISPONIBLE,
    RESERVADO,
    PRESTADO
}

enum class EstadoSolicitud {
    SOLICITADA,
    APROBADA,
    ENTREGADA,
    DEVUELTA,
    CANCELADA,
    RECHAZADA
}