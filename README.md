PréstamoLab CTMA

Aplicación Android desarrollada en Kotlin + Jetpack Compose para gestionar el préstamo de equipos de laboratorio (electrónica, informática, herramientas, etc.) dentro de un entorno académico/técnico (CTMA).

Permite consultar un catálogo de equipos, ver su estado de disponibilidad, generar solicitudes de préstamo y hacer seguimiento a esas solicitudes (aprobación, entrega, devolución o cancelación).

Características
📋 Catálogo de equipos con nombre, categoría y estado (Disponible / Reservado / Prestado).
🔍 Detalle de equipo, con opción de reservarlo si está disponible.
📝 Solicitud de préstamo, indicando ambiente destino, propósito y duración.
📑 Mis solicitudes, con posibilidad de cancelar solicitudes activas.
🔄 Estado compartido entre pantallas mediante un único ViewModel, así los cambios (reservas, solicitudes, cancelaciones) se reflejan en toda la app.
Arquitectura

El proyecto sigue una separación simple por capas:

com.example.prstamolabctma
├── model/          # Data classes y enums del dominio
│   ├── Equipo.kt
│   ├── Estados.kt
│   └── SolicitudPrestamo.kt
├── repository/      # Acceso a datos (actualmente en memoria)
│   ├── PrestamoRepository.kt
│   └── InMemoryPrestamoRepository.kt
├── viewmodel/       # Lógica de presentación y estado de UI
│   └── PrestamoViewModel.kt
├── navigation/      # Definición de rutas y navegación entre pantallas
│   └── AppNavigation.kt
├── uii/             # Pantallas Compose (UI)
│   ├── CatalogoScreen.kt
│   ├── EquipoDetalleScreen.kt
│   ├── SolicitudScreen.kt
│   ├── MisSolicitudesScreen.kt
│   └── SolicitudDetalleScreen.kt
└── MainActivity.kt  # Punto de entrada de la app
Modelo de datos

Equipo

Campo	Tipo	Descripción
id	Int	Identificador único
nombre	String	Nombre del equipo
categoria	CategoriaEquipo	ELECTRONICA, INFORMATICA, HERRAMIENTA, OTRO
estado	EstadoEquipo	DISPONIBLE, RESERVADO, PRESTADO

SolicitudPrestamo

Campo	Tipo	Descripción
id	Int	Identificador único
equipoId	Int	Referencia al equipo solicitado
ambienteDestino	String	Lugar donde se usará el equipo
proposito	String	Motivo del préstamo
duracionHoras	Int	Duración estimada en horas
estado	EstadoSolicitud	SOLICITADA, APROBADA, ENTREGADA, DEVUELTA, CANCELADA, RECHAZADA
Flujo de navegación
inicio ──► catalogo ──► equipoDetalle/{equipoId} ──► solicitar/{equipoId} ──► misSolicitudes
│
▼
solicitudDetalle/{solicitudId}
Tecnologías
Kotlin
Jetpack Compose (Material 3)
Navigation Compose (androidx.navigation:navigation-compose)
ViewModel + StateFlow (androidx.lifecycle:lifecycle-viewmodel-compose)
Requisitos
Android Studio (versión reciente, con soporte para Compose)
JDK 17+
SDK de Android configurado (mínimo según build.gradle.kts)
Cómo ejecutar el proyecto
Clona o descarga el repositorio.
Ábrelo en Android Studio.
Deja que Gradle sincronice las dependencias (File → Sync Project with Gradle Files).
Verifica que el módulo app tenga la dependencia:
kotlin
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
Ejecuta la app en un emulador o dispositivo físico (Run ▶).
Estado actual / Alcance
Los datos se manejan en memoria (InMemoryPrestamoRepository), por lo que se reinician al cerrar la app. No hay persistencia local (Room) ni backend conectado todavía.
El flujo de aprobación/entrega/devolución de solicitudes (estados APROBADA, ENTREGADA, DEVUELTA, RECHAZADA) está modelado en el enum EstadoSolicitud, pero aún no tiene pantallas ni lógica que lo gestionen — solo se implementa SOLICITADA → CANCELADA.
Próximos pasos sugeridos
Persistencia de datos (Room o backend remoto).
Pantalla/lógica de administración para aprobar, entregar y devolver solicitudes.
Autenticación de usuarios (quién solicita, quién aprueba).
Pruebas unitarias para PrestamoViewModel y InMemoryPrestamoRepository.