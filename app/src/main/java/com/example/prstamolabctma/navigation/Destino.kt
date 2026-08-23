package com.example.prstamolabctma.navigation

sealed class Destino(val ruta: String) {
    object Catalogo : Destino("catalogo")
    object Equipo : Destino("equipo/{equipoId}") {
        fun crearRuta(equipoId: Int) = "equipo/$equipoId"
    }
    object Solicitud : Destino("solicitud/{equipoId}") {
        fun crearRuta(equipoId: Int) = "solicitud/$equipoId"
    }
    object MisSolicitudes : Destino("misSolicitudes")
}
