# PréstamoLab CTMA

## 📌 Product Goal
Mejorar la trazabilidad y consulta de préstamos de recursos de formación mediante una experiencia móvil Android, con validaciones y pruebas reproducibles.

## 👥 Usuarios
- Aprendiz: consulta catálogo y solicita préstamo.
- Instructor: valida evidencias y observa el proceso.
- Gestor simulado: representa cambios de estado en pruebas.

## 🎯 Alcance del primer incremento
- Consultar catálogo de equipos.
- Ver detalle de un equipo.
- Registrar solicitud con destino, propósito y duración.
- Validar reglas de negocio (propósito, duración, disponibilidad).
- Evitar duplicación por doble pulsación.
- Cancelar solicitudes en estado SOLICITADA.
- Mantener datos en repositorio simulado.

## 🛠️ Tecnologías
- Kotlin + Jetpack Compose
- ViewModel + StateFlow
- Repository InMemory
- Git + GitHub

## ✅ Definition of Done mínima
1. El proyecto compila y ejecuta.
2. Los criterios de aceptación están implementados.
3. La navegación controla IDs inexistentes.
4. Se ejecutaron pruebas acordadas.
5. Git y README actualizados.

## 📖 Historias de Usuario

1. **Consultar catálogo de equipos**  
   *Como aprendiz, quiero ver un catálogo de equipos con su disponibilidad para saber qué puedo solicitar.*  
   **Criterio de aceptación:** Al abrir el catálogo, se muestran nombre, categoría y estado de cada equipo.

2. **Registrar solicitud de préstamo**  
   *Como aprendiz, quiero registrar una solicitud con destino, propósito y duración para usar un equipo en prácticas.*  
   **Criterio de aceptación:** Si los datos son válidos, se crea una sola solicitud en estado SOLICITADA y el equipo pasa a RESERVADO.

3. **Cancelar solicitud**  
   *Como aprendiz, quiero cancelar una solicitud en estado SOLICITADA para liberar el equipo.*  
   **Criterio de aceptación:** Al cancelar, la solicitud pasa a CANCELADA y el equipo vuelve a DISPONIBLE.

4. **Validar propósito mínimo**  
   *Como aprendiz, quiero que el sistema me avise si escribo un propósito demasiado corto para asegurar claridad.*  
   **Criterio de aceptación:** Si el propósito tiene menos de 10 caracteres, no se guarda y aparece un mensaje específico.

5. **Evitar duplicación por doble clic**  
   *Como aprendiz, quiero que el sistema ignore pulsaciones repetidas para no generar solicitudes duplicadas.*  
   **Criterio de aceptación:** Al presionar dos veces el botón de guardar, solo se crea una solicitud.

6. **Controlar duración máxima**  
   *Como aprendiz, quiero que el sistema valide la duración del préstamo para evitar tiempos excesivos.*  
   **Criterio de aceptación:** Si la duración supera el límite definido, aparece un mensaje y no se guarda la solicitud.

## ⚠️ Matriz de Riesgos

| Riesgo | Descripción | Probabilidad | Impacto | Mitigación |
|--------|-------------|--------------|---------|------------|
| **[Duplicación de solicitudes](ca://s?q=Riesgo_duplicacion_de_solicitudes_en_PrestamoLab_CTMA)** | El usuario pulsa dos veces el botón y se crean solicitudes duplicadas. | Alta | Medio | Implementar control de doble clic y validación en repositorio. |
| **[Propósito demasiado corto](ca://s?q=Riesgo_propósito_corto_en_PrestamoLab_CTMA)** | El aprendiz ingresa un propósito con menos de 10 caracteres. | Media | Bajo | Validar longitud mínima y mostrar mensaje de error. |
| **[Duración fuera de rango](ca://s?q=Riesgo_duracion_fuera_de_rango_en_PrestamoLab_CTMA)** | Se ingresan tiempos de préstamo superiores al límite permitido. | Media | Alto | Validar duración máxima y rechazar solicitudes inválidas. |
| **[ID inexistente](ca://s?q=Riesgo_ID_inexistente_en_PrestamoLab_CTMA)** | El sistema recibe un ID de equipo o solicitud que no existe. | Baja | Alto | Manejar navegación segura y mostrar mensaje de “no encontrado”. |
| **[Accesibilidad limitada](ca://s?q=Riesgo_accesibilidad_limitada_en_PrestamoLab_CTMA)** | Usuarios con dificultades visuales no pueden usar la app correctamente. | Media | Medio | Usar contrastes adecuados, etiquetas y soporte de TalkBack. |

## 🎯 Alcance funcional mínimo del incremento

1. **[Consultar catálogo](ca://s?q=Alcance_funcional_consultar_catalogo_en_PrestamoLab_CTMA)**  
   Mostrar un catálogo de equipos con nombre, categoría y disponibilidad.

2. **[Ver detalle de equipo](ca://s?q=Alcance_funcional_ver_detalle_equipo_en_PrestamoLab_CTMA)**  
   Abrir el detalle de un equipo utilizando `equipoId` como argumento de navegación.

3. **[Registrar solicitud](ca://s?q=Alcance_funcional_registrar_solicitud_en_PrestamoLab_CTMA)**  
   Permitir registrar una solicitud de préstamo para un equipo disponible.

4. **[Ingresar datos obligatorios](ca://s?q=Alcance_funcional_ingresar_datos_obligatorios_en_PrestamoLab_CTMA)**  
   Solicitar ambiente/destino, propósito y duración estimada.

5. **[Validar reglas de negocio](ca://s?q=Alcance_funcional_validar_reglas_en_PrestamoLab_CTMA)**  
   Verificar que los datos cumplan con las restricciones antes de guardar.

6. **[Mis solicitudes](ca://s?q=Alcance_funcional_mis_solicitudes_en_PrestamoLab_CTMA)**  
   Mostrar la lista de solicitudes creadas y el detalle de cada una.

7. **[Evitar duplicación](ca://s?q=Alcance_funcional_evitar_duplicacion_en_PrestamoLab_CTMA)**  
   Prevenir solicitudes duplicadas por doble pulsación del botón Guardar.

8. **[Actualizar disponibilidad](ca://s?q=Alcance_funcional_actualizar_disponibilidad_en_PrestamoLab_CTMA)**  
   Cambiar el estado del equipo de acuerdo con la solicitud creada.

9. **[Controlar IDs inexistentes](ca://s?q=Alcance_funcional_controlar_IDs_inexistentes_en_PrestamoLab_CTMA)**  
   Manejar identificadores inválidos sin que la aplicación se cierre abruptamente.

10. **[Cancelar solicitud](ca://s?q=Alcance_funcional_cancelar_solicitud_en_PrestamoLab_CTMA)**  
    Permitir cancelar una solicitud que todavía esté en estado SOLICITADA.

11. **[Repositorio simulado](ca://s?q=Alcance_funcional_repositorio_simulado_en_PrestamoLab_CTMA)**  
    Mantener los datos durante la ejecución mediante un repositorio InMemory compartido.

12. **[Accesibilidad básica](ca://s?q=Alcance_funcional_accesibilidad_basica_en_PrestamoLab_CTMA)**  
    Presentar mensajes accesibles y no depender únicamente del color.

## ✅ Definition of Done (DoD) mínima

Un incremento se considera terminado cuando cumple con todos estos criterios:

1. **[Compilación y ejecución](ca://s?q=DoD_compilacion_y_ejecucion_en_PrestamoLab_CTMA)**  
   El proyecto compila y puede ejecutarse en el ambiente definido.

2. **[Criterios implementados](ca://s?q=DoD_criterios_implementados_en_PrestamoLab_CTMA)**  
   Los criterios de aceptación seleccionados están implementados.

3. **[UI desacoplada](ca://s?q=DoD_UI_desacoplada_en_PrestamoLab_CTMA)**  
   La interfaz no modifica directamente la fuente de datos.

4. **[ViewModel con UiState](ca://s?q=DoD_ViewModel_con_UiState_en_PrestamoLab_CTMA)**  
   El ViewModel expone UiState/StateFlow de solo lectura.

5. **[Navegación segura](ca://s?q=DoD_navegacion_segura_en_PrestamoLab_CTMA)**  
   La navegación transporta identificadores y controla IDs inexistentes.

6. **[Pruebas ejecutadas](ca://s?q=DoD_pruebas_ejecutadas_en_PrestamoLab_CTMA)**  
   Se ejecutaron los casos acordados y los resultados son reales.

7. **[Defectos gestionados](ca://s?q=DoD_defectos_gestionados_en_PrestamoLab_CTMA)**  
   Los defectos críticos/altos tienen decisión explícita.

8. **[Confirmación y regresión](ca://s?q=DoD_confirmacion_y_regresion_en_PrestamoLab_CTMA)**  
   Las correcciones relevantes tienen confirmación y pruebas de regresión.

9. **[Repositorio actualizado](ca://s?q=DoD_repositorio_actualizado_en_PrestamoLab_CTMA)**  
   Git y README están actualizados.

10. **[Demostración y autoría](ca://s?q=DoD_demostracion_y_autoria_en_PrestamoLab_CTMA)**  
    El incremento puede demostrarse y cada integrante puede explicarlo.

## 📌 Product Backlog inicial

| ID     | Historia / necesidad | Prioridad | Riesgo |
|--------|----------------------|-----------|--------|
| **[PB-01](ca://s?q=Backlog_PB01_consultar_catalogo_en_PrestamoLab_CTMA)** | Consultar catálogo de equipos y disponibilidad. | Alta | Alto |
| **[PB-02](ca://s?q=Backlog_PB02_consultar_detalle_equipo_en_PrestamoLab_CTMA)** | Consultar detalle de un equipo. | Alta | Medio |
| **[PB-03](ca://s?q=Backlog_PB03_registrar_solicitud_en_PrestamoLab_CTMA)** | Registrar solicitud de préstamo. | Alta | Alto |
| **[PB-04](ca://s?q=Backlog_PB04_validar_propósito_destino_duracion_en_PrestamoLab_CTMA)** | Validar propósito, destino y duración. | Alta | Alto |
| **[PB-05](ca://s?q=Backlog_PB05_evitar_solicitud_equipo_no_disponible_en_PrestamoLab_CTMA)** | Evitar solicitud sobre equipo no disponible. | Alta | Alto |
| **[PB-06](ca://s?q=Backlog_PB06_evitar_duplicacion_por_doble_pulsacion_en_PrestamoLab_CTMA)** | Evitar duplicación por doble pulsación. | Alta | Alto |
| **[PB-07](ca://s?q=Backlog_PB07_consultar_mis_solicitudes_en_PrestamoLab_CTMA)** | Consultar mis solicitudes. | Media | Medio |
| **[PB-08](ca://s?q=Backlog_PB08_consultar_detalle_solicitud_en_PrestamoLab_CTMA)** | Consultar detalle de solicitud. | Media | Medio |
| **[PB-09](ca://s?q=Backlog_PB09_cancelar_solicitud_SOLICITADA_en_PrestamoLab_CTMA)** | Cancelar solicitud en estado SOLICITADA. | Media | Medio |
| **[PB-10](ca://s?q=Backlog_PB10_manejar_IDs_inexistentes_en_PrestamoLab_CTMA)** | Manejar IDs inexistentes y estados vacíos. | Media | Medio |
| **[PB-11](ca://s?q=Backlog_PB11_accesibilidad_interfaz_en_PrestamoLab_CTMA)** | Mantener interfaz usable con texto aumentado. | Media | Medio |
| **[PB-12](ca://s?q=Backlog_PB12_documentar_arquitectura_y_pruebas_en_PrestamoLab_CTMA)** | Documentar arquitectura, pruebas y limitaciones. | Media | Medio |