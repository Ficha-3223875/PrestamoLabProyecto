package com.example.prestamolabctma.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.prestamolabctma.model.CategoriaEquipo
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.SolicitudPrestamo
import com.example.prestamolabctma.evaluacion.Reporte
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- 1. ENTIDADES LOCALES (Mapeadores para no contaminar el dominio ni la UI) ---
data class EquipoEntity(val id: Int, val nombre: String, val categoria: String, val descripcion: String, val estado: String)

data class SolicitudPrestamoEntity(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String,
    val fechaRegistro: String = "",
    val evidenciaUri: String? = null,
    val estadoEvidencia: String = "Local"
)

data class ReporteEntity(val id: String, val titulo: String)

class BaseDatosLocal(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "prestamolab_reportactma.db"
        
        // Versión del esquema para demostrar Migración 1 -> 2 -> 3 (Semana 9)
        const val DATABASE_VERSION = 3

        // Tablas
        const val TABLA_EQUIPOS = "equipos"
        const val TABLA_SOLICITUDES = "solicitudes"
        const val TABLA_REPORTES = "reportes"
    }

    // Estado reactivo interno para emular la invalidación de Room mediante Flow
    private val _cambiosNotifier = MutableStateFlow(0)
    val cambiosNotifier: StateFlow<Int> = _cambiosNotifier.asStateFlow()

    fun notificarCambio() {
        _cambiosNotifier.value = _cambiosNotifier.value + 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // ESQUEMA EN VERSIÓN 3
        db.execSQL("""
            CREATE TABLE $TABLA_EQUIPOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                categoria TEXT,
                descripcion TEXT,
                estado TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLA_SOLICITUDES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                equipoId INTEGER,
                ambienteDestino TEXT,
                proposito TEXT,
                duracionHoras INTEGER,
                estado TEXT,
                fechaRegistro TEXT DEFAULT '',
                evidenciaUri TEXT DEFAULT NULL,
                estadoEvidencia TEXT DEFAULT 'Local'
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLA_REPORTES (
                id TEXT PRIMARY KEY,
                titulo TEXT
            )
        """.trimIndent())

        // Poblar datos iniciales obligatorios del catálogo
        poblarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // LOGICA DE MIGRACIÓN VERSIONADA EXIGIDA POR LA GUÍA
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE $TABLA_SOLICITUDES ADD COLUMN fechaRegistro TEXT DEFAULT ''")
            } catch (e: Exception) {}
        }
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE $TABLA_SOLICITUDES ADD COLUMN evidenciaUri TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE $TABLA_SOLICITUDES ADD COLUMN estadoEvidencia TEXT DEFAULT 'Local'")
            } catch (e: Exception) {}
        }
    }

    private fun poblarDatosIniciales(db: SQLiteDatabase) {
        val valores = listOf(
            ContentValues().apply {
                put("id", 1)
                put("nombre", "Kit Arduino UNO")
                put("categoria", "ELECTRONICA")
                put("descripcion", "Kit para prácticas de electrónica y programación.")
                put("estado", "DISPONIBLE")
            },
            ContentValues().apply {
                put("id", 2)
                put("nombre", "Portátil Lenovo")
                put("categoria", "COMPUTO")
                put("descripcion", "Equipo portátil para actividades académicas.")
                put("estado", "DISPONIBLE")
            },
            ContentValues().apply {
                put("id", 3)
                put("nombre", "Multímetro Digital")
                put("categoria", "MEDICION")
                put("descripcion", "Instrumento para realizar mediciones eléctricas.")
                put("estado", "DISPONIBLE")
            },
            ContentValues().apply {
                put("id", 4)
                put("nombre", "Proyector Epson")
                put("categoria", "AUDIOVISUAL")
                put("descripcion", "Proyector para presentaciones y clases.")
                put("estado", "RESERVADO")
            },
            ContentValues().apply {
                put("id", 5)
                put("nombre", "Taladro Eléctrico")
                put("categoria", "HERRAMIENTA")
                put("descripcion", "Herramienta para prácticas de mantenimiento.")
                put("estado", "DISPONIBLE")
            }
        )
        for (v in valores) {
            db.insert(TABLA_EQUIPOS, null, v)
        }
    }

    // --- DAO: ACCESO SEGURO A DATOS ---
    
    fun listarEquiposRaw(): List<EquipoEntity> {
        val lista = mutableListOf<EquipoEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLA_EQUIPOS", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(EquipoEntity(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun obtenerEquipoRaw(id: Int): EquipoEntity? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLA_EQUIPOS WHERE id = ?", arrayOf(id.toString()))
        var entity: EquipoEntity? = null
        if (cursor.moveToFirst()) {
            entity = EquipoEntity(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            )
        }
        cursor.close()
        return entity
    }

    fun actualizarEstadoEquipoRaw(id: Int, nuevoEstado: String) {
        val db = writableDatabase
        val cv = ContentValues().apply { put("estado", nuevoEstado) }
        db.update(TABLA_EQUIPOS, cv, "id = ?", arrayOf(id.toString()))
        notificarCambio()
    }

    fun reemplazarEquiposRaw(nuevosEquipos: List<EquipoEntity>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (eq in nuevosEquipos) {
                val cv = ContentValues().apply {
                    put("id", eq.id)
                    put("nombre", eq.nombre)
                    put("categoria", eq.categoria)
                    put("descripcion", eq.descripcion)
                    put("estado", eq.estado)
                }
                db.insertWithOnConflict(TABLA_EQUIPOS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        notificarCambio()
    }

    fun listarSolicitudesRaw(): List<SolicitudPrestamoEntity> {
        val lista = mutableListOf<SolicitudPrestamoEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLA_SOLICITUDES", null)
        if (cursor.moveToFirst()) {
            do {
                val evidenciaUriCol = cursor.getColumnIndex("evidenciaUri")
                val estadoEvidenciaCol = cursor.getColumnIndex("estadoEvidencia")

                lista.add(SolicitudPrestamoEntity(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    equipoId = cursor.getInt(cursor.getColumnIndexOrThrow("equipoId")),
                    ambienteDestino = cursor.getString(cursor.getColumnIndexOrThrow("ambienteDestino")),
                    proposito = cursor.getString(cursor.getColumnIndexOrThrow("proposito")),
                    duracionHoras = cursor.getInt(cursor.getColumnIndexOrThrow("duracionHoras")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                    fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow("fechaRegistro")),
                    evidenciaUri = if (evidenciaUriCol != -1) cursor.getString(evidenciaUriCol) else null,
                    estadoEvidencia = if (estadoEvidenciaCol != -1) cursor.getString(estadoEvidenciaCol) ?: "Local" else "Local"
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun obtenerSolicitudRaw(id: Int): SolicitudPrestamoEntity? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLA_SOLICITUDES WHERE id = ?", arrayOf(id.toString()))
        var entity: SolicitudPrestamoEntity? = null
        if (cursor.moveToFirst()) {
            val evidenciaUriCol = cursor.getColumnIndex("evidenciaUri")
            val estadoEvidenciaCol = cursor.getColumnIndex("estadoEvidencia")

            entity = SolicitudPrestamoEntity(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                equipoId = cursor.getInt(cursor.getColumnIndexOrThrow("equipoId")),
                ambienteDestino = cursor.getString(cursor.getColumnIndexOrThrow("ambienteDestino")),
                proposito = cursor.getString(cursor.getColumnIndexOrThrow("proposito")),
                duracionHoras = cursor.getInt(cursor.getColumnIndexOrThrow("duracionHoras")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow("fechaRegistro")),
                evidenciaUri = if (evidenciaUriCol != -1) cursor.getString(evidenciaUriCol) else null,
                estadoEvidencia = if (estadoEvidenciaCol != -1) cursor.getString(estadoEvidenciaCol) ?: "Local" else "Local"
            )
        }
        cursor.close()
        return entity
    }

    fun insertarSolicitudRaw(entity: SolicitudPrestamoEntity): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("equipoId", entity.equipoId)
            put("ambienteDestino", entity.ambienteDestino)
            put("proposito", entity.proposito)
            put("duracionHoras", entity.duracionHoras)
            put("estado", entity.estado)
            put("fechaRegistro", entity.fechaRegistro)
            put("evidenciaUri", entity.evidenciaUri)
            put("estadoEvidencia", entity.estadoEvidencia)
        }
        val id = db.insert(TABLA_SOLICITUDES, null, cv)
        notificarCambio()
        return id
    }

    fun actualizarEstadoSolicitudRaw(id: Int, nuevoEstado: String) {
        val db = writableDatabase
        val cv = ContentValues().apply { put("estado", nuevoEstado) }
        db.update(TABLA_SOLICITUDES, cv, "id = ?", arrayOf(id.toString()))
        notificarCambio()
    }

    fun actualizarEvidenciaSolicitudRaw(id: Int, uri: String?, estadoEvidencia: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("evidenciaUri", uri)
            put("estadoEvidencia", estadoEvidencia)
        }
        db.update(TABLA_SOLICITUDES, cv, "id = ?", arrayOf(id.toString()))
        notificarCambio()
    }

    fun listarReportesRaw(): List<ReporteEntity> {
        val lista = mutableListOf<ReporteEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLA_REPORTES", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(ReporteEntity(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun insertarReporteRaw(entity: ReporteEntity) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("id", entity.id)
            put("titulo", entity.titulo)
        }
        db.insertWithOnConflict(TABLA_REPORTES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        notificarCambio()
    }
}

// --- MAPEADORES INMUTABLES ENTIDAD <-> DOMINIO ---
object Mappers {
    fun EquipoEntity.toDomain() = Equipo(
        id = id,
        nombre = nombre,
        categoria = CategoriaEquipo.valueOf(categoria),
        descripcion = descripcion,
        estado = EstadoEquipo.valueOf(estado)
    )

    fun SolicitudPrestamoEntity.toDomain() = SolicitudPrestamo(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = EstadoSolicitud.valueOf(estado),
        evidenciaUri = evidenciaUri,
        estadoEvidencia = estadoEvidencia
    )

    fun ReporteEntity.toDomain() = Reporte(
        id = id,
        titulo = titulo
    )
}
