package com.example.prestamolabctma.evaluacion

import com.example.prestamolabctma.data.local.BaseDatosLocal
import com.example.prestamolabctma.data.local.Mappers.toDomain
import com.example.prestamolabctma.data.local.ReporteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RoomReporteRepository(private val db: BaseDatosLocal) : ReporteRepository {

    private val _reportes = MutableStateFlow<List<Reporte>>(emptyList())
    override val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()

    init {
        actualizarLista()
    }

    private fun actualizarLista() {
        _reportes.value = db.listarReportesRaw().map { it.toDomain() }
    }

    override fun agregar(reporte: Reporte) {
        val entity = ReporteEntity(id = reporte.id, titulo = reporte.titulo)
        db.insertarReporteRaw(entity)
        actualizarLista()
    }
}
