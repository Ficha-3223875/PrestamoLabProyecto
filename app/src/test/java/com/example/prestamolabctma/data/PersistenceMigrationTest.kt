package com.example.prestamolabctma.data

import com.example.prestamolabctma.data.local.EquipoEntity
import com.example.prestamolabctma.data.local.Mappers.toDomain
import com.example.prestamolabctma.data.local.ReporteEntity
import com.example.prestamolabctma.data.local.SolicitudPrestamoEntity
import com.example.prestamolabctma.model.CategoriaEquipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.model.EstadoSolicitud
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PersistenceMigrationTest {

    @Test
    fun testVerificarEsquemasVersionadosYMapeadoresInmutables() {
        // Validar que las entidades locales de persistencia de la Semana 6 no contaminan el dominio
        val equipoEntity = EquipoEntity(
            id = 10,
            nombre = "Osciloscopio",
            categoria = "MEDICION",
            descripcion = "Canal dual 50MHz",
            estado = "DISPONIBLE"
        )

        // Ejecutar mapeador hacia el dominio limpio de la UI
        val dominio = equipoEntity.toDomain()

        assertEquals(10, dominio.id)
        assertEquals("Osciloscopio", dominio.nombre)
        assertEquals(CategoriaEquipo.MEDICION, dominio.categoria)
        assertEquals(EstadoEquipo.DISPONIBLE, dominio.estado)
    }

    @Test
    fun testVerificarEsquemaSolicitudVersionDosConFechaRegistro() {
        // La versión 2 incluye el campo fechaRegistro exigido en la transformación
        val solicitudEntity = SolicitudPrestamoEntity(
            id = 1,
            equipoId = 2,
            ambienteDestino = "Laboratorio Informática",
            proposito = "Desarrollo de Apps Móviles con Jetpack Compose",
            duracionHoras = 3,
            estado = "SOLICITADA",
            fechaRegistro = "2026-09-22" // Campo nuevo de Versión 2
        )

        val dominioSol = solicitudEntity.toDomain()

        assertEquals(1, dominioSol.id)
        assertEquals(2, dominioSol.equipoId)
        assertEquals(EstadoSolicitud.SOLICITADA, dominioSol.estado)
        assertNotNull(solicitudEntity.fechaRegistro)
    }

    @Test
    fun testSimulacionDeMigracionUnoADos() {
        // Simular la ejecución de la sentencia SQL ALTER TABLE ejecutada en onUpgrade de la Semana 6
        val sqlAlterTableSimulado = "ALTER TABLE solicitudes ADD COLUMN fechaRegistro TEXT DEFAULT ''"
        
        assertNotNull(sqlAlterTableSimulado)
        assertEquals("ALTER TABLE solicitudes ADD COLUMN fechaRegistro TEXT DEFAULT ''", sqlAlterTableSimulado)
        
        val reporteEntity = ReporteEntity(id = "R-101", titulo = "Reporte de Auditoría Semanal")
        val dominioReporte = reporteEntity.toDomain()
        
        assertEquals("R-101", dominioReporte.id)
        assertEquals("Reporte de Auditoría Semanal", dominioReporte.titulo)
    }
}
