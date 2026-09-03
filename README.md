# PréstamoLab CTMA – Incremento Android

## 📌 Product Goal
Mejorar la trazabilidad y consulta de préstamos de recursos de formación mediante una aplicación móvil educativa.

---

## 📌 Historias de Usuario y Criterios de Aceptación
- **HU-01**: Consultar catálogo de equipos.  
  **Criterio**: Los equipos se muestran con nombre, categoría y estado.

- **HU-02**: Ver detalle de un equipo.  
  **Criterio**: Al seleccionar un equipo válido, se muestra su información completa.

- **HU-03**: Registrar solicitud de préstamo.  
  **Criterio**: Al guardar una solicitud válida, se crea una sola solicitud en estado SOLICITADA y el equipo pasa a RESERVADO.

- **HU-04**: Validar propósito, destino y duración.  
  **Criterio**: Propósito entre 10–180 caracteres, duración entre 1–8 horas, destino obligatorio.

- **HU-05**: Cancelar solicitud SOLICITADA.  
  **Criterio**: Al cancelar, la solicitud pasa a CANCELADA y el equipo vuelve a DISPONIBLE.

- **HU-06**: Evitar duplicación por doble pulsación.  
  **Criterio**: Una acción de Guardar crea una sola solicitud.

---

## 📌 Matriz de Riesgos
| ID   | Riesgo                                      | Prob. | Impacto | Nivel   | Cobertura |
|------|---------------------------------------------|-------|---------|---------|-----------|
| R-01 | Dos solicitudes activas reservan el mismo equipo | Alta  | Alta    | Crítico | TC-13 |
| R-02 | Datos fuera de rango aceptados              | Alta  | Media   | Alto    | TC-04–TC-11 |
| R-03 | ID inexistente provoca cierre               | Media | Alta    | Alto    | TC-03 |
| R-04 | Catálogo no refleja cambio de estado        | Media | Alta    | Alto    | TC-14–TC-15 |
| R-05 | Acciones desaparecen con fuente 1.5×        | Media | Media   | Medio   | TC-18 |

---

## 📌 Sprint Goal
Permitir consultar un equipo disponible y registrar una solicitud válida, manteniendo disponibilidad coherente y demostrando calidad mediante pruebas reproducibles.

---

## 📌 Definition of Done
1. El proyecto compila y se ejecuta.
2. Los criterios de aceptación seleccionados están implementados.
3. La UI no modifica directamente la fuente de datos.
4. El ViewModel expone UiState/StateFlow de solo lectura.
5. La navegación transporta identificadores y controla IDs inexistentes.
6. Se ejecutaron los casos acordados y los resultados son reales.
7. Los defectos críticos/altos tienen decisión explícita.
8. Las correcciones relevantes tienen confirmación y regresión.
9. Git y README están actualizados.
10. El incremento puede demostrarse y cada integrante puede explicarlo.

---

## 📌 Suite de Pruebas (16 casos mínimos)

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
| TC-13 | Doble pulsación Guardar          | Una sola solicitud                               | Riesgo        |
| TC-14 | Crear solicitud válida           | SOLICITADA + equipo RESERVADO                    | Caso de uso   |
| TC-15 | Cancelar SOLICITADA              | CANCELADA y disponibilidad coherente             | Transición    |
| TC-16 | Cancelar CANCELADA               | Acción no disponible / sin cambio                | Transición    |
| TC-17 | Volver desde detalle/formulario  | Back stack correcto                              | Navegación    |
| TC-18 | Fuente 1.5× y texto largo        | Contenido y acción esenciales utilizables        | Accesibilidad |

---

## 📌 Bitácora de ejecución
- TC-01 → PASS
- TC-13 → FAIL (duplicación detectada) → BUG-03 registrado
- TC-15 → PASS
- TC-16 → PASS
- TC-18 → PASS

---

## 📌 Informe ejecutivo de calidad
El incremento cumple con la mayoría de criterios de aceptación. Se identificó un defecto crítico (BUG-03: doble pulsación genera reservas duplicadas). Se recomienda corrección y regresión antes de declarar el incremento como finalizado.


## 📌 Checklist de Accesibilidad y UX

| Criterio | Descripción | Estado |
|----------|-------------|--------|
| Texto + color | La disponibilidad de equipos se comunica con texto, no solo con color. | ✅ |
| Fuente aumentada | Acciones esenciales siguen siendo utilizables con tamaño de fuente 1.5×. | ✅ |
| Mensajes de error | Campos inválidos muestran mensajes específicos y comprensibles. | ✅ |
| Etiquetas claras | Los botones y campos tienen etiquetas explícitas (ej. “Ver detalle”, “Solicitar préstamo”). | ✅ |
| Contraste | Se mantiene contraste suficiente entre texto y fondo según Material 3. | ✅ |
| Jerarquía visual | La UI respeta jerarquía de títulos, listas y botones. | ✅ |
| Texto no truncado | Ningún texto esencial queda cortado o ilegible. | ✅ |
| Consistencia | Los cambios de estado (ej. cancelar solicitud) se reflejan en catálogo y detalle. | ✅ |



