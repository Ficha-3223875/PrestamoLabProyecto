# PréstamoLab CTMA - Aplicación Móvil Android

**PréstamoLab CTMA** es una aplicación móvil nativa desarrollada en Kotlin + Jetpack Compose para gestionar la consulta, solicitud, seguimiento, devolución y registro de evidencia fotográfica de préstamos de equipos y herramientas de formación en el Centro de Tecnología de la Manufactura Avanzada (CTMA).

El proyecto integra prácticas de **Scrum**, **Arquitectura Limpia Android (MVVM)**, **Persistencia Local con Room (v3)**, **Preferencias con DataStore**, **Programación Asíncrona con Corrutinas/Flow**, **Consumo de Servicios REST con Retrofit**, **Capacidades del Dispositivo (Photo Picker, FileProvider Cámara y Biometría)** y **Controles de Seguridad y Calidad**.

---

## 🚀 Arquitectura y Capas del Proyecto

```
com.example.prstamolabctma
├── data/
│   ├── local/          # Room DB (Entities, DAOs, AppDatabase, Migraciones, Mappers)
│   ├── preferences/    # DataStore Preferences (Filtros persistentes)
│   └── remote/         # Retrofit REST (DTOs, ApiService, RetrofitClient, RemoteMappers)
├── model/              # Modelos de dominio y Enums (Equipo, SolicitudPrestamo, Evidencia)
├── repository/         # Repository Pattern (Offline-First / Single Source of Truth)
├── ui/
│   ├── state/          # Modelos de Estado UiState<T> y OperationState
│   └── theme/          # Material 3 Theme, Color, Type
├── uii/                # Vistas Jetpack Compose (Catalogo, Detalle, Solicitud, MisSolicitudes)
├── util/               # Utilidades (EvidenciaValidator, NotificationHelper, BiometricHelper)
├── viewmodel/          # PrestamoViewModel + StateFlow + ViewModelProvider.Factory
├── navigation/         # AppNavigation con Navigation Compose
└── MainActivity.kt     # FragmentActivity / Punto de entrada
```

---

## 🛠 Características e Incrementos Implementados

- 📋 **Catálogo de Equipos**: Filtrado dinámico por categoría (`ELECTRÓNICA`, `INFORMÁTICA`, etc.) conservado en DataStore Preferences.
- 🔍 **Detalle de Equipo**: Consulta de disponibilidad e inicio del proceso de reserva.
- 📝 **Solicitudes de Préstamo**: Registro con validación de destino, duración (1-8 horas) y propósito (10-180 caracteres).
- 🔒 **Firma Biométrica (`BiometricPrompt`)**: Confirmación mediante huella dactilar o rostro al guardar o cancelar una solicitud.
- 📑 **Mis Solicitudes & Cancelación**: Consulta reactiva de solicitudes activas y cancelación con re-liberación automática del equipo.
- 📷 **Evidencia Fotográfica con Mínimo Privilegio**:
  - **Photo Picker (`PickVisualMedia`)**: Selección de imágenes de la galería sin solicitar permisos globales.
  - **Cámara (`TakePicture`)**: Captura mediante `FileProvider` con `content://` URIs seguras.
  - **Validación**: Comprobación de formato MIME (`image/*`) y límite de 5 MB.
- 🔔 **Notificaciones Contextuales (`POST_NOTIFICATIONS`)**: Solicitud de permiso en tiempo de ejecución solo cuando el usuario activa un recordatorio.
- 🌐 **Sincronización Offline-First con Retrofit**:
  - Room actúa como Fuente Única de Verdad (Single Source of Truth).
  - Si la red falla, la UI conserva los datos locales almacenados y muestra un estado de advertencia sin pantalla en blanco.
- 🛡 **Seguridad por Ambientes**: Desactivación de tráfico en texto claro (Cleartext HTTP) mediante `network_security_config.xml`.

---

## 📝 Instrumento de Conocimiento (Respuestas Técnicas)

### 1. Mínimo Privilegio aplicado a la selección de fotografías
El **Principio de Mínimo Privilegio** otorga a la app únicamente los permisos estrictamente necesarios. Se utilizó el **System Photo Picker (`PickVisualMedia`)**, el cual abre el selector nativo del sistema operativo. El usuario selecciona la foto específica y el SO concede acceso *únicamente* a esa URI. No se solicita el permiso de lectura amplia de la galería (`READ_MEDIA_IMAGES`), protegiendo la privacidad del usuario.

### 2. Diferencia entre `content://` URI, `file://` URI y bytes de una imagen
- **`content://` URI**: Identificador administrado por un `ContentProvider` (`FileProvider` / Photo Picker) con permisos temporales concedidos por el SO. No expone rutas del disco y es la forma segura obligatoria de compartir archivos.
- **`file://` URI**: Ruta absoluta del sistema de archivos local (`file:///data/...`). Está prohibida para compartir en Android moderno ya que produce `FileUriExposedException`.
- **Bytes (Bitmap / Base64)**: Datos binarios crudos. Almacenarlos en base de datos causa saturación de memoria (`OutOfMemoryError`) y bloqueos en el cursor de Room.

### 3. Ventaja del Photo Picker frente a leer la galería completa
Photo Picker se ejecuta fuera del entorno de la aplicación. La app no recibe permisos de lectura de almacenamiento global ni puede escanear los archivos del usuario. Solo recibe una `content://` URI temporal de lectura para la imagen seleccionada por el usuario.

### 4. Manejo ante la negativa del permiso `POST_NOTIFICATIONS`
La aplicación aplica **Degradación Grácil (Graceful Degradation)**: continua funcionando normalmente, no se cierra ni bloquea la gestión de préstamos, y no insiste repetidamente con el diálogo de permiso. Solo desactiva las alertas emergentes.

### 5. Validaciones previas a persistir o enviar una imagen
1. **Tipo MIME**: Comprobar formato válido (`image/jpeg`, `image/png`, `image/webp`).
2. **Tamaño máximo**: Validar que no supere 5 MB (`EvidenciaValidator`).
3. **Acceso**: Verificar que la URI sea legible a través del `ContentResolver`.

### 6. Por qué la validación cliente no reemplaza la del servidor
La validación cliente mejora la experiencia de usuario (UX), pero puede ser eludida por atacantes interceptando peticiones. El servidor (API REST) debe realizar siempre su propia validación de seguridad de archivos y parámetros.

### 7. Dato de Configuración vs. Secreto
- **Dato de Configuración**: Valor no sensible para adaptar la app al ambiente (`API_BASE_URL`). Puede incluirse en compilados.
- **Secreto**: Credencial confidencial (`access token`). Debe almacenarse de forma cifrada (`EncryptedSharedPreferences` / `Keystore`), viajar por HTTPS y nunca escribirse en texto plano.

### 8. Contores para evitar exponer evidencias o credenciales en producción
1. Uso obligatorio de **HTTPS** y desactivación de Cleartext Traffic en `network_security_config.xml`.
2. **Desactivación de logs sensibles** en compilados de producción (`release`).
3. Uso de **`FileProvider` y `content://` URIs** en subdirectorios de caché privado.

---

## ❓ Preguntas de Sustentación

### 40. Trazabilidad HU ➔ Criterio ➔ Código ➔ Prueba
- **HU-03**: Solicitar préstamo de equipo.
- **Criterio de Aceptación**: No se permite registrar una solicitud con propósito menor a 10 caracteres o destino vacío.
- **Código**: Validación de negocio en [RoomPrestamoRepository.kt](file:///C:/Users/Alejandro/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/java/com/example/prstamolabctma/repository/RoomPrestamoRepository.kt).
- **Prueba**: [InMemoryPrestamoRepositoryTest.kt](file:///C:/Users/Alejandro/AndroidStudioProjects/PrestamoLabCTMA/app/src/test/java/com/example/prstamolabctma/repository/InMemoryPrestamoRepositoryTest.kt).

### 41. Room como Fuente Local Canónica (Single Source of Truth)
Toda la UI observa únicamente los flujos reactivos de Room (`Flow<List<...>>`). Las respuestas de Retrofit no alimentan directamente la UI; Retrofit actualiza Room y Room invalida las consultas notificando a las pantallas.

### 42. Diferencia entre `Flow` y `StateFlow`
- **`Flow`**: Flujo frío asíncrono que sólo produce valores cuando hay un recolector activo.
- **`StateFlow`**: Flujo caliente (Hot Flow) que retiene el último estado emitido (`value`), requiere valor inicial y sobrevive a cambios de configuración en la UI.

### 43. Caso de Error de Red y representación en `UiState`
Si la red falla, Room mantiene la lista local y emite `UiState.Contenido(listaLocal)`. De forma independiente, `refreshState` emite `OperationState.Fallida("Sin conexión. Mostrando datos locales")`, desplegando un Snackbar sin borrar el contenido de la pantalla.

### 44. Ejemplo de Test Automatizado (AAA: Arrange, Act, Assert)
En [OfflineFirstRepositoryTest.kt](file:///C:/Users/Alejandro/AndroidStudioProjects/PrestamoLabCTMA/app/src/test/java/com/example/prstamolabctma/repository/OfflineFirstRepositoryTest.kt):
- **Arrange**: Se almacena un equipo local en Room y se configura `fakeApi.shouldFailWithNetworkError = true`.
- **Act**: Se llama a `repository.refrescarDatos()`.
- **Assert**: Se verifica que `result.isFailure` es verdadero y que los datos locales en Room no sufrieron pérdidas.

### 45. Desarrollo mediante TDD
Se aplicó TDD en la utilidad [EvidenciaValidatorTest.kt](file:///C:/Users/Alejandro/AndroidStudioProjects/PrestamoLabCTMA/app/src/test/java/com/example/prstamolabctma/util/EvidenciaValidatorTest.kt) para verificar las reglas de negocio del límite de 5 MB y formatos de imagen antes de escribir la implementación.

### 46. Defecto encontrado y solución
- **Defecto**: Incompatibilidad de firmas JVM al usar KSP con versiones anteriores de Room.
- **Solución**: Actualización a Room v2.8.5 y re-ejecución de la suite completa de 27 pruebas unitarias de regresión.

### 47. Permiso del dispositivo y Mínimo Privilegio
Se solicitaron `POST_NOTIFICATIONS` y `USE_BIOMETRIC`. Cumplen mínimo privilegio porque no piden acceso general a la galería (se usa Photo Picker) y la notificación sólo se pide en el momento en que el usuario activa un recordatorio.

### 48. Quality Gates del proyecto
1. Compilación limpia (`./gradlew assembleDebug`).
2. Paso exitoso del 100% de las pruebas unitarias (`./gradlew testDebugUnitTest`).
3. Verificación de linter (`./gradlew lint`).

### 49. Riesgo Residual
La lectura de metadatos de archivos multimedia grandes puede provocar demoras leves si no se ejecuta en un hilo secundario. Mitigado asignando `Dispatchers.IO` en el repositorio.

---

## 🧪 Ejecución de Pruebas

Para ejecutar las pruebas unitarias automatizadas del proyecto:

```bash
./gradlew testDebugUnitTest
```

Para generar la compilación de la aplicación:

```bash
./gradlew assembleDebug
```
