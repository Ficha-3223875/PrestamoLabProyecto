package com.example.prstamolabctma.model

enum class CategoriaEquipo {
    ELECTRONICA,
    COMPUTO,
    HERRAMIENTA,
    AUDIOVISUAL,
    OTRO
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