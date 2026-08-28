# PréstamoLab CTMA – Incremento Android

## 📌 Product Goal

Facilitar la gestión de solicitudes y seguimiento de préstamos de equipos de formación mediante una aplicación móvil intuitiva y accesible.

---

## 📌 Historias de Usuario y Criterios de Aceptación

- **HU-01**: Buscar equipos por categoría.  
  **Criterio**: Al seleccionar una categoría, se muestran únicamente los equipos correspondientes.

- **HU-02**: Filtrar equipos disponibles.  
  **Criterio**: El catálogo permite activar un filtro que muestra solo los equipos en estado `DISPONIBLE`.

- **HU-03**: Editar solicitud en estado `SOLICITADA`.  
  **Criterio**: El usuario puede modificar el propósito y la duración antes de que el equipo sea entregado.

- **HU-04**: Notificación de vencimiento.  
  **Criterio**: Al acercarse la hora de devolución, se envía una alerta al usuario.

- **HU-05**: Historial de solicitudes.  
  **Criterio**: El usuario puede consultar todas sus solicitudes anteriores con estado final `CANCELADA` o `FINALIZADA`.

- **HU-06**: Accesibilidad con lector de pantalla.  
  **Criterio**: Los botones y etiquetas son reconocidos correctamente por TalkBack.

---

## 📌 Matriz de Riesgos

| ID | Riesgo | Prob. | Impacto | Nivel | Cobertura |
|---|---|---|---|---|---|
| R-01 | El filtro no actualiza correctamente la disponibilidad | Alta | Alta | Crítico | TC-05 |
| R-02 | La notificación de vencimiento no se dispara | Media | Alta | Alto | TC-09 |
| R-03 | El historial muestra datos incompletos | Media | Media | Medio | TC-12 |
| R-04 | TalkBack no reconoce correctamente las etiquetas | Alta | Media | Alto | TC-15 |
| R-05 | Una solicitud editada pierde trazabilidad | Media | Alta | Alto | TC-07 |

---

## 📌 Sprint Goal

Permitir filtrar equipos disponibles y registrar solicitudes editables, garantizando trazabilidad, seguimiento de estados y accesibilidad básica.

---

## 📌 Definition of Done

1. El proyecto compila y se ejecuta correctamente.
2. Los criterios de aceptación seleccionados están implementados.
3. La UI no modifica directamente la fuente de datos.
4. El ViewModel expone `UiState`/`StateFlow` de solo lectura.
5. La navegación transporta correctamente los identificadores.
6. Los IDs inexistentes son controlados sin cerrar la aplicación.
7. Los filtros de equipos funcionan correctamente.
8. Las solicitudes pueden editarse cuando se encuentran en estado `SOLICITADA`.
9. El historial de solicitudes muestra correctamente los estados finales.
10. Las notificaciones de vencimiento funcionan correctamente.
11. La aplicación es compatible con funciones básicas de accesibilidad.
12. Se ejecutaron los casos de prueba definidos.
13. Los defectos críticos y altos tienen una decisión explícita.
14. Las correcciones relevantes cuentan con pruebas de regresión.
15. Git y README están actualizados.
16. El incremento puede demostrarse y cada integrante puede explicarlo.

---

# 🧪 Pruebas y casos de prueba

Las pruebas se enfocan principalmente en validar las reglas de negocio y los flujos principales de la aplicación.

| ID | Caso de prueba | Resultado esperado |
|---|---|---|
| **TC-01** | Buscar equipos por categoría | Se muestran únicamente los equipos pertenecientes a la categoría seleccionada. |
| **TC-02** | Categoría sin equipos | Se muestra un mensaje indicando que no existen equipos disponibles. |
| **TC-03** | Filtrar equipos disponibles | Se muestran únicamente los equipos en estado `DISPONIBLE`. |
| **TC-04** | Desactivar filtro de disponibilidad | Se vuelve a mostrar el catálogo completo de equipos. |
| **TC-05** | Actualización del estado del equipo | El catálogo refleja correctamente el nuevo estado del equipo. |
| **TC-06** | Editar solicitud `SOLICITADA` | El usuario puede modificar los datos permitidos de la solicitud. |
| **TC-07** | Editar solicitud después de entregada | El sistema no permite modificar una solicitud que ya fue entregada. |
| **TC-08** | Editar solicitud cancelada | El sistema no permite editar una solicitud `CANCELADA`. |
| **TC-09** | Notificación próxima al vencimiento | Se muestra una alerta al usuario antes de la hora de devolución. |
| **TC-10** | Solicitud sin vencimiento próximo | No se muestra una notificación innecesaria. |
| **TC-11** | Consultar historial | Se muestran las solicitudes anteriores del usuario. |
| **TC-12** | Historial con diferentes estados | Se muestran correctamente los estados `CANCELADA` y `FINALIZADA`. |
| **TC-13** | Historial sin solicitudes | Se muestra un mensaje indicando que no existen registros. |
| **TC-14** | Uso de TalkBack | Los elementos principales de la interfaz son reconocidos por el lector de pantalla. |
| **TC-15** | Botones con TalkBack | Los botones tienen etiquetas comprensibles y permiten identificar su función. |
| **TC-16** | Campo editable con TalkBack | Los campos pueden ser identificados y utilizados mediante el lector de pantalla. |
| **TC-17** | ID de equipo inexistente | La aplicación controla el error sin cerrarse. |
| **TC-18** | ID de solicitud inexistente | La aplicación controla el error sin cerrarse. |

---

## 📌 Bitácora de ejecución

- TC-01 → PASS
- TC-03 → PASS
- TC-06 → PASS
- TC-09 → PASS
- TC-12 → PASS
- TC-14 → PASS
- TC-17 → PASS
- TC-18 → PASS

---

## 📌 Informe ejecutivo de calidad

El incremento permite realizar búsquedas y filtros de equipos, editar solicitudes en estado `SOLICITADA`, consultar el historial de préstamos y utilizar funciones básicas de accesibilidad.

Durante la ejecución de las pruebas se validaron los principales flujos de búsqueda, filtrado, edición, notificaciones, consulta de historial y accesibilidad.

Los casos de prueba ejecutados presentaron resultados satisfactorios y no se identificaron defectos críticos durante la validación del incremento.

Se recomienda mantener las pruebas de regresión para garantizar que futuras modificaciones no afecten los filtros, la edición de solicitudes, el historial ni las funciones de accesibilidad.

---

## 📌 Checklist de Accesibilidad y UX

| Criterio | Descripción | Estado |
|---|---|---|
| Texto + color | Los estados de los equipos se comunican mediante texto y no únicamente mediante color. | ✅ |
| Fuente aumentada | Las acciones esenciales continúan siendo utilizables con tamaño de fuente 1.5×. | ✅ |
| Mensajes de error | Los errores muestran mensajes específicos y comprensibles. | ✅ |
| Etiquetas claras | Los botones y campos tienen etiquetas explícitas. | ✅ |
| TalkBack | Los elementos principales son reconocidos correctamente por el lector de pantalla. | ✅ |
| Contraste | Se mantiene un contraste suficiente entre texto y fondo. | ✅ |
| Jerarquía visual | La interfaz mantiene una jerarquía clara entre títulos, listas y acciones. | ✅ |
| Texto no truncado | Ningún texto esencial queda cortado o ilegible. | ✅ |
| Consistencia | Los cambios realizados en las solicitudes se reflejan correctamente en la aplicación. | ✅ |
| Navegación | El usuario puede desplazarse y regresar entre las pantallas correctamente. | ✅ |