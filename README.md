# PréstamoLab CTMA

Aplicación móvil Android desarrollada en **Jetpack Compose** para la gestión integral de préstamos de equipos y herramientas de formación del CTMA.

El proyecto implementa una arquitectura robusta por capas, persistencia local con **Room**, flujos reactivos mediante **Corrutinas y Flow**, sincronización preparada para servicios remotos mediante **Retrofit**, y capacidades avanzadas del dispositivo como **Photo Picker** y **geolocalización GPS**.

---

## 📌 Product Goal

Facilitar la consulta, solicitud, préstamo, seguimiento y devolución trazable de equipos y herramientas de formación del CTMA mediante una aplicación móvil Android, con una experiencia segura, verificable y preparada para operar con persistencia local y sincronización asíncrona.

---

## 👥 Roles de Usuario

### Aprendiz

- Consulta el catálogo de equipos.
- Consulta el detalle de los equipos.
- Solicita préstamos.
- Adjunta evidencia fotográfica.
- Adjunta ubicación GPS.
- Gestiona sus solicitudes de préstamo.

### Instructor / Gestor

- Valida procesos.
- Supervisa la trazabilidad.
- Observa los cambios de estado de los recursos.

---

## 🎯 Evolución por Incrementos

### Semana 5 — v0.2.0

Consolidación de la arquitectura base:

- Jetpack Compose.
- ViewModel.
- `UiState` / `StateFlow`.
- Repositorio en memoria (`InMemory`).
- Navegación segura.

### Semana 6 — v0.3.0

Incorporación de persistencia local:

- Room Database.
- Entities.
- DAOs.
- Relaciones.
- Fuente local canónica.
- Estructuración formal bajo Scrum.

### Semana 7 — v0.4.0

Implementación de operaciones asíncronas y reactivas:

- Kotlin Coroutines.
- Funciones `suspend`.
- `Flow`.
- `StateFlow`.
- Manejo de excepciones.
- Estados `Loading`, `Content`, `Empty` y `Error`.
- `collectAsStateWithLifecycle()`.

### Semana 8 — v0.5.0

Integración de servicios remotos:

- Retrofit.
- OkHttp.
- DTOs.
- Mapeo de datos.
- JUnit.
- Coroutines Test.

### Semana 9 — v0.6.0

Integración de capacidades del dispositivo:

- Photo Picker.
- `FusedLocationProviderClient`.
- Geolocalización GPS.
- Persistencia segura de URIs.
- Notificaciones contextuales.
- Pruebas instrumentadas.

---

## 🛠️ Tecnologías y Arquitectura

- **Lenguaje:** Kotlin (Toolchain 17)
- **UI:** Jetpack Compose
- **Diseño:** Material 3
- **Carga de imágenes:** Coil
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Patrón:** Flujo Unidireccional de Datos (UDF)
- **Persistencia:** Room Database
- **Preferencias:** DataStore
- **Concurrencia:** Kotlin Coroutines
- **Flujos reactivos:** Flow / StateFlow
- **Red:** Retrofit / OkHttp
- **Control de versiones:** Git / GitHub
- **CI:** GitHub Actions

### Estructura del proyecto

```text
com.example.prstamolabctma/
│
├── model/
│   ├── Equipo.kt
│   ├── SolicitudPrestamo.kt
│   └── Estados.kt
│
├── data/
│   ├── local/
│   │   ├── Entities
│   │   ├── DAOs
│   │   └── RoomDatabase
│   │
│   └── repository/
│       ├── PrestamoRepository.kt
│       └── RoomPrestamoRepository.kt
│
├── viewmodel/
│   ├── PrestamoViewModel.kt
│   └── PrestamoViewModelFactory.kt
│
├── ui/
│   ├── catalogo/
│   ├── equipo/
│   ├── solicitud/
│   ├── misprestamos/
│   └── common/
│       ├── EvidenciaUbicacionSection
│       └── StateRenderers
│
├── util/
│   └── NotificationHelper.kt
│
└── navigation/
    └── AppNavigation.kt
```

# 📏 Reglas de Negocio Principales

### Solo se puede solicitar un equipo que se encuentre en estado DISPONIBLE.
- El ambiente o destino del préstamo es obligatorio.
- El propósito del préstamo debe tener una longitud estrictamente entre 10 y 180 caracteres.
- La duración del préstamo debe estar comprendida entre 1 y 8 horas.
- Se previene la creación de solicitudes duplicadas mediante control de doble pulsación.
- Al crear una solicitud, su estado inicial es SOLICITADA y el equipo pasa a RESERVADO.
- Una solicitud en estado SOLICITADA puede ser cancelada, lo que retorna el equipo a estado DISPONIBLE.
- Las evidencias fotográficas se copian al almacenamiento interno de la aplicación para garantizar persistencia de lectura mediante URIs seguras gestionadas en Room.

# 📖 Respuestas a las Preguntas de Sustentación

### 1. Abra una HU y muestre un criterio de aceptación; siga la trazabilidad hasta el código y la prueba que lo valida.

HU seleccionada: HU-02 — Registrar solicitud de préstamo.

Criterio de aceptación:

Si los datos son válidos, se crea una sola solicitud en estado SOLICITADA y el equipo pasa a RESERVADO.

Trazabilidad al código:

Se implementa en PrestamoViewModel.kt mediante la función de validación y las operaciones sobre RoomPrestamoRepository.kt, actualizando el estado del equipo en la base de datos local Room.

Trazabilidad a la prueba:

Se valida mediante pruebas unitarias de repositorio y pruebas de flujos asíncronos con kotlinx-coroutines-test, verificando que el estado inicial obtenido sea coherente.

### 2. Explique por qué Room se considera fuente local canónica en su solución.

Room se establece como la fuente local canónica porque centraliza todas las operaciones de persistencia transaccional mediante un esquema relacional estructurado, utilizando entidades como EquipmentEntity y LoanEntity.

Esto permite que la información no dependa de la memoria volátil del ViewModel y que los datos puedan mantenerse ante cierres de la aplicación o cambios en el ciclo de vida.

### 3. ¿Qué diferencia existe entre Flow y StateFlow en el contexto del ViewModel?
   Flow

Flow es un flujo de datos asíncrono en frío (cold stream) que emite valores bajo demanda cada vez que un colector se suscribe a él.

StateFlow

StateFlow es un flujo en caliente (hot stream) que mantiene un estado actual retenido en memoria.

En el ViewModel se utiliza StateFlow<UiState> porque la interfaz necesita conocer inmediatamente el último estado disponible, por ejemplo:

Loading
Content
Empty
Error

La interfaz observa estos cambios mediante:

collectAsStateWithLifecycle()

### 4. Muestre un caso de error de red y explique cómo se representa en UiState.

Cuando ocurre un fallo de conectividad o un error HTTP, por ejemplo 404 o 500, durante una operación con Retrofit, el error puede ser capturado y el ViewModel puede actualizar el estado.

Por ejemplo:

UiState.Error(
message = "Error de sincronización con el servidor"
)

La interfaz detecta el estado Error mediante el manejo correspondiente de UiState y renderiza el componente visual de error.

### 5. Seleccione un test automatizado y explique Arrange, Act y Assert.
   Arrange — Preparar

Se inicializan las dependencias necesarias para la prueba, como una instancia del repositorio en memoria o el ViewModel con datos simulados.

Act — Actuar

Se ejecuta la acción que se desea probar.

Por ejemplo:

viewModel.crearSolicitud(...)
Assert — Afirmar

Se valida mediante aserciones que el resultado obtenido coincida con el resultado esperado.

Por ejemplo:

assertEquals(
expectedState,
currentState
)

### 6. ¿Qué parte del incremento fue desarrollada mediante TDD y qué aprendieron?

El desarrollo de las reglas de negocio relacionadas con la validación del propósito y la duración de los préstamos se estructuró bajo el ciclo:

Red → Green → Refactor

Primero se creó la prueba que representaba el comportamiento esperado. Posteriormente se implementó el código necesario para que la prueba pasara y finalmente se realizó la refactorización.

El aprendizaje principal fue que escribir primero las pruebas ayuda a diseñar funciones de validación más limpias, específicas y fáciles de comprobar.

### 7. Muestre un defecto encontrado, su confirmación y la regresión seleccionada.
   Defecto identificado

BUG-01: pérdida de permisos de lectura sobre las URIs temporales proporcionadas por Photo Picker después de reiniciar la aplicación.

El problema impedía que Coil pudiera renderizar correctamente una imagen previamente seleccionada.

Confirmación y corrección

Se implementó una función auxiliar:

guardarImagenEnAlmacenamientoInterno

Esta función copia físicamente el archivo seleccionado al directorio interno de la aplicación.

De esta manera, la aplicación puede mantener una referencia persistente a la imagen.

Prueba de regresión

Se realizó el siguiente procedimiento:

Seleccionar una imagen mediante Photo Picker.
Guardar la imagen.
Registrar la solicitud.
Cerrar la aplicación.
Abrir nuevamente la aplicación.
Consultar el detalle de la solicitud.
Comprobar que la imagen continúe renderizándose correctamente.

### 8. ¿Qué permiso del dispositivo solicitaron y por qué cumple mínimo privilegio?

Se solicitaron los siguientes permisos:

ACCESS_FINE_LOCATION

Se utiliza para obtener la ubicación GPS del dispositivo cuando el usuario necesita adjuntar la ubicación a una solicitud.

POST_NOTIFICATIONS

Se utiliza para mostrar notificaciones contextuales relacionadas con la aplicación.

Estos permisos se solicitan bajo demanda cuando la funcionalidad correspondiente los necesita, en lugar de solicitar todos los permisos al iniciar la aplicación.

Esto permite aplicar el principio de mínimo privilegio, solicitando únicamente los permisos necesarios para cada funcionalidad.

### 9. ¿Qué quality gates utiliza su Pull Request?

Las integraciones hacia la rama principal mediante Pull Requests están respaldadas por GitHub Actions.

Los controles incluyen:

Compilación
./gradlew build
Pruebas
./gradlew test
Análisis de código

Se realiza la verificación mediante herramientas de análisis estático y linting.

Estos controles permiten detectar errores de compilación, fallos en las pruebas y problemas de calidad antes de integrar los cambios a la rama principal.

### 10. ¿Qué riesgo residual permanece en el incremento actual?

Como riesgo residual se identifica que la sincronización con servicios remotos mediante Retrofit se encuentra preparada a nivel de arquitectura y repositorios, pero el funcionamiento actual se apoya principalmente en Room como fuente local.

Una interrupción prolongada de los servicios remotos puede requerir mecanismos adicionales de reintento y gestión de colas.

Estos mecanismos pueden optimizarse en futuras versiones del proyecto.

# 📱 Funcionamiento de la Aplicación

El flujo principal de PréstamoLab es:

Catálogo de equipos
↓
Detalle del equipo
↓
Solicitar préstamo
↓
Solicitud creada
↓
Mis préstamos
↓
Consultar solicitud
↓
Cancelar solicitud

Durante este flujo, la aplicación debe manejar los siguientes estados:

Loading
Content
Empty
Error

La información sigue un flujo reactivo:

Room
↓
Flow
↓
Repository
↓
ViewModel
↓
StateFlow
↓
Jetpack Compose

Esto permite que los cambios realizados en la base de datos puedan reflejarse en la interfaz de manera reactiva.

# 🧪 Pruebas y Validación

- Las pruebas automatizadas se ejecutan mediante Gradle.

Ejecutar todas las pruebas
./gradlew test
Ejecutar las pruebas unitarias de la aplicación
./gradlew :app:testDebugUnitTest
Validaciones principales de Semana 7

- El incremento v0.4.0 debe validar:

Estados Loading.
Estados Content.
Estados Empty.
Estados Error.
Operaciones suspend.
Flujos Flow.
StateFlow.
Manejo de excepciones.
Operaciones asíncronas.
Actualización reactiva de la interfaz.
Manejo de errores sin cierres abruptos.
Cancelación de operaciones cuando corresponda.

# 📌 Incrementos del Proyecto

- Semana	Versión	Incremento
- Semana 5	v0.2.0	Arquitectura base y navegación
- Semana 6	v0.3.0	Persistencia local con Room
- Semana 7	v0.4.0	Corrutinas, Flow y estado reactivo
- Semana 8	v0.5.0	Integración API REST
- Semana 9	v0.6.0	Capacidades del dispositivo y seguridad

# ▶️ Instrucciones de Ejecución

- Clonar el repositorio.
- Abrir el proyecto en Android Studio.
- Sincronizar las dependencias de Gradle.
- Crear o seleccionar un emulador Android.
- Ejecutar la aplicación.
- Explorar el catálogo de equipos.
- Seleccionar un equipo.
- Consultar el detalle.
- Registrar una solicitud de préstamo.
- Adjuntar evidencia fotográfica cuando corresponda.
- Capturar la ubicación GPS cuando corresponda.
- Consultar la solicitud desde Mis préstamos.
- Cancelar la solicitud cuando corresponda.

# 🔀 Control de Versiones

El proyecto utiliza Git y GitHub para el control de versiones y seguimiento del desarrollo.

Se utilizan:

Ramas de trabajo.
Commits.
Pull Requests.
GitHub Actions.
Pruebas automatizadas.
Trazabilidad entre Historias de Usuario, código y pruebas.

# 📚 Documentación del Proyecto

La documentación del proyecto incluye:

- Product Goal.
- Product Backlog.
- Historias de Usuario.
- Criterios de aceptación.
- Matriz de riesgos.
- Casos de prueba.
- Matriz de trazabilidad.
- Evidencias de los incrementos.
- Documentación técnica.
- Resultados de pruebas.
- Evidencias de Scrum.
- Definition of Done.
- Sprint Review.
- Retrospective.