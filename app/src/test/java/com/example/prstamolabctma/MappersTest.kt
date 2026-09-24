package com.example.prstamolabctma

import com.example.prstamolabctma.data.remote.dto.EquipoDto
import com.example.prstamolabctma.data.remote.dto.toDomain
import com.example.prstamolabctma.data.remote.dto.toDto
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import org.junit.Assert.assertEquals
import org.junit.Test

class MappersTest {

    @Test
    fun equipoDto_toDomain_mapeaCorrectamente() {
        // Given
        val dto = EquipoDto(
            id = 1,
            nombre = "Multímetro Digital",
            categoria = "HERRAMIENTA",
            estado = "DISPONIBLE"
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(1, domain.id)
        assertEquals("Multímetro Digital", domain.nombre)
        assertEquals(CategoriaEquipo.HERRAMIENTA, domain.categoria)
        assertEquals(EstadoEquipo.DISPONIBLE, domain.estado)
    }

    @Test
    fun equipo_toDto_mapeaCorrectamente() {
        // Given
        val equipo = Equipo(
            id = 2,
            nombre = "Osciloscopio",
            categoria = CategoriaEquipo.HERRAMIENTA,
            estado = EstadoEquipo.PRESTADO
        )

        // When
        val dto = equipo.toDto()

        // Then
        assertEquals(2, dto.id)
        assertEquals("Osciloscopio", dto.nombre)
        assertEquals("HERRAMIENTA", dto.categoria)
        assertEquals("PRESTADO", dto.estado)
    }
}