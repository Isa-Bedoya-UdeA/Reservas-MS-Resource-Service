# Pruebas de API - MS-Schedule-Service v2.0

Este documento contiene las pruebas de API para el microservicio de Horarios y Empleados con la nueva arquitectura separada.

## Cambio de Arquitectura (v2.0)

### Problema Resuelto
El diseño anterior con la tabla `disponibilidad` tenía un problema: cuando se creaba una reserva para un día específico, el sistema bloqueaba el horario para todos los días futuros del mismo día de la semana.

### Nueva Arquitectura
Se separaron los conceptos en:
1. **Horarios Laborales (WorkSchedule)**: Define cuándo trabaja el empleado SEMANALMENTE (recurrente)
2. **Bloqueos de Horario (ScheduleBlock)**: Define ocupaciones por FECHA ESPECÍFICA (reservas, vacaciones, etc.)

---

## Configuración Inicial

### Variables de Entorno

Para ejecutar las pruebas, asegúrate de configurar las siguientes variables en tu archivo `.env`:

```bash
# SPRING PROFILE
SPRING_PROFILE=dev

# DATABASE CONFIG - SUPABASE (Transaction Pooler - IPv4 compatible)
DB_URL=jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
DB_USER=postgres.[TU-PROJECT-REF]
DB_PASSWORD=[TU-CONTRASEÑA-DE-SUPABASE]

# EXTERNAL SERVICES URLs
SERVICES_AUTH_URL=http://localhost:8081
```

### Configuración de JWT

El microservicio de Schedule requiere autenticación JWT para operaciones que requieren roles específicos (PROVEEDOR). Debes obtener un token JWT del microservicio de Autenticación.

**Para obtener un token JWT:**
1. Inicia sesión en el Auth Service: `POST http://localhost:8081/api/auth/login`
2. Usa el `accessToken` retornado en el header `Authorization: Bearer [JWT_TOKEN]`

---

## Empleados

### 1. Crear Empleado (Requiere ROLE_PROVEEDOR)

**Nombre:** Create Employee - Success
**URL:** `http://localhost:8083/api/schedule/employees`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "fullName": "John Doe",
    "phone": "+573001234567",
    "notes": "Barbería especializado en cortes modernos"
}
```
**Código esperado:** 201 Created
**Response esperado:**
```json
{
    "id": "uuid-del-empleado",
    "providerId": "uuid-del-proveedor",
    "fullName": "John Doe",
    "phone": "+573001234567",
    "active": true,
    "dateHired": "2026-04-28T12:00:00",
    "notes": "Barbería especializado en cortes modernos",
    "createdAt": "2026-04-28T12:00:00",
    "updatedAt": "2026-04-28T12:00:00"
}
```

---

## Horarios Laborales (Work Schedules)

### 2. Crear Horario Laboral (Requiere ROLE_PROVEEDOR)

**Nombre:** Create Work Schedule - Success
**URL:** `http://localhost:8083/api/schedule/work-schedules`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "employeeId": "uuid-del-empleado",
    "dayOfWeek": "LUNES",
    "startTime": "08:00",
    "endTime": "17:00"
}
```
**Código esperado:** 201 Created
**Response esperado:**
```json
{
    "id": "uuid-del-horario",
    "employeeId": "uuid-del-empleado",
    "dayOfWeek": "LUNES",
    "startTime": "08:00",
    "endTime": "17:00",
    "active": true,
    "createdAt": "2026-04-28T12:00:00",
    "updatedAt": "2026-04-28T12:00:00"
}
```

### 3. Crear Horario Laboral - Conflicto (409 Conflict)

**Nombre:** Create Work Schedule - Conflict
**URL:** `http://localhost:8083/api/schedule/work-schedules`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:** (Intenta crear un horario que se superpone con uno existente)
```json
{
    "employeeId": "uuid-del-empleado",
    "dayOfWeek": "LUNES",
    "startTime": "10:00",
    "endTime": "18:00"
}
```
**Código esperado:** 409 Conflict
**Response esperado:**
```json
{
    "timestamp": "2026-04-30T12:00:00",
    "status": 409,
    "error": "Conflict",
    "message": "Ya existe un horario laboral que se superpone con el rango de tiempo especificado para el día LUNES",
    "path": "/api/schedule/work-schedules"
}
```

### 4. Actualizar Horario Laboral (Requiere ROLE_PROVEEDOR)

**Nombre:** Update Work Schedule - Success
**URL:** `http://localhost:8083/api/schedule/work-schedules/[UUID-HORARIO]`
**Método:** PUT
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "dayOfWeek": "MARTES",
    "startTime": "09:00",
    "endTime": "18:00"
}
```
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "id": "uuid-del-horario",
    "employeeId": "uuid-del-empleado",
    "dayOfWeek": "MARTES",
    "startTime": "09:00",
    "endTime": "18:00",
    "active": true,
    "createdAt": "2026-04-28T12:00:00",
    "updatedAt": "2026-04-30T12:00:00"
}
```

### 5. Eliminar Horario Laboral (Requiere ROLE_PROVEEDOR)

**Nombre:** Delete Work Schedule - Success
**URL:** `http://localhost:8083/api/schedule/work-schedules/[UUID-HORARIO]`
**Método:** DELETE
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

### 6. Obtener Horario Laboral por ID (Requiere ROLE_PROVEEDOR)

**Nombre:** Get Work Schedule by ID - Success
**URL:** `http://localhost:8083/api/schedule/work-schedules/[UUID-HORARIO]`
**Método:** GET
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 200 OK
**Response esperado:** (mismo formato que creación)

### 7. Listar Horarios Laborales de Empleado (Requiere ROLE_PROVEEDOR)

**Nombre:** Get Work Schedules by Employee - Success
**URL:** `http://localhost:8083/api/schedule/work-schedules/employee/[UUID-EMPLEADO]`
**Método:** GET
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 200 OK
**Response esperado:** (array de horarios)

### 8. Listar Horarios Activos de Empleado (Público)

**Nombre:** Get Active Work Schedules by Employee - Success
**URL:** `http://localhost:8083/api/schedule/work-schedules/employee/[UUID-EMPLEADO]/active`
**Método:** GET
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_CUALQUIERA]
```
**Código esperado:** 200 OK
**Response esperado:** (array de horarios activos ordenados)

---

## Bloqueos de Horario (Schedule Blocks)

### 9. Crear Bloqueo de Horario (Requiere ROLE_PROVEEDOR)

**Nombre:** Create Schedule Block - Success
**URL:** `http://localhost:8083/api/schedule/schedule-blocks`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "employeeId": "uuid-del-empleado",
    "date": "2026-12-15",
    "startTime": "10:00",
    "endTime": "11:00",
    "blockType": "VACACIONES"
}
```
**Código esperado:** 201 Created
**Response esperado:**
```json
{
    "id": "uuid-del-bloqueo",
    "employeeId": "uuid-del-empleado",
    "reservationId": null,
    "date": "2026-12-15",
    "startTime": "10:00",
    "endTime": "11:00",
    "blockType": "VACACIONES",
    "active": true,
    "createdAt": "2026-04-30T12:00:00",
    "updatedAt": "2026-04-30T12:00:00"
}
```

### 10. Crear Bloqueo de Horario - Fuera de Horario Laboral (400 Bad Request)

**Nombre:** Create Schedule Block - Outside Work Hours
**URL:** `http://localhost:8083/api/schedule/schedule-blocks`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:** (Intenta bloquear un horario cuando el empleado no trabaja)
```json
{
    "employeeId": "uuid-del-empleado",
    "date": "2026-12-15",
    "startTime": "20:00",
    "endTime": "21:00",
    "blockType": "VACACIONES"
}
```
**Código esperado:** 400 Bad Request
**Response esperado:**
```json
{
    "timestamp": "2026-04-30T12:00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "El empleado no trabaja durante el horario especificado para el día LUNES",
    "path": "/api/schedule/schedule-blocks"
}
```

### 11. Crear Bloqueo para Reserva (Interno)

**Nombre:** Create Reservation Block - Success
**URL:** `http://localhost:8083/api/schedule/schedule-blocks/reservation`
**Método:** POST
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_MICROSERVICIO_RESERVAS]
Content-Type: application/json
```
**Body:**
```json
{
    "employeeId": "uuid-del-empleado",
    "reservationId": "uuid-de-la-reserva",
    "date": "2026-12-15",
    "startTime": "14:00",
    "endTime": "15:00"
}
```
**Código esperado:** 201 Created
**Response esperado:** (vacío)

### 12. Cancelar Bloqueo de Reserva (Interno)

**Nombre:** Cancel Reservation Block - Success
**URL:** `http://localhost:8083/api/schedule/schedule-blocks/reservation/[UUID-RESERVA]`
**Método:** DELETE
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_MICROSERVICIO_RESERVAS]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

### 13. Verificar Disponibilidad de Empleado

**Nombre:** Check Employee Availability - Available
**URL:** `http://localhost:8083/api/schedule/schedule-blocks/check-availability`
**Método:** POST
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_CUALQUIERA]
Content-Type: application/json
```
**Body:**
```json
{
    "employeeId": "uuid-del-empleado",
    "date": "2026-12-15",
    "startTime": "14:00",
    "endTime": "15:00"
}
```
**Código esperado:** 200 OK
**Response esperado:** `true`

### 14. Listar Bloqueos por Rango de Fechas

**Nombre:** Get Schedule Blocks by Date Range - Success
**URL:** `http://localhost:8083/api/schedule/schedule-blocks/date-range`
**Método:** POST
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_CUALQUIERA]
Content-Type: application/json
```
**Body:**
```json
{
    "employeeId": "uuid-del-empleado",
    "startDate": "2026-12-01",
    "endDate": "2026-12-31"
}
```
**Código esperado:** 200 OK
**Response esperado:** (array de bloqueos en el rango)

---

## Health Check

### 15. Health Check

**Nombre:** Health Check - Success
**URL:** `http://localhost:8083/api/`
**Método:** GET
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "status": "UP",
    "timestamp": "2026-04-28T12:00:00"
}
```

### 16. Version Check

**Nombre:** Version Check - Success
**URL:** `http://localhost:8083/api/version`
**Método:** GET
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "version": "2.0.0-SNAPSHOT",
    "service": "Reservas-MS-Schedule-Service"
}
```

---

## Notas Importantes

### Cambios Principales v2.0

1. **Nuevos Endpoints:**
   - `/api/schedule/work-schedules/*` - Para horarios laborales recurrentes
   - `/api/schedule/schedule-blocks/*` - Para bloqueos por fecha específica

2. **Endpoint Eliminados:**
   - `/api/schedule/availability/*` - Reemplazado por los nuevos endpoints

3. **Validación de Disponibilidad:**
   - Los bloqueos solo se pueden crear dentro de horarios laborales existentes
   - Las reservas ahora crean bloqueos específicos por fecha, no afectan horarios recurrentes

4. **Tipos de Bloqueo:**
   - `RESERVA` - Creado automáticamente por MS-Reservation
   - `VACACIONES` - Creado manualmente por proveedor
   - `PERMISO` - Creado manualmente por proveedor
   - `ADMINISTRATIVO` - Creado manualmente por proveedor

### Códigos de Error

- **400 Bad Request**: Datos inválidos, fuera de horario laboral, fecha pasada
- **401 Unauthorized**: No autenticado
- **403 Forbidden**: No tiene rol PROVEEDOR o no es dueño del empleado
- **404 Not Found**: Recurso no encontrado
- **409 Conflict**: Superposición de horarios o bloqueos
    "notes": "Senior stylist with 5 years experience"
}
```
**Código esperado:** 201 Created
**Response esperado:**
```json
{
    "id": "[UUID-NUEVO-EMPLEADO]",
    "providerId": "[UUID-PROVEEDOR]",
    "fullName": "John Doe",
    "phone": "+573001234567",
    "active": true,
    "hireDate": "2026-04-28T12:00:00",
    "notes": "Senior stylist with 5 years experience",
    "createdAt": "2026-04-28T12:00:00",
    "updatedAt": "2026-04-28T12:00:00"
}
```
**Nota:** El `providerId` se obtiene automáticamente del JWT del proveedor autenticado.

---

### 2. Crear Empleado con Datos Inválidos (400)

**Nombre:** Create Employee - Invalid Data
**URL:** `http://localhost:8083/api/schedule/employees`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "fullName": "  ",
    "phone": "123456789012345678901",
    "notes": "a"
}
```
**Código esperado:** 400 Bad Request
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 400,
    "error": "Validation Error",
    "message": "Validation errors in input data",
    "validationErrors": {
        "fullName": "Full name is required",
        "phone": "Phone number cannot exceed 20 characters"
    }
}
```

---

### 3. Crear Empleado sin Autorización (401)

**Nombre:** Create Employee - Unauthorized
**URL:** `http://localhost:8083/api/schedule/employees`
**Método:** POST
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
    "fullName": "John Doe",
    "phone": "+573001234567"
}
```
**Código esperado:** 401 Unauthorized
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Full authentication is required to access this resource"
}
```

---

### 4. Actualizar Empleado (Requiere ROLE_PROVEEDOR)

**Nombre:** Update Employee - Success
**URL:** `http://localhost:8083/api/schedule/employees/[UUID-EMPLEADO]`
**Método:** PUT
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "fullName": "John Smith",
    "phone": "+573009876543"
}
```
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "id": "[UUID-EMPLEADO]",
    "providerId": "[UUID-PROVEEDOR]",
    "fullName": "John Smith",
    "phone": "+573009876543",
    "active": true,
    "hireDate": "2026-04-28T12:00:00",
    "notes": "Senior stylist with 5 years experience",
    "createdAt": "2026-04-28T12:00:00",
    "updatedAt": "2026-04-28T12:05:00"
}
```
**Nota:** Solo el proveedor creador del empleado puede actualizarlo.

---

### 5. Actualizar Empleado de Otro Proveedor (403)

**Nombre:** Update Employee - Forbidden (Not Owner)
**URL:** `http://localhost:8083/api/schedule/employees/[UUID-EMPLEADO-OTRO-PROVEEDOR]`
**Método:** PUT
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Body:**
```json
{
    "phone": "+573009876543"
}
```
**Código esperado:** 403 Forbidden
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "No tienes permisos para modificar este empleado"
}
```

---

### 6. Eliminar Empleado (Hard Delete) (Requiere ROLE_PROVEEDOR)

**Nombre:** Delete Employee - Success
**URL:** `http://localhost:8083/api/schedule/employees/[UUID-EMPLEADO]`
**Método:** DELETE
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

---

### 7. Desactivar Empleado (Soft Delete) (Requiere ROLE_PROVEEDOR)

**Nombre:** Deactivate Employee - Success
**URL:** `http://localhost:8083/api/schedule/employees/[UUID-EMPLEADO]/deactivate`
**Método:** PATCH
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

---

### 8. Desactivar Empleado Ya Inactivo (409)

**Nombre:** Deactivate Employee - Already Inactive
**URL:** `http://localhost:8083/api/schedule/employees/[UUID-EMPLEADO-INACTIVO]/deactivate`
**Método:** PATCH
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 409 Conflict
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 409,
    "error": "Conflict",
    "message": "El empleado ya está inactivo"
}
```

---

### 9. Activar Empleado (Requiere ROLE_PROVEEDOR)

**Nombre:** Activate Employee - Success
**URL:** `http://localhost:8083/api/schedule/employees/[UUID-EMPLEADO-INACTIVO]/activate`
**Método:** PATCH
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

---

### 10. Activar Empleado Ya Activo (409)

**Nombre:** Activate Employee - Already Active
**URL:** `http://localhost:8083/api/schedule/employees/[UUID-EMPLEADO]/activate`
**Método:** PATCH
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 409 Conflict
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 409,
    "error": "Conflict",
    "message": "El empleado ya está activo"
}
```

---

### 11. Obtener Empleado por ID (Requiere ROLE_PROVEEDOR)

**Nombre:** Get Employee by ID - Success
**URL:** `http://localhost:8083/api/schedule/employees/[UUID-EMPLEADO]`
**Método:** GET
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "id": "[UUID-EMPLEADO]",
    "providerId": "[UUID-PROVEEDOR]",
    "fullName": "John Doe",
    "phone": "+573001234567",
    "active": true,
    "hireDate": "2026-04-28T12:00:00",
    "notes": "Senior stylist with 5 years experience",
    "createdAt": "2026-04-28T12:00:00",
    "updatedAt": "2026-04-28T12:00:00"
}
```

---

### 12. Obtener Empleado No Existente (404)

**Nombre:** Get Employee by ID - Not Found
**URL:** `http://localhost:8083/api/schedule/employees/00000000-0000-0000-0000-000000000000`
**Método:** GET
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 404 Not Found
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Empleado no encontrado con id: 00000000-0000-0000-0000-000000000000",
    "path": "/api/schedule/employees/00000000-0000-0000-0000-000000000000"
}
```

---

### 10. Listar Empleados del Proveedor (Requiere ROLE_PROVEEDOR)

**Nombre:** Get Employees by Provider - Success
**URL:** `http://localhost:8083/api/schedule/employees`
**Método:** GET
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 200 OK
**Response esperado:**
```json
[
    {
        "id": "[UUID-EMPLEADO-1]",
        "providerId": "[UUID-PROVEEDOR]",
        "fullName": "John Doe",
        "phone": "+573001234567",
        "active": true,
        "hireDate": "2026-04-28T12:00:00",
        "notes": "Senior stylist",
        "createdAt": "2026-04-28T12:00:00",
        "updatedAt": "2026-04-28T12:00:00"
    },
    {
        "id": "[UUID-EMPLEADO-2]",
        "providerId": "[UUID-PROVEEDOR]",
        "fullName": "Jane Smith",
        "phone": "+573009876543",
        "active": true,
        "hireDate": "2026-04-28T12:00:00",
        "notes": "Junior stylist",
        "createdAt": "2026-04-28T12:00:00",
        "updatedAt": "2026-04-28T12:00:00"
    }
]
```
**Nota:** Retorna todos los empleados (activos e inactivos) del proveedor autenticado.

---

### 11. Listar Empleados Activos del Proveedor (Público)

**Nombre:** Get Active Employees by Provider - Success
**URL:** `http://localhost:8083/api/schedule/employees/active`
**Método:** GET
**Headers:**
```
Content-Type: application/json
```
**Código esperado:** 200 OK
**Response esperado:**
```json
[
    {
        "id": "[UUID-EMPLEADO-1]",
        "providerId": "[UUID-PROVEEDOR]",
        "fullName": "John Doe",
        "phone": "+573001234567",
        "active": true,
        "hireDate": "2026-04-28T12:00:00",
        "notes": "Senior stylist",
        "createdAt": "2026-04-28T12:00:00",
        "updatedAt": "2026-04-28T12:00:00"
    }
]
```
**Nota:** Retorna solo los empleados activos del proveedor autenticado.

