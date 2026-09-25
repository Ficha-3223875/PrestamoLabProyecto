package com.example.prestamolabctma.viewmodel

/**
 * Estados de la pantalla/contenido principal (Exigidos por la Guía de Aprendizaje - Semana 7)
 */
sealed interface EstadoPantalla<out T> {
    object Cargando : EstadoPantalla<Nothing>
    data class Contenido<T>(val datos: T) : EstadoPantalla<T>
    data class Vacio(val mensaje: String = "No se encontraron elementos disponibles.") : EstadoPantalla<Nothing>
    data class Error(val mensaje: String) : EstadoPantalla<Nothing>
}

/**
 * Estados de operación asíncrona (Exigidos por la Guía de Aprendizaje - Semana 7)
 */
sealed interface EstadoOperacion {
    object Inactiva : EstadoOperacion
    object EnCurso : EstadoOperacion
    data class Exitosa(val mensaje: String) : EstadoOperacion
    data class Fallida(val mensaje: String) : EstadoOperacion
}
