package com.example.prstamolabctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.prstamolabctma.data.local.AppDatabase
import com.example.prstamolabctma.data.remote.ApiClient
import com.example.prstamolabctma.data.repository.RoomPrestamoRepository
import com.example.prstamolabctma.navigation.NavGraph
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PrestamoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getInstance(applicationContext)

                // 👇 Inyectamos ApiClient.retrofitService al repositorio (Guía 8)
                val repository = RoomPrestamoRepository(
                    equipoDao = db.equipoDao(),
                    solicitudDao = db.solicitudDao(),
                    apiService = ApiClient.retrofitService
                )

                @Suppress("UNCHECKED_CAST")
                return PrestamoViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Sembrar datos locales y sincronizar con la API remota (Estrategia Local-First)
            LaunchedEffect(Unit) {
                val db = AppDatabase.getInstance(applicationContext)
                val repository = RoomPrestamoRepository(
                    equipoDao = db.equipoDao(),
                    solicitudDao = db.solicitudDao(),
                    apiService = ApiClient.retrofitService
                )

                // 1. Si está vacío, inserta dato locales básicos
                repository.sembrarSiVacio()

                // 2. Intenta sincronizar con el servicio remoto (API REST)
                repository.sincronizarEquipos()
            }

            val navController = rememberNavController()
            NavGraph(navController, viewModel)
        }
    }
}