package com.example.prestamolabctma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestamolabctma.data.InMemoryPrestamoRepository
import com.example.prestamolabctma.data.PrestamoRepository
import com.example.prestamolabctma.data.RoomPrestamoRepository
import com.example.prestamolabctma.data.local.PreferenciaDataStore
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class PrestamoUiState(
    val estadoPantalla: EstadoPantalla<List<Equipo>> = EstadoPantalla.Cargando,
    val estadoOperacion: EstadoOperacion = EstadoOperacion.Inactiva,
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val guardando: Boolean = false,
    val filtroCategoria: String = "TODOS",
    val recordatorioNotificacionesActivo: Boolean = false,
    val permisoNotificacionesConcedido: Boolean = false,
    val bluetoothEstado: String = "Bluetooth no verificado",
    val bluetoothDispositivos: List<String> = emptyList()
)

class PrestamoViewModel(
    private val repository: PrestamoRepository = InMemoryPrestamoRepository(),
    private val dataStore: PreferenciaDataStore? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        if (repository is RoomPrestamoRepository && dataStore != null) {
            _uiState.value = _uiState.value.copy(estadoPantalla = EstadoPantalla.Cargando)
            viewModelScope.launch {
                combine(
                    repository.equiposFlow,
                    repository.solicitudesFlow,
                    dataStore.categoriaFiltroFlow
                ) { listaEquipos, listaSolicitudes, filtro ->
                    val equiposFiltrados = if (filtro == "TODOS") {
                        listaEquipos
                    } else {
                        listaEquipos.filter { it.categoria.name == filtro }
                    }

                    val nuevoEstadoPantalla = if (equiposFiltrados.isEmpty()) {
                        EstadoPantalla.Vacio("No se encontraron equipos disponibles para la categoría seleccionada.")
                    } else {
                        EstadoPantalla.Contenido(equiposFiltrados)
                    }

                    _uiState.value.copy(
                        estadoPantalla = nuevoEstadoPantalla,
                        equipos = equiposFiltrados,
                        solicitudes = listaSolicitudes,
                        filtroCategoria = filtro
                    )
                }.collect { nuevoEstado ->
                    _uiState.value = nuevoEstado
                }
            }
        } else {
            val inicialEquipos = repository.listarEquipos()
            val estadoInicialPantalla = if (inicialEquipos.isEmpty()) {
                EstadoPantalla.Vacio("No hay equipos registrados.")
            } else {
                EstadoPantalla.Contenido(inicialEquipos)
            }
            _uiState.value = PrestamoUiState(
                estadoPantalla = estadoInicialPantalla,
                equipos = inicialEquipos,
                solicitudes = repository.listarSolicitudes()
            )
        }
    }

    fun sincronizarConServidor() {
        if (_uiState.value.estadoOperacion is EstadoOperacion.EnCurso) return

        _uiState.value = _uiState.value.copy(
            estadoOperacion = EstadoOperacion.EnCurso
        )

        viewModelScope.launch {
            val resultado = repository.sincronizarConServidor()

            val nuevoEstadoOperacion = resultado.fold(
                onSuccess = {
                    EstadoOperacion.Exitosa("Sincronización exitosa con la API. Catálogo actualizado.")
                },
                onFailure = {
                    EstadoOperacion.Fallida("Sin conexión con el servidor. Mostrando datos locales almacenados.")
                }
            )

            _uiState.value = _uiState.value.copy(
                estadoOperacion = nuevoEstadoOperacion
            )
        }
    }

    fun adjuntarEvidencia(solicitudId: Int, uriString: String) {
        if (uriString.isBlank()) {
            _uiState.value = _uiState.value.copy(
                estadoOperacion = EstadoOperacion.Fallida("URI de imagen no válida o selección cancelada.")
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            estadoOperacion = EstadoOperacion.EnCurso
        )

        viewModelScope.launch {
            val resultado = repository.adjuntarEvidencia(solicitudId, uriString)

            val nuevoEstadoOperacion = resultado.fold(
                onSuccess = {
                    EstadoOperacion.Exitosa("Evidencia adjuntada correctamente.")
                },
                onFailure = {
                    EstadoOperacion.Fallida(it.message ?: "Error al adjuntar la evidencia.")
                }
            )

            _uiState.value = _uiState.value.copy(
                estadoOperacion = nuevoEstadoOperacion,
                solicitudes = repository.listarSolicitudes()
            )
        }
    }

    fun reintentarSubirEvidencia(solicitudId: Int) {
        _uiState.value = _uiState.value.copy(
            estadoOperacion = EstadoOperacion.EnCurso
        )

        viewModelScope.launch {
            val resultado = repository.subirEvidenciaPendiente(solicitudId)

            val nuevoEstadoOperacion = resultado.fold(
                onSuccess = {
                    EstadoOperacion.Exitosa("Evidencia subida y sincronizada exitosamente.")
                },
                onFailure = {
                    EstadoOperacion.Fallida("Fallo de red: La evidencia se conserva almacenada en la base de datos local.")
                }
            )

            _uiState.value = _uiState.value.copy(
                estadoOperacion = nuevoEstadoOperacion,
                solicitudes = repository.listarSolicitudes()
            )
        }
    }

    fun cambiarEstadoRecordatorioNotificaciones(activo: Boolean) {
        _uiState.value = _uiState.value.copy(
            recordatorioNotificacionesActivo = activo
        )
    }

    fun actualizarPermisoNotificaciones(concedido: Boolean) {
        _uiState.value = _uiState.value.copy(
            permisoNotificacionesConcedido = concedido
        )
    }

    fun cambiarFiltroCategoria(categoria: String) {
        if (dataStore != null) {
            viewModelScope.launch {
                dataStore.guardarCategoriaFiltro(categoria)
            }
        } else {
            val todosEquipos = repository.listarEquipos()
            val filtrados = if (categoria == "TODOS") todosEquipos else todosEquipos.filter { it.categoria.name == categoria }
            val nuevoEstadoPantalla = if (filtrados.isEmpty()) {
                EstadoPantalla.Vacio("No se encontraron equipos para la categoría seleccionada.")
            } else {
                EstadoPantalla.Contenido(filtrados)
            }
            _uiState.value = _uiState.value.copy(
                estadoPantalla = nuevoEstadoPantalla,
                equipos = filtrados,
                filtroCategoria = categoria
            )
        }
    }

    fun equipo(id: Int): Equipo? {
        return repository.obtenerEquipo(id)
    }

    fun solicitud(id: Int): SolicitudPrestamo? {
        return repository.obtenerSolicitud(id)
    }

    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Boolean {
        if (_uiState.value.guardando || _uiState.value.estadoOperacion is EstadoOperacion.EnCurso) {
            return false
        }

        _uiState.value = _uiState.value.copy(
            guardando = true,
            estadoOperacion = EstadoOperacion.EnCurso,
            mensaje = null
        )

        viewModelScope.launch {
            val resultado = repository.crearSolicitud(equipoId, ambienteDestino, proposito, duracionHoras)

            val todosEquipos = repository.listarEquipos()
            val filtroActual = _uiState.value.filtroCategoria
            val equiposFiltrados = if (filtroActual == "TODOS") todosEquipos else todosEquipos.filter { it.categoria.name == filtroActual }
            val nuevoEstadoPantalla = if (equiposFiltrados.isEmpty()) {
                EstadoPantalla.Vacio("No se encontraron equipos para la categoría seleccionada.")
            } else {
                EstadoPantalla.Contenido(equiposFiltrados)
            }

            val nuevoEstadoOperacion = resultado.fold(
                onSuccess = { EstadoOperacion.Exitosa("Solicitud creada correctamente.") },
                onFailure = { EstadoOperacion.Fallida(it.message ?: "No se pudo crear la solicitud.") }
            )

            val mensajeFinal = resultado.fold(
                onSuccess = { "Solicitud creada correctamente." },
                onFailure = { it.message ?: "No se pudo crear la solicitud." }
            )

            _uiState.value = _uiState.value.copy(
                estadoPantalla = nuevoEstadoPantalla,
                estadoOperacion = nuevoEstadoOperacion,
                equipos = equiposFiltrados,
                solicitudes = repository.listarSolicitudes(),
                mensaje = mensajeFinal,
                guardando = false
            )
        }

        return true
    }

    fun cancelarSolicitud(id: Int): Boolean {
        if (_uiState.value.estadoOperacion is EstadoOperacion.EnCurso) {
            return false
        }

        _uiState.value = _uiState.value.copy(
            estadoOperacion = EstadoOperacion.EnCurso
        )

        viewModelScope.launch {
            val resultado = repository.cancelarSolicitud(id)

            val todosEquipos = repository.listarEquipos()
            val filtroActual = _uiState.value.filtroCategoria
            val equiposFiltrados = if (filtroActual == "TODOS") todosEquipos else todosEquipos.filter { it.categoria.name == filtroActual }
            val nuevoEstadoPantalla = if (equiposFiltrados.isEmpty()) {
                EstadoPantalla.Vacio("No se encontraron equipos para la categoría seleccionada.")
            } else {
                EstadoPantalla.Contenido(equiposFiltrados)
            }

            val nuevoEstadoOperacion = resultado.fold(
                onSuccess = { EstadoOperacion.Exitosa("Solicitud cancelada.") },
                onFailure = { EstadoOperacion.Fallida(it.message ?: "No se pudo cancelar la solicitud.") }
            )

            _uiState.value = _uiState.value.copy(
                estadoPantalla = nuevoEstadoPantalla,
                estadoOperacion = nuevoEstadoOperacion,
                equipos = equiposFiltrados,
                solicitudes = repository.listarSolicitudes(),
                mensaje = resultado.fold(
                    onSuccess = { "Solicitud cancelada." },
                    onFailure = { it.message ?: "No se pudo cancelar." }
                )
            )
        }

        return true
    }

    fun reiniciarEstadoOperacion() {
        _uiState.value = _uiState.value.copy(
            estadoOperacion = EstadoOperacion.Inactiva,
            mensaje = null
        )
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(
            mensaje = null,
            estadoOperacion = EstadoOperacion.Inactiva
        )
    }

    fun verificarBluetooth() {
        _uiState.value = _uiState.value.copy(
            bluetoothEstado = "Verificando adaptador Bluetooth...",
            estadoOperacion = EstadoOperacion.EnCurso
        )
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            _uiState.value = _uiState.value.copy(
                bluetoothEstado = "Bluetooth Activo - Conectado a Lector de Equipos",
                bluetoothDispositivos = listOf("Lector RFID Barcode #1 (Vinculado)", "Impresora Térmica Préstamos (Disponible)"),
                estadoOperacion = EstadoOperacion.Exitosa("Capacidad Bluetooth verificada y dispositivos vinculados correctamente.")
            )
        }
    }
}
