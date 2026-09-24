package com.example.prstamolabctma.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.prstamolabctma.data.local.AppDatabase
import com.example.prstamolabctma.data.local.EquipoDao
import com.example.prstamolabctma.data.local.EquipoEntity
import com.example.prstamolabctma.data.local.SolicitudDao
import com.example.prstamolabctma.data.local.SolicitudEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DaoTest {

    private lateinit var db: AppDatabase
    private lateinit var equipoDao: EquipoDao
    private lateinit var solicitudDao: SolicitudDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        equipoDao = db.equipoDao()
        solicitudDao = db.solicitudDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarYObtenerEquipos() = runBlocking {
        val equipos = listOf(
            EquipoEntity(1, "Multímetro", "ELECTRONICA", "DISPONIBLE"),
            EquipoEntity(2, "Laptop", "INFORMATICA", "DISPONIBLE")
        )
        equipoDao.insertEquipos(equipos)

        val result = equipoDao.getEquiposList()
        assertEquals(2, result.size)
        assertEquals("Multímetro", result[0].nombre)
    }

    @Test
    fun actualizarEstadoEquipo() = runBlocking {
        val equipo = EquipoEntity(1, "Multímetro", "ELECTRONICA", "DISPONIBLE")
        equipoDao.insertEquipo(equipo)

        equipoDao.updateEstadoEquipo(1, "RESERVADO")

        val actualizado = equipoDao.getEquipoById(1)
        assertNotNull(actualizado)
        assertEquals("RESERVADO", actualizado?.estado)
    }

    @Test
    fun insertarYObtenerSolicitudes() = runBlocking {
        val solicitud = SolicitudEntity(
            id = 1,
            equipoId = 1,
            ambienteDestino = "Lab 1",
            proposito = "Uso de prueba de medición",
            duracionHoras = 2,
            estado = "SOLICITADA"
        )
        solicitudDao.insertSolicitud(solicitud)

        val lista = solicitudDao.getSolicitudesList()
        assertEquals(1, lista.size)
        assertEquals("Lab 1", lista[0].ambienteDestino)

        val conteoActivas = solicitudDao.countSolicitudesActivasPorEquipo(1, "SOLICITADA")
        assertEquals(1, conteoActivas)
    }
}
