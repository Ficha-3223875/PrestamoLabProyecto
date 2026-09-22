package com.example.prstamolabctma.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.prstamolabctma.data.local.dao.EquipoDao
import com.example.prstamolabctma.data.local.dao.SolicitudPrestamoDao
import com.example.prstamolabctma.data.local.entity.EquipoEntity
import com.example.prstamolabctma.data.local.entity.SolicitudPrestamoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [EquipoEntity::class, SolicitudPrestamoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun equipoDao(): EquipoDao
    abstract fun solicitudPrestamoDao(): SolicitudPrestamoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prestamolab_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val scope = CoroutineScope(Dispatchers.IO)
                        scope.launch {
                            try {
                                val iniciales = listOf(
                                    EquipoEntity(id = 1, nombre = "Multímetro Digital", categoria = "ELECTRONICA", estado = "DISPONIBLE"),
                                    EquipoEntity(id = 2, nombre = "Cámara Digital", categoria = "AUDIOVISUAL", estado = "DISPONIBLE"),
                                    EquipoEntity(id = 3, nombre = "Taladro Eléctrico", categoria = "HERRAMIENTA", estado = "PRESTADO"),
                                    EquipoEntity(id = 4, nombre = "Tableta", categoria = "COMPUTO", estado = "DISPONIBLE")
                                )
                                INSTANCE?.equipoDao()?.insertEquipos(iniciales)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}