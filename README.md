# GUÍA DE APRENDIZAJE INTEGRADORA - PréstamoLab CTMA

## 📌 Propósito General
Construir, probar y sustentar incrementos funcionales de una aplicación Android para la gestión de préstamos de equipos y herramientas de formación, trabajando con **Scrum**, **Git/GitHub**, **Arquitectura Android**, **Persistencia local (Room y DataStore)**, **Programación asíncrona**, **Consumo de API REST**, **Capacidades del dispositivo (Cámara y Bluetooth)**, **Seguridad (R8/Proguard)** y **Automatización de pruebas**.

---

## 📌 Incremento Funcional y Características Principales

1. **Gestión de Catálogo y Categorías**: Búsqueda, filtrado por categoría y filtrado de equipos disponibles persistidos mediante DataStore.
2. **Solicitudes de Préstamo**: Creación, consulta, edición en estado `SOLICITADA` y cancelación de préstamos.
3. **Capacidades del Dispositivo**:
   - **Cámara**: Adjuntar evidencia fotográfica a los préstamos mediante `ActivityResultContracts.TakePicturePreview`.
   - **Bluetooth**: Verificación de conectividad, estado del adaptador Bluetooth y vinculación con dispositivos y lectores de códigos/accesorios de laboratorio.
4. **Resiliencia y Red**: Consumo de API REST mediante Retrofit y manejo de fallos offline con caché local.
5. **Accesibilidad**: Soporte completo para lectores de pantalla (**TalkBack**), contraste de colores y fuentes escalables.

---

## 📌 Historias de Usuario y Criterios de Aceptación

- **HU-01**: Buscar equipos por categoría. *(Criterio: Al seleccionar una categoría, se muestran únicamente los equipos correspondientes).*
- **HU-02**: Filtrar equipos disponibles. *(Criterio: El catálogo permite activar un filtro que muestra solo los equipos en estado `DISPONIBLE`).*
- **HU-03**: Editar solicitud en estado `SOLICITADA`. *(Criterio: El usuario puede modificar el propósito y la duración antes de la entrega).*
- **HU-04**: Notificación de vencimiento. *(Criterio: Al acercarse la hora de devolución, se envía una alerta al usuario).*
- **HU-05**: Historial de solicitudes. *(Criterio: Consulta de solicitudes anteriores con estado final `CANCELADA` o `FINALIZADA`).*
- **HU-06**: Accesibilidad con lector de pantalla. *(Criterio: Botones y etiquetas reconocidos correctamente por TalkBack).*

---

## ❓ Cuestionario y Preguntas de Sustentación (Guía de Aprendizaje)

### P1. ¿Cómo se aplica la metodología Scrum y el control de versiones con Git/GitHub en el desarrollo del incremento?
> **Respuesta:** Scrum se aplica mediante iteraciones de desarrollo (Sprints), definición de Historias de Usuario con Criterios de Aceptación claros, una Matriz de Riesgos y una **Definition of Done (DoD)** estricta. El control de versiones se gestiona con Git mediante ramas de características (`feature/`), commits atómicos y descriptivos, Pull Requests y revisión de código antes de integrar los cambios a la rama principal (`main`).

### P2. ¿Cuál es la arquitectura de la aplicación y cómo se separan las capas de datos, dominio y presentación?
> **Respuesta:** La aplicación implementa el patrón **MVVM (Model-View-ViewModel)** combinado con el **Repository Pattern** y arquitectura limpia unidireccional (UDF).
> - **Capa de Presentación**: Pantallas en Jetpack Compose que consumen estados inmutables (`StateFlow` / `UiState`) recolectados con ciclo de vida consciente (`collectAsStateWithLifecycle`).
> - **Capa de Dominio / Lógica**: ViewModels que gestionan la lógica de negocio, validaciones y estados de operación.
> - **Capa de Datos**: Repositorios (`RoomPrestamoRepository`, `InMemoryPrestamoRepository`) que unifican el acceso a fuentes locales (Room, DataStore) y remotas (Retrofit API).

### P3. ¿Cómo se implementa la persistencia local de datos y la persistencia de preferencias?
> **Respuesta:** 
> - **Room Database**: Utiliza entidades `@Entity`, DAOs `@Dao` y la base de datos `@Database` (`BaseDatosLocal`) para almacenar y consultar equipos y solicitudes de forma relacional con soporte para flujos reactivos (`Flow<List<...>>`).
> - **DataStore Preferences**: Almacena las preferencias de usuario (como filtros de categoría activos y configuraciones de sesión) de manera asíncrona mediante pares clave-valor fuertemente tipados.

### P4. ¿De qué manera se realiza la programación asíncrona y la reactividad en la aplicación?
> **Respuesta:** Se utilizan **Kotlin Coroutines** y **Kotlin Flows**. Las operaciones de larga duración (consultas de base de datos, llamadas a API REST, sincronización y Bluetooth) se ejecutan en hilos secundarios utilizando `viewModelScope` y `Dispatchers.IO`. La reactividad se logra exponiendo `StateFlow` y utilizando operadores como `combine` para fusionar flujos de datos locales y preferencias de usuario en tiempo real.

### P5. ¿Cómo se gestiona el consumo de servicios web API REST y la resiliencia offline?
> **Respuesta:** Se utiliza **Retrofit** y **OkHttp** con DTOs (`EquipoDto`) y mappers de conversión. Ante fallos de conexión (sin internet o servidor caído), la aplicación implementa un mecanismo de **resiliencia offline**: intercepta el error, captura la excepción y recurre automáticamente a la fuente de datos local de Room, informando al usuario mediante `EstadoOperacion.Fallida` sin interrumpir la experiencia de uso.

### P6. ¿Qué capacidades del hardware del dispositivo móvil se integran en la aplicación?
> **Respuesta:** 
> 1. **Cámara**: Integrada mediante `ActivityResultContracts.TakePicturePreview` en `SolicitudDetalleScreen` para capturar y adjuntar evidencias fotográficas a las solicitudes de préstamo.
> 2. **Bluetooth**: Gestionado mediante verificación de estado del adaptador y simulación/conexión de dispositivos periféricos (lectores RFID de códigos de barras, impresoras térmicas de recibos de préstamo) desde el catálogo principal.

### P7. ¿Cómo se asegura la protección del código y la optimización mediante R8 / Proguard?
> **Respuesta:** Se configuran reglas de keep personalizadas en el archivo `keepRules/rules.keep` (y `proguard-rules.pro`). Esto asegura que los modelos de datos serializados (DTOs de Retrofit), entidades de Room y clases reflejadas no sean renombradas u optimizadas agresivamente por el motor **R8**, evitando fallos de reflexión o deserialización en la versión de producción (`release`).

### P8. ¿Cómo se estructuran y ejecutan las pruebas automatizadas para garantizar la calidad?
> **Respuesta:** Se implementan pruebas unitarias y de integración utilizando **JUnit**, **Kotlin Coroutines Test** (`runTest`), y pruebas de ViewModel y Repositorio:
> - `PrestamoViewModelTest`: Valida el comportamiento del ViewModel, estados de pantalla y flujos reactivos.
> - `InMemoryPrestamoRepositoryTest` y `PersistenceMigrationTest`: Validan la persistencia y reglas de negocio locales.
> - `WebServicesResilienceTest` y `EvidenciaMediaNetworkTest`: Validan el comportamiento ante respuestas de red y sincronización.

---

## 📌 Definición de Hecho (Definition of Done)

1. [x] El proyecto compila y se ejecuta correctamente sin errores.
2. [x] Los criterios de aceptación seleccionados están implementados al 100%.
3. [x] La UI no modifica directamente la fuente de datos (arquitectura unidireccional).
4. [x] El ViewModel expone `UiState` / `StateFlow` de solo lectura.
5. [x] La navegación transporta correctamente los identificadores mediante argumentos seguros.
6. [x] Los IDs inexistentes son controlados sin cerrar la aplicación (`try-catch` y estados de error).
7. [x] Los filtros de equipos y capacidades del dispositivo (Cámara y Bluetooth) funcionan correctamente.
8. [x] Se ejecutaron exitosamente las pruebas automatizadas (43/43 tests pasando).
9. [x] Git y README se encuentran completamente actualizados.

---

## 🧪 Resumen de Pruebas Automatizadas
- **Total de pruebas unitarias**: 43 pruebas ejecutadas en `app:testDebugUnitTest`.
- **Estado**: ✅ **Todas pasando exitosamente (`PASS`)**.
