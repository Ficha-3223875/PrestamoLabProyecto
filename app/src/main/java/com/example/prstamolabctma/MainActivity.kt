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
import com.example.prstamolabctma.data.repository.RoomPrestamoRepository
import com.example.prstamolabctma.navigation.NavGraph
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PrestamoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getInstance(applicationContext)
                val repository = RoomPrestamoRepository(db.equipoDao(), db.solicitudDao())
                @Suppress("UNCHECKED_CAST")
                return PrestamoViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Sembrar datos iniciales si la base de datos está vacía (Semana 6)
            LaunchedEffect(Unit) {
                val db = AppDatabase.getInstance(applicationContext)
                RoomPrestamoRepository(db.equipoDao(), db.solicitudDao()).sembrarSiVacio()
                // Nota: Ya no se requiere viewModel.cargarEquipos() porque el Flow es reactivo (Semana 7)
            }

            val navController = rememberNavController()
            NavGraph(navController, viewModel)
        }
    }
}