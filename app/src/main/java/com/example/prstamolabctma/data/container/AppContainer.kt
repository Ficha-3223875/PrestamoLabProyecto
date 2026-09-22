package com.example.prstamolabctma.data

import android.content.Context
import com.example.prstamolabctma.data.local.AppDatabase
import com.example.prstamolabctma.data.repository.OfflineFirstPrestamoRepository
import com.example.prstamolabctma.data.repository.PrestamoRepository

interface AppContainer {
    val prestamoRepository: PrestamoRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    // 1. Obtenemos la instancia de la base de datos Room
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    // 2. Inyectamos los DAOs dentro de nuestro repositorio
    override val prestamoRepository: PrestamoRepository by lazy {
        OfflineFirstPrestamoRepository(
            equipoDao = database.equipoDao(),
            solicitudDao = database.solicitudPrestamoDao()
        )
    }
}