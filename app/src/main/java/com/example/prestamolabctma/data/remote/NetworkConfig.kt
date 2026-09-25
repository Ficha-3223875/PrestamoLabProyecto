package com.example.prestamolabctma.data.remote

/**
 * Configuración de entornos de red (DESARROLLO, PRUEBAS, PRODUCCION - Semana 9)
 * Garantiza el uso obligatorio de HTTPS en producción y seguridad en registros.
 */
enum class EntornoRed(val baseUrl: String, val requiereHttps: Boolean) {
    DESARROLLO("http://10.0.2.2:8080/", false),
    PRUEBAS("https://testing.ctma.sena.edu.co/", true),
    PRODUCCION("https://api.ctma.sena.edu.co/", true)
}

object NetworkConfig {
    var entornoActual: EntornoRed = EntornoRed.DESARROLLO

    fun obtenerBaseUrl(): String = entornoActual.baseUrl

    fun esProduccion(): Boolean = entornoActual == EntornoRed.PRODUCCION

    fun validarSeguridadEntorno(): Boolean {
        return if (esProduccion()) {
            entornoActual.requiereHttps && entornoActual.baseUrl.startsWith("https://")
        } else {
            true
        }
    }
}
