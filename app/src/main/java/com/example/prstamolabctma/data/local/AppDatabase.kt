package com.example.prstamolabctma.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.prstamolabctma.data.local.dao.EquipoDao
import com.example.prstamolabctma.data.local.dao.SolicitudDao
import com.example.prstamolabctma.data.local.entity.EquipoEntity
import com.example.prstamolabctma.data.local.entity.SolicitudEntity

@Database(
    entities = [EquipoEntity::class, SolicitudEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun equipoDao(): EquipoDao
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prestamolab.db"
                ).build().also { INSTANCE = it }
            }
    }
}