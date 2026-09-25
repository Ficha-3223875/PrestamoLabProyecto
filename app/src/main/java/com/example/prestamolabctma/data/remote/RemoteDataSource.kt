package com.example.prestamolabctma.data.remote

import com.example.prestamolabctma.data.remote.dto.EquipoDto
import java.io.IOException

/**
 * Fuente de datos remota para orquestación de llamadas REST y evidencia fotográfica (Semana 8 & 9)
 */
class RemoteDataSource(
    private val apiService: EquipoApiService? = null
) {
    var simularFalloRed: Boolean = false

    suspend fun obtenerEquiposRemotos(): List<EquipoDto> {
        if (simularFalloRed) {
            throw IOException("Fallo de red simulado: No se pudo conectar al servidor REST.")
        }

        if (apiService != null) {
            return try {
                apiService.obtenerEquipos()
            } catch (e: Exception) {
                throw IOException("Error al comunicarse con la API: ${e.message}", e)
            }
        }

        return listOf(
            EquipoDto(1, "Kit Arduino UNO", "ELECTRONICA", "Kit para prácticas de electrónica y programación con microcontroladores.", "DISPONIBLE"),
            EquipoDto(2, "Portátil Lenovo", "COMPUTO", "Equipo portátil para actividades académicas de software.", "DISPONIBLE"),
            EquipoDto(3, "Multímetro Digital", "MEDICION", "Instrumento de alta precisión para realizar mediciones eléctricas.", "DISPONIBLE"),
            EquipoDto(4, "Proyector Epson", "AUDIOVISUAL", "Proyector Full HD para presentaciones y clases magistrales.", "RESERVADO"),
            EquipoDto(5, "Taladro Eléctrico", "HERRAMIENTA", "Herramienta industrial para prácticas de mantenimiento.", "DISPONIBLE"),
            EquipoDto(6, "Osciloscopio Digital", "MEDICION", "Osciloscopio de 2 canales 100MHz para laboratorio.", "DISPONIBLE")
        )
    }

    suspend fun subirEvidenciaRemota(solicitudId: Int, uriString: String): Result<String> {
        if (simularFalloRed) {
            return Result.failure(IOException("Fallo de red: La evidencia se mantendrá almacenada localmente."))
        }

        if (uriString.isBlank()) {
            return Result.failure(IllegalArgumentException("URI de evidencia no válida."))
        }

        if (uriString.contains("archivo_grande_excedido")) {
            return Result.failure(IllegalArgumentException("El archivo excede el tamaño máximo permitido de 10 MB."))
        }

        return Result.success("https://api.ctma.sena.edu.co/evidencias/$solicitudId/foto.jpg")
    }
}
