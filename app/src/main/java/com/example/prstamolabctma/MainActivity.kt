package com.example.prstamolabctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.prstamolabctma.data.repository.InMemoryPrestamoRepository
import com.example.prstamolabctma.navigation.NavGraph
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PrestamoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = InMemoryPrestamoRepository()
                return PrestamoViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Inicializar equipos al arrancar
            viewModel.cargarEquipos()

            // Crear NavController y pasar al NavGraph
            val navController = rememberNavController()
            NavGraph(navController, viewModel)
        }
    }
}
