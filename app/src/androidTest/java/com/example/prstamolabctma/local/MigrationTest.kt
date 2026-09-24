package com.example.prstamolabctma.local

import android.content.Context
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.prstamolabctma.data.local.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @Test
    fun migrar1a2ConservaDatos() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        // Crear esquema versión 1 manualmente
        val dbV1 = FrameworkSQLiteOpenHelperFactory().create(
            androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DB)
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `equipos` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `categoria` TEXT NOT NULL, `estado` TEXT NOT NULL, PRIMARY KEY(`id`))"
                        )
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `solicitudes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `equipoId` INTEGER NOT NULL, `ambienteDestino` TEXT NOT NULL, `proposito` TEXT NOT NULL, `duracionHoras` INTEGER NOT NULL, `estado` TEXT NOT NULL)"
                        )
                    }

                    override fun onUpgrade(
                        db: androidx.sqlite.db.SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) {}
                })
                .build()
        ).writableDatabase

        // Insertar datos en v1
        dbV1.execSQL(
            "INSERT INTO solicitudes (id, equipoId, ambienteDestino, proposito, duracionHoras, estado) VALUES (1, 2, 'Lab Redes', 'Configuración de switches', 3, 'SOLICITADA')"
        )
        dbV1.close()

        // Ejecutar migración v1 -> v2
        val dbV2 = FrameworkSQLiteOpenHelperFactory().create(
            androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DB)
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(2) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {}
                    override fun onUpgrade(
                        db: androidx.sqlite.db.SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) {
                        if (oldVersion == 1 && newVersion == 2) {
                            AppDatabase.MIGRATION_1_2.migrate(db)
                        }
                    }
                })
                .build()
        ).writableDatabase

        // Verificar que la nueva columna fechaCreacion existe y los datos v1 se conservaron
        val cursor = dbV2.query("SELECT id, ambienteDestino, fechaCreacion FROM solicitudes WHERE id = 1")
        assertTrue(cursor.moveToFirst())
        assertEquals(1, cursor.getInt(0))
        assertEquals("Lab Redes", cursor.getString(1))
        // fechaCreacion tiene valor por defecto 0
        assertEquals(0L, cursor.getLong(2))
        cursor.close()
        dbV2.close()
    }
}
