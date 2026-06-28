# Requerimientos — Sistema de Gestión Veterinaria

## Roles del sistema

| Rol | Permisos |
|---|---|
| **Admin** | Acceso total, configuración, reportes, usuarios |
| **Veterinario** | Historia clínica, consultas, recetas, citas propias |
| **Recepcionista** | Clientes, mascotas, citas, facturación |

---

## Requerimientos Funcionales

### 1. Autenticación y usuarios

- RF01 — Login con email y contraseña
- RF02 — JWT con expiración y refresh token
- RF03 — CRUD de usuarios con asignación de rol
- RF04 — Cambio de contraseña
- RF05 — Recuperación de contraseña por email

### 2. Clientes (dueños)

- RF06 — Registrar cliente: nombre, DNI, teléfono, email, dirección
- RF07 — Buscar cliente por nombre o DNI
- RF08 — Editar y desactivar cliente
- RF09 — Ver todas las mascotas de un cliente
- RF10 — Historial de citas y pagos del cliente

### 3. Mascotas

- RF11 — Registrar mascota: nombre, especie, raza, sexo, fecha de nacimiento, color, foto
- RF12 — Asociar mascota a un cliente
- RF13 — Editar datos de mascota
- RF14 — Ver ficha completa de mascota (datos + historial + vacunas)
- RF15 — Marcar mascota como fallecida (no eliminar)

### 4. Citas

- RF16 — Agendar cita: fecha, hora, veterinario, mascota, motivo
- RF17 — Vista de calendario semanal/diario por veterinario
- RF18 — Estados de cita: Pendiente, Confirmada, En atención, Completada, Cancelada
- RF19 — Cancelar cita con motivo
- RF20 — Evitar solapamiento de citas por veterinario
- RF21 — Recordatorio por email 24h antes de la cita

### 5. Historia clínica

- RF22 — Crear consulta al completar una cita
- RF23 — Registrar: peso, temperatura, síntomas, diagnóstico, tratamiento, observaciones
- RF24 — Adjuntar archivos a la consulta (radiografías, análisis)
- RF25 — Ver historial cronológico de consultas por mascota
- RF26 — Exportar historial clínico en PDF

### 6. Vacunas y desparasitación

- RF27 — Registrar vacuna aplicada: nombre, fecha, dosis, lote, próxima dosis
- RF28 — Alerta automática cuando una vacuna está próxima a vencer (7 días antes)
- RF29 — Ver carnet de vacunación por mascota
- RF30 — Exportar carnet en PDF

### 7. Recetas

- RF31 — Crear receta vinculada a una consulta
- RF32 — Agregar medicamentos con dosis, frecuencia y duración
- RF33 — Exportar receta en PDF con membrete de la clínica

### 8. Inventario

- RF34 — CRUD de productos/medicamentos: nombre, categoría, stock, precio, vencimiento
- RF35 — Descontar stock automáticamente al usar en una consulta o receta
- RF36 — Alerta de stock bajo (umbral configurable)
- RF37 — Alerta de productos próximos a vencer

### 9. Facturación

- RF38 — Generar factura por consulta/servicios prestados
- RF39 — Agregar productos del inventario a la factura
- RF40 — Estados de pago: Pendiente, Pagado, Anulado
- RF41 — Métodos de pago: efectivo, tarjeta, transferencia
- RF42 — Exportar factura en PDF

### 10. Reportes y dashboard

- RF43 — Dashboard: citas del día, ingresos del mes, mascotas atendidas
- RF44 — Reporte de ingresos por rango de fechas
- RF45 — Reporte de citas por veterinario
- RF46 — Reporte de productos más usados
- RF47 — Exportar reportes en PDF o Excel

---

## Requerimientos No Funcionales

- RNF01 — API REST con Spring Boot, documentada en Swagger/OpenAPI
- RNF02 — Frontend en React con diseño responsive
- RNF03 — Base de datos PostgreSQL
- RNF04 — Autenticación stateless con JWT
- RNF05 — Contraseñas cifradas con BCrypt
- RNF06 — Manejo global de errores con mensajes claros
- RNF07 — Paginación en todos los listados
- RNF08 — Logs de auditoría (quién hizo qué y cuándo)
- RNF09 — Deploy en la nube (Railway o Render)
- RNF10 — Tiempo de respuesta de API < 2 segundos
