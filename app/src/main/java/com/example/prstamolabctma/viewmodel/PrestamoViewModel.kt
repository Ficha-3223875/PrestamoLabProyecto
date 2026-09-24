package com.example.prstamolabctma.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.prstamolabctma.data.local.AppDatabase
import com.example.prstamolabctma.data.preferences.UserPreferencesRepository
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.repository.PrestamoRepository
import com.example.prstamolabctma.repository.RoomPrestamoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PrestamoViewModel(
    application: Application,
    private val repo: PrestamoRepository,
    private val preferencesRepo: UserPreferencesRepository
) : AndroidViewModel(application) {

    val categoriaFiltro: StateFlow<CategoriaEquipo?> = preferencesRepo.categoriaFiltroFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val equipos: StateFlow<List<Equipo>> = combine(
        repo.obtenerEquiposFlow(),
        preferencesRepo.categoriaFiltroFlow
    ) { listaEquipos, filtro ->
        if (filtro == null) {
            listaEquipos
        } else {
            listaEquipos.filter { it.categoria == filtro }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val solicitudes: StateFlow<List<SolicitudPrestamo>> = repo.obtenerSolicitudesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun seleccionarCategoriaFiltro(categoria: CategoriaEquipo?) {
        viewModelScope.launch {
            preferencesRepo.guardarCategoriaFiltro(categoria)
        }
    }

    fun crearSolicitud(solicitud: SolicitudPrestamo, onResultado: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repo.crearSolicitud(solicitud)
            onResultado(result)
        }
    }

    fun cancelarSolicitud(id: Int, onResultado: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            val result = repo.cancelarSolicitud(id)
            onResultado(result)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getDatabase(application)
            val repo = RoomPrestamoRepository(db.equipoDao(), db.solicitudDao())
            val preferencesRepo = UserPreferencesRepository(application)
            return PrestamoViewModel(application, repo, preferencesRepo) as T
        }
    }
}
