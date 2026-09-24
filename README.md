# PréstamoLab CTMA – Documentación Completa del Proyecto (Guías 1 a 9)

## 📌 Product Goal (Objetivo del Producto)
Facilitar la consulta, solicitud, préstamo, seguimiento y devolución trazable de equipos y herramientas de formación del CTMA mediante una aplicación móvil Android nativa, con una experiencia segura, persistente localmente (Room), sincronizada remotamente y equipada con capacidades avanzadas del dispositivo.

---

## 🚀 Evolución Incremental del Proyecto (Versioning)
El desarrollo del proyecto se ha estructurado de forma evolutiva e incremental sobre el mismo repositorio y código base:
* **v0.1.0 / v0.2.0 (Semanas 5):** Consolidación de la arquitectura base (Compose UI, ViewModel, UiState, StateFlow, Repository simulado y navegación básica).
* **v0.3.0 (Semana 6):** Implementación de la persistencia local con Room (Entities, DAOs, relaciones) y DataStore para preferencias.
* **v0.4.0 (Semana 7):** Integración de programación asíncrona reactiva con Coroutines, funciones `suspend` y `Flow` / `StateFlow` orientadas al ciclo de vida.
* **v0.5.0 (Semana 8):** Conexión con servicios remotos vía Retrofit/OkHttp bajo una estrategia *Local-First* y pruebas automatizadas con `MockWebServer`.
* **v0.6.0 (Semana 9):** Incorporación de capacidades físicas del dispositivo (Cámara con `FileProvider` y Bluetooth/BLE), manejo seguro de permisos, y suite de pruebas instrumentadas y de regresión.

---

## 📌 Historias de Usuario y Criterios de Aceptación
- **HU-01**: Consultar catálogo de equipos.  
  **Criterio**: Los equipos se muestran con nombre, categoría y estado de disponibilidad.
- **HU-02**: Ver detalle de un equipo.  
  **Criterio**: Al seleccionar un equipo válido, se muestra su información completa y detallada.
- **HU-03**: Registrar solicitud de préstamo.  
  **Criterio**: Al guardar una solicitud válida, se crea una sola solicitud en estado `SOLICITADA` y el equipo pasa a `RESERVADO`.
- **HU-04**: Validar propósito, destino y duración.  
  **Criterio**: Propósito entre 10–180 caracteres, duración entre 1–8 horas, destino obligatorio con mensajes específicos.
- **HU-05**: Cancelar solicitud `SOLICITADA`.  
  **Criterio**: Al cancelar, la solicitud pasa a `CANCELADA` y el equipo vuelve a estar `DISPONIBLE`.
- **HU-06**: Evitar duplicación por doble pulsación.  
  **Criterio**: Una acción repetida de Guardar genera una única solicitud de manera transaccional.
- **HU-07**: Sincronización de datos con servicio remoto.  
  **Criterio**: Estrategia Local-First mediante Retrofit y Room como fuente canónica de verdad.
- **HU-08**: Adjuntar evidencia fotográfica y selección Bluetooth.  
  **Criterio**: Captura de evidencia mediante cámara con manejo de permisos y `FileProvider`, guardando la URI en metadatos, y asociación opcional a un dispositivo Bluetooth seleccionado.

---

## 🔒 Capacidades Físicas y Seguridad
* **Cámara y FileProvider**: Captura segura de evidencia fotográfica mediante contratos nativos de Android y almacenamiento en caché temporal mediante `FileProvider` para evitar excepciones de URI expuesta.
* **Bluetooth / BLE**: Escaneo y selección de dispositivos Bluetooth vinculados al flujo de préstamo y trazabilidad en laboratorio.
* **Privacidad y Permisos**: Solicitud estricta en tiempo de ejecución bajo el principio de *mínimo privilegio*.
* **Aislamiento de Datos**: Cifrado lógico y separación estricta entre la capa de UI, el repositorio y las fuentes de datos (Room / DataStore / Retrofit).

---

## 📌 Matriz de Riesgos
| ID   | Riesgo                                      | Prob. | Impacto | Nivel   | Cobertura |
|------|---------------------------------------------|-------|---------|---------|-----------|
| R-01 | Dos solicitudes activas reservan el mismo equipo | Alta  | Alta    | Crítico | TC-13 |
| R-02 | Datos fuera de rango aceptados              | Alta  | Media   | Alto    | TC-04–TC-11 |
| R-03 | ID inexistente provoca cierre               | Media | Alta    | Alto    | TC-03 |
| R-04 | Catálogo no refleja cambio de estado        | Media | Alta    | Alto    | TC-14–TC-15 |
| R-05 | Acciones desaparecen con fuente 1.5×        | Media | Media   | Medio   | TC-18 |
| R-06 | Excepción de permisos o URI nula en cámara  | Media | Alta    | Alto    | TC-19–TC-20 |

---

## 🎯 Definition of Done (Definición de Terminado)
1. El proyecto compila y se ejecuta exitosamente en Android Studio.
2. Los criterios de aceptación de cada incremento están implementados y verificados.
3. La UI Compose no modifica directamente las fuentes de datos (Room ni Retrofit).
4. El ViewModel expone `UiState` y `StateFlow` de solo lectura.
5. Se implementaron y validaron la persistencia local, la red y las capacidades físicas (Cámara/Bluetooth).
6. Se ejecutaron y superaron las pruebas unitarias, de integración (`MockWebServer`) e instrumentadas.
7. Los defectos identificados cuentan con solución y regresión documentada.
8. Git, GitHub Actions (CI) y la documentación se encuentran sincronizados.
9. El incremento es demostrable y cada integrante del equipo puede explicarlo y modificarlo en vivo.

---

## 📋 Suite de Pruebas (Casos Integrados TC-01 a TC-20)

| ID    | Escenario                        | Resultado esperado                                | Técnica       |
|-------|----------------------------------|--------------------------------------------------|---------------|
| TC-01 | Catálogo con datos               | Equipos visibles con disponibilidad              | Caso de uso   |
| TC-02 | EquipoId válido                  | Detalle corresponde al equipo seleccionado       | Caso de uso   |
| TC-03 | EquipoId inexistente             | Estado recuperable; sin cierre abrupto           | Negativa      |
| TC-04 | Propósito 9 caracteres           | No guarda; mensaje específico                    | Límite        |
| TC-05 | Propósito 10 caracteres          | Guarda si demás datos válidos                    | Límite        |
| TC-06 | Propósito 180 caracteres         | Guarda                                           | Límite        |
| TC-07 | Propósito 181 caracteres         | No guarda                                        | Límite        |
| TC-08 | Duración 0 horas                 | No guarda                                        | Límite        |
| TC-09 | Duración 1 hora                  | Válida                                           | Límite        |
| TC-10 | Duración 8 horas                 | Válida                                           | Límite        |
| TC-11 | Duración 9 horas                 | No guarda                                        | Límite        |
| TC-12 | Equipo no disponible             | Solicitud rechazada                              | Decisión      |
| TC-13 | Doble pulsación Guardar          | Una sola solicitud (Riesgo mitigado)             | Riesgo        |
| TC-14 | Crear solicitud válida           | SOLICITADA + equipo RESERVADO                    | Caso de uso   |
| TC-15 | Cancelar SOLICITADA              | CANCELADA y disponibilidad coherente             | Transición    |
| TC-16 | Cancelar CANCELADA               | Acción no disponible / sin cambio                | Transición    |
| TC-17 | Volver desde detalle/formulario  | Back stack correcto en navegación                | Navegación    |
| TC-18 | Fuente 1.5× y texto largo        | Contenido y acción esenciales utilizables        | Accesibilidad |
| TC-19 | Captura de evidencia fotográfica | Se genera URI válida mediante FileProvider       | Capacidad     |
| TC-20 | Selección Bluetooth              | Se asocia correctamente el dispositivo elegido   | Capacidad     |

---

## 📊 Bitácora de Ejecución de Pruebas
* TC-01 a TC-12 → **PASS**
* TC-13 → **FAIL** (detectado en sprint inicial) $\rightarrow$ Registrado como **BUG-03** y solucionado mediante bloqueo de estado en UI/ViewModel $\rightarrow$ **PASS en Regresión**.
* TC-14 a TC-20 → **PASS**

---

## 🏛️ Informe Ejecutivo de Arquitectura y Calidad
La aplicación implementa una arquitectura limpia y robusta:
* **Capa de Presentación**: Desarrollada en **Jetpack Compose**, basada en flujos unidireccionales de datos (`UiState` observable con `StateFlow`).
* **Capa de Dominio y Datos (`Repository`)**: Actúa como un mediador inteligente bajo una estrategia **Local-First**.
* **Persistencia Canónica**: Utiliza **Room** para garantizar la disponibilidad offline y la persistencia local de estados, solicitudes y metadatos de evidencia.
* **Capa Remota**: Gestionada mediante **Retrofit y OkHttp**, permitiendo la sincronización con servicios externos y su validación mediante `MockWebServer`.
* **Calidad Continua**: Integración de pruebas unitarias con `kotlinx-coroutines-test`, pruebas de UI y validación automática mediante **GitHub Actions** en cada Pull Request.

---

## ♿ Checklist de Accesibilidad y UX

| Criterio | Descripción | Estado |
|----------|-------------|--------|
| Texto + color | La disponibilidad de equipos se comunica con texto y color simultáneamente. | ✅ |
| Fuente aumentada | Los componentes y acciones críticas se adaptan correctamente a fuentes de 1.5×. | ✅ |
| Mensajes de error | Los campos inválidos muestran realimentación clara y específica. | ✅ |
| Etiquetas y Contraste | Botones etiquetados explícitamente y uso de paleta con contraste Material 3. | ✅ |
| Consistencia | Los cambios de estado se reflejan en tiempo real entre el catálogo y el detalle. | ✅ |

---

## 🤖 Uso Responsable de Inteligencia Artificial (Sección 14)
Conforme a los lineamientos del programa de formación:
* **Propósito de apoyo**: La asistencia de inteligencia artificial se utilizó como herramienta de apoyo para el diseño de patrones arquitectónicos reactivos (`StateFlow`), estructuración de pruebas unitarias con corrutinas y la configuración de contratos de hardware (`FileProvider` y Bluetooth).
* **Verificación y Autoría**: Todo el código y los artefactos generados fueron auditados, probados localmente y validados por el equipo de desarrollo, el cual posee plena autoría y capacidad para explicar y modificar cualquier componente durante la sustentación.