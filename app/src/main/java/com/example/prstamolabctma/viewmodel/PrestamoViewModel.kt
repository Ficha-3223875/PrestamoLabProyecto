package com.example.prstamolabctma.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.prstamolabctma.data.local.AppDatabase
import com.example.prstamolabctma.data.preferences.PreferencesRepository
import com.example.prstamolabctma.data.preferences.UserPreferencesRepository
import com.example.prstamolabctma.data.remote.RetrofitClient
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEvidencia
import com.example.prstamolabctma.model.Evidencia
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.repository.PrestamoRepository
import com.example.prstamolabctma.repository.RoomPrestamoRepository
import com.example.prstamolabctma.ui.state.OperationState
import com.example.prstamolabctma.ui.state.UiState
import com.example.prstamolabctma.util.EvidenciaValidator
import com.example.prstamolabctma.util.NotificationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PrestamoViewModel(
    application: Application,
    private val repo: PrestamoRepository,
    private val preferencesRepo: PreferencesRepository
) : AndroidViewModel(application) {

    val categoriaFiltro: StateFlow<CategoriaEquipo?> = preferencesRepo.categoriaFiltroFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val equiposUiState: StateFlow<UiState<List<Equipo>>> = combine(
        repo.obtenerEquiposFlow(),
        preferencesRepo.categoriaFiltroFlow
    ) { listaEquipos, filtro ->
        val listaFiltrada = if (filtro == null) {
            listaEquipos
        } else {
            listaEquipos.filter { it.categoria == filtro }
        }
        if (listaFiltrada.isEmpty()) {
            UiState.Vacio
        } else {
            UiState.Contenido(listaFiltrada)
        }
    }
        .catch { throwable ->
            emit(UiState.Error(throwable.message ?: "Error al cargar equipos"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Cargando
        )

    val solicitudesUiState: StateFlow<UiState<List<SolicitudPrestamo>>> = repo.obtenerSolicitudesFlow()
        .map { lista ->
            if (lista.isEmpty()) {
                UiState.Vacio
            } else {
                UiState.Contenido(lista)
            }
        }
        .catch { throwable ->
            emit(UiState.Error(throwable.message ?: "Error al cargar solicitudes"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Cargando
        )

    private val _operacionState = MutableStateFlow<OperationState>(OperationState.Inactiva)
    val operacionState: StateFlow<OperationState> = _operacionState.asStateFlow()

    private val _refreshState = MutableStateFlow<OperationState>(OperationState.Inactiva)
    val refreshState: StateFlow<OperationState> = _refreshState.asStateFlow()

    fun resetearEstadoOperacion() {
        _operacionState.value = OperationState.Inactiva
    }

    fun resetearEstadoRefresh() {
        _refreshState.value = OperationState.Inactiva
    }

    fun seleccionarCategoriaFiltro(categoria: CategoriaEquipo?) {
        viewModelScope.launch {
            preferencesRepo.guardarCategoriaFiltro(categoria)
        }
    }

    fun refrescarDatos() {
        viewModelScope.launch {
            _refreshState.value = OperationState.EnCurso
            val result = repo.refrescarDatos()
            if (result.isSuccess) {
                _refreshState.value = OperationState.Exitosa("Datos sincronizados correctamente")
            } else {
                val mensaje = result.exceptionOrNull()?.message ?: "Sin conexión a la red. Mostrando datos locales."
                _refreshState.value = OperationState.Fallida("Sin conexión. Mostrando datos en caché local: $mensaje")
            }
        }
    }

    fun crearSolicitud(solicitud: SolicitudPrestamo) {
        viewModelScope.launch {
            _operacionState.value = OperationState.EnCurso
            val result = repo.crearSolicitud(solicitud)
            if (result.isSuccess) {
                _operacionState.value = OperationState.Exitosa("Solicitud creada con éxito")
            } else {
                val mensaje = result.exceptionOrNull()?.message ?: "Error al crear la solicitud"
                _operacionState.value = OperationState.Fallida(mensaje)
            }
        }
    }

    fun cancelarSolicitud(id: Int) {
        viewModelScope.launch {
            _operacionState.value = OperationState.EnCurso
            val result = repo.cancelarSolicitud(id)
            if (result.isSuccess) {
                _operacionState.value = OperationState.Exitosa("Solicitud cancelada")
            } else {
                val mensaje = result.exceptionOrNull()?.message ?: "Error al cancelar la solicitud"
                _operacionState.value = OperationState.Fallida(mensaje)
            }
        }
    }

    // --- Métodos de Evidencias Fotográficas ---

    fun obtenerEvidenciasPorSolicitudFlow(solicitudId: Int): Flow<UiState<List<Evidencia>>> {
        return repo.obtenerEvidenciasPorSolicitudFlow(solicitudId)
            .map { lista ->
                if (lista.isEmpty()) {
                    UiState.Vacio
                } else {
                    UiState.Contenido(lista)
                }
            }
            .catch { throwable ->
                emit(UiState.Error(throwable.message ?: "Error al cargar evidencias"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Cargando
            )
    }

    fun adjuntarEvidencia(solicitudId: Int, context: Context, uri: Uri) {
        val validacion = EvidenciaValidator.validarEvidencia(context, uri)
        if (validacion.isFailure) {
            val msg = validacion.exceptionOrNull()?.message ?: "Imagen no válida"
            _operacionState.value = OperationState.Fallida(msg)
            return
        }

        val metadatos = validacion.getOrNull() ?: return
        val evidencia = Evidencia(
            id = 0,
            solicitudId = solicitudId,
            uri = metadatos.uri,
            tipoMime = metadatos.tipoMime,
            tamanoBytes = metadatos.tamanoBytes,
            estado = EstadoEvidencia.LOCAL
        )

        viewModelScope.launch {
            _operacionState.value = OperationState.EnCurso
            val res = repo.adjuntarEvidencia(evidencia)
            if (res.isSuccess) {
                _operacionState.value = OperationState.Exitosa("Evidencia fotográfica adjuntada")
            } else {
                val msg = res.exceptionOrNull()?.message ?: "Error al adjuntar evidencia"
                _operacionState.value = OperationState.Fallida(msg)
            }
        }
    }

    fun eliminarEvidencia(id: Int) {
        viewModelScope.launch {
            _operacionState.value = OperationState.EnCurso
            val res = repo.eliminarEvidencia(id)
            if (res.isSuccess) {
                _operacionState.value = OperationState.Exitosa("Evidencia eliminada")
            } else {
                val msg = res.exceptionOrNull()?.message ?: "Error al eliminar evidencia"
                _operacionState.value = OperationState.Fallida(msg)
            }
        }
    }

    fun reintentarSubidaEvidencia(id: Int) {
        viewModelScope.launch {
            _operacionState.value = OperationState.EnCurso
            val res = repo.reintentarSubidaEvidencia(id)
            if (res.isSuccess) {
                _operacionState.value = OperationState.Exitosa("Evidencia re-sincronizada")
            } else {
                val msg = res.exceptionOrNull()?.message ?: "Error al reintentar subida"
                _operacionState.value = OperationState.Fallida(msg)
            }
        }
    }

    fun activarRecordatorioNotificacion(context: Context, solicitudId: Int) {
        NotificationHelper.enviarNotificacionRecordatorio(
            context = context,
            idNotificacion = solicitudId,
            titulo = "Recordatorio de Préstamo #$solicitudId",
            mensaje = "Recordatorio activo para el retorno del equipo según horario programado."
        )
        _operacionState.value = OperationState.Exitosa("Recordatorio activado")
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getDatabase(application)
            val api = RetrofitClient.apiService
            val repo = RoomPrestamoRepository(db.equipoDao(), db.solicitudDao(), db.evidenciaDao(), api)
            val preferencesRepo = UserPreferencesRepository(application)
            return PrestamoViewModel(application, repo, preferencesRepo) as T
        }
    }
}
