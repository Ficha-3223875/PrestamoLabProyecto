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
