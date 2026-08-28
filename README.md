📱 PréstamoLab CTMA – Incremento Android
📌 Product Goal
Facilitar la gestión de solicitudes y seguimiento de préstamos de equipos de formación mediante una aplicación móvil intuitiva y accesible.

📌 Historias de Usuario y Criterios de Aceptación
HU-01: Buscar equipos por categoría  
Criterio: Al ingresar una categoría válida, se muestran únicamente los equipos correspondientes.

HU-02: Filtrar equipos disponibles  
Criterio: El catálogo permite activar un filtro que muestra solo los equipos en estado DISPONIBLE.

HU-03: Editar solicitud en estado SOLICITADA  
Criterio: El usuario puede modificar propósito y duración antes de que el equipo sea entregado.

HU-04: Notificación de vencimiento  
Criterio: Al acercarse la hora de devolución, se envía una alerta al usuario.

HU-05: Historial de solicitudes  
Criterio: El usuario puede consultar todas sus solicitudes anteriores con estado final (CANCELADA, FINALIZADA).

HU-06: Accesibilidad con lector de pantalla  
Criterio: Los botones y etiquetas son reconocidos correctamente por TalkBack.

📌 Matriz de Riesgos
ID	Riesgo	Prob.	Impacto	Nivel	Cobertura
R-01	Filtro no actualiza disponibilidad	Alta	Alta	Crítico	TC-05
R-02	Notificación no se dispara	Media	Alta	Alto	TC-09
R-03	Historial muestra datos incompletos	Media	Media	Medio	TC-12
R-04	TalkBack no reconoce etiquetas	Alta	Media	Alto	TC-15
R-05	Solicitud editada pierde trazabilidad	Media	Alta	Alto	TC-07


📌 Sprint Goal
Permitir filtrar equipos disponibles y registrar solicitudes editables, garantizando trazabilidad y accesibilidad básica.

📌 Definition of Done
El proyecto compila y corre en emulador.

Los criterios de aceptación seleccionados están implementados.

La UI respeta accesibilidad (TalkBack, contraste, etiquetas).

Los filtros y notificaciones funcionan en pruebas reproducibles.

Git y README están actualizados.

Cada integrante puede explicar el incremento y demostrarlo.

📌 Suite de Pruebas (ejemplo 15 casos)
ID	Escenario	Resultado esperado	Técnica
TC-01	Catálogo con datos	Equipos visibles con disponibilidad	Caso de uso
TC-02	Filtro DISPONIBLE activado	Solo equipos DISPONIBLE	Decisión
TC-03	Filtro sin resultados	Mensaje claro “No hay equipos disponibles”	Negativa
TC-04	Solicitud SOLICITADA editable	Propósito/duración se actualizan	Caso de uso
TC-05	Solicitud RESERVADA no editable	Mensaje “No se puede editar”	Decisión
TC-06	Notificación vencimiento activa	Alerta enviada antes de devolución	Caso de uso
TC-07	Notificación no configurada	No se dispara	Negativa
TC-08	Historial vacío	Mensaje “No hay solicitudes previas”	Negativa
TC-09	Historial con datos	Lista completa con estados finales	Caso de uso
TC-10	TalkBack en botones principales	Lector reconoce etiquetas	Accesibilidad
TC-11	Contraste en tema oscuro	Texto legible según Material 3	Accesibilidad
TC-12	Texto largo en propósito	No se trunca, mensaje claro	Límite
TC-13	Duración fuera de rango	No guarda, mensaje específico	Límite
TC-14	Cancelar solicitud editada	Estado coherente en catálogo	Transición
TC-15	Back stack correcto	Navegación retorna a pantalla previa	Navegación


📌 Informe ejecutivo de calidad
El incremento cumple con la mayoría de criterios de aceptación. Se identificaron riesgos en accesibilidad y notificaciones que requieren pruebas adicionales. Se recomienda corrección y regresión antes de declarar el incremento finalizado.

📌 Checklist de Accesibilidad y UX
Criterio	Descripción	Estado
Texto + color	Disponibilidad comunicada con texto y color	✅
TalkBack	Botones y etiquetas reconocidos	⚠️
Mensajes de error	Campos inválidos muestran mensajes claros	✅
Contraste	Cumple Material 3 en tema claro/oscuro	✅
Jerarquía visual	Respeta títulos, listas y botones	✅
Texto no truncado	Propósitos largos se muestran completos	✅
Consistencia	Cambios reflejados en catálogo y detalle	✅