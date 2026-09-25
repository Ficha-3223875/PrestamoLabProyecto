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
class Migration2To3Test {

    private val TEST_DB = "migration-2-3-test"

    @Test
    fun migrar2a3CreaTablaEvidencias() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val dbV2 = FrameworkSQLiteOpenHelperFactory().create(
            androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DB)
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(2) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `equipos` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `categoria` TEXT NOT NULL, `estado` TEXT NOT NULL, PRIMARY KEY(`id`))"
                        )
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `solicitudes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `equipoId` INTEGER NOT NULL, `ambienteDestino` TEXT NOT NULL, `proposito` TEXT NOT NULL, `duracionHoras` INTEGER NOT NULL, `estado` TEXT NOT NULL, `fechaCreacion` INTEGER NOT NULL)"
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

        dbV2.execSQL(
            "INSERT INTO solicitudes (id, equipoId, ambienteDestino, proposito, duracionHoras, estado, fechaCreacion) VALUES (1, 2, 'Lab 1', 'Propósito de prueba', 2, 'SOLICITADA', 1000)"
        )
        dbV2.close()

        val dbV3 = FrameworkSQLiteOpenHelperFactory().create(
            androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DB)
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(3) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {}
                    override fun onUpgrade(
                        db: androidx.sqlite.db.SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) {
                        if (oldVersion == 2 && newVersion == 3) {
                            AppDatabase.MIGRATION_2_3.migrate(db)
                        }
                    }
                })
                .build()
        ).writableDatabase

        val cursorSolicitudes = dbV3.query("SELECT id FROM solicitudes WHERE id = 1")
        assertTrue(cursorSolicitudes.moveToFirst())
        cursorSolicitudes.close()

        dbV3.execSQL(
            "INSERT INTO evidencias (solicitudId, uri, tipoMime, tamanoBytes, estado, fechaCreacion) VALUES (1, 'content://test/foto.jpg', 'image/jpeg', 2048, 'LOCAL', 2000)"
        )

        val cursorEvidencias = dbV3.query("SELECT uri, estado FROM evidencias WHERE solicitudId = 1")
        assertTrue(cursorEvidencias.moveToFirst())
        assertEquals("content://test/foto.jpg", cursorEvidencias.getString(0))
        assertEquals("LOCAL", cursorEvidencias.getString(1))
        cursorEvidencias.close()

        dbV3.close()
    }
}
