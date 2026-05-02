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

### 13. Listar Empleados del Proveedor (Requiere ROLE_PROVEEDOR)

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

### 14. Listar Empleados Activos del Proveedor (Público)

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

---

## Horarios Laborales (Work Schedules)

### 15. Crear Horario Laboral (Requiere ROLE_PROVEEDOR)

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

### 16. Crear Horario Laboral - Conflicto (409 Conflict)

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

### 17. Actualizar Horario Laboral (Requiere ROLE_PROVEEDOR)

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

### 18. Eliminar Horario Laboral (Requiere ROLE_PROVEEDOR)

**Nombre:** Delete Work Schedule - Success
**URL:** `http://localhost:8083/api/schedule/work-schedules/[UUID-HORARIO]`
**Método:** DELETE
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

### 19. Obtener Horario Laboral por ID (Requiere ROLE_PROVEEDOR)

**Nombre:** Get Work Schedule by ID - Success
**URL:** `http://localhost:8083/api/schedule/work-schedules/[UUID-HORARIO]`
**Método:** GET
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 200 OK
**Response esperado:** (mismo formato que creación)

### 20. Listar Horarios Laborales de Empleado (Requiere ROLE_PROVEEDOR)

**Nombre:** Get Work Schedules by Employee - Success
**URL:** `http://localhost:8083/api/schedule/work-schedules/employee/[UUID-EMPLEADO]`
**Método:** GET
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_PROVEEDOR]
```
**Código esperado:** 200 OK
**Response esperado:** (array de horarios)

### 21. Listar Horarios Activos de Empleado (Público)

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

### 22. Crear Bloqueo de Horario (Requiere ROLE_PROVEEDOR)

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

### 23. Crear Bloqueo de Horario - Fuera de Horario Laboral (400 Bad Request)

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

### 24. Crear Bloqueo para Reserva (Interno)

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

### 25. Cancelar Bloqueo de Reserva (Interno)

**Nombre:** Cancel Reservation Block - Success
**URL:** `http://localhost:8083/api/schedule/schedule-blocks/reservation/[UUID-RESERVA]`
**Método:** DELETE
**Headers:**
```
Authorization: Bearer [JWT_TOKEN_MICROSERVICIO_RESERVAS]
```
**Código esperado:** 204 No Content
**Response esperado:** (vacío)

### 26. Verificar Disponibilidad de Empleado

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

### 27. Listar Bloqueos por Rango de Fechas

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

## Asociación Empleado-Servicio (Employee Service Offerings)

### 28. Crear Asociación Empleado-Servicio (Requiere ROLE_PROVEEDOR)

**Nombre:** Create Employee-Service Association - Success
**URL:** `http://localhost:8083/api/schedule/employee-services`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
```
**Body:**
```json
{
    "employeeId": "[UUID-EMPLEADO]",
    "serviceId": "[UUID-SERVICIO]"
}
```
**Código esperado:** 201 Created
**Response esperado:**
```json
{
    "id": "[UUID-ASOCIACION]",
    "employeeId": "[UUID-EMPLEADO]",
    "serviceId": "[UUID-SERVICIO]",
    "active": true,
    "assignmentDate": "2026-04-28T12:00:00",
    "createdAt": "2026-04-28T12:00:00",
    "updatedAt": "2026-04-28T12:00:00"
}
```
**Nota:** Crea una asociación entre un empleado y un servicio. El proveedor debe ser dueño de ambos.

---

### 29. Crear Asociación - Empleado No Existe (Error)

**Nombre:** Create Employee-Service Association - Employee Not Found
**URL:** `http://localhost:8083/api/schedule/employee-services`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
```
**Body:**
```json
{
    "employeeId": "00000000-0000-0000-0000-000000000000",
    "serviceId": "[UUID-SERVICIO]"
}
```
**Código esperado:** 404 Not Found
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Empleado no encontrado con id: 550e8400-e29b-41d4-a716-446655440000",
    "path": "/api/schedule/employee-services"
}
```

---

### 30. Crear Asociación - Asociación Ya Existe (Error)

**Nombre:** Create Employee-Service Association - Already Exists
**URL:** `http://localhost:8083/api/schedule/employee-services`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
```
**Body:**
```json
{
    "employeeId": "[UUID-EMPLEADO-EXISTENTE]",
    "serviceId": "[UUID-SERVICIO-EXISTENTE]"
}
```
**Código esperado:** 409 Conflict
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 409,
    "error": "Conflict",
    "message": "Ya existe una asociación activa entre el empleado [UUID-EMPLEADO] y el servicio [UUID-SERVICIO]",
    "path": "/api/schedule/employee-services"
}
```

---

### 31. Desactivar Asociación Empleado-Servicio (Requiere ROLE_PROVEEDOR)

**Nombre:** Deactivate Employee-Service Association - Success
**URL:** `http://localhost:8083/api/schedule/employee-services/[UUID-ASOCIACION]/deactivate`
**Método:** PATCH
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
```
**Código esperado:** 204 No Content
**Nota:** Desactiva (soft delete) una asociación existente. Solo el proveedor dueño puede desactivar.

---

### 32. Desactivar Asociación - Ya Inactiva (Error)

**Nombre:** Deactivate Employee-Service Association - Already Inactive
**URL:** `http://localhost:8083/api/schedule/employee-services/[UUID-ASOCIACION-INACTIVA]/deactivate`
**Método:** PATCH
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
```
**Código esperado:** 409 Conflict
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 409,
    "error": "Conflict",
    "message": "La asociación empleado-servicio con ID [UUID-ASOCIACION] ya está inactiva",
    "path": "/api/schedule/employee-services/[UUID-ASOCIACION-INACTIVA]/deactivate"
}
```

---

### 33. Activar Asociación Empleado-Servicio (Requiere ROLE_PROVEEDOR)

**Nombre:** Activate Employee-Service Association - Success
**URL:** `http://localhost:8083/api/schedule/employee-services/[UUID-ASOCIACION]/activate`
**Método:** PATCH
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
```
**Código esperado:** 204 No Content
**Nota:** Activa una asociación previamente desactivada. Solo el proveedor dueño puede activar.

---

### 34. Activar Asociación - Ya Activa (Error)

**Nombre:** Activate Employee-Service Association - Already Active
**URL:** `http://localhost:8083/api/schedule/employee-services/[UUID-ASOCIACION-ACTIVA]/activate`
**Método:** PATCH
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
```
**Código esperado:** 409 Conflict
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 409,
    "error": "Conflict",
    "message": "La asociación empleado-servicio con ID [UUID-ASOCIACION] ya está activa",
    "path": "/api/schedule/employee-services/[UUID-ASOCIACION-ACTIVA]/activate"
}
```

---

### 35. Eliminar Asociación Empleado-Servicio (Hard Delete)

**Nombre:** Delete Employee-Service Association - Success
**URL:** `http://localhost:8083/api/schedule/employee-services/[UUID-ASOCIACION]`
**Método:** DELETE
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
```
**Código esperado:** 204 No Content
**Nota:** Elimina permanentemente una asociación. Solo el proveedor dueño puede eliminar.

---

### 36. Eliminar Asociación - No Existe (Error)

**Nombre:** Delete Employee-Service Association - Not Found
**URL:** `http://localhost:8083/api/schedule/employee-services/550e8400-e29b-41d4-a716-446655440000`
**Método:** DELETE
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
```
**Código esperado:** 404 Not Found
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Asociación empleado-servicio no encontrada con ID: 550e8400-e29b-41d4-a716-446655440000",
    "path": "/api/schedule/employee-services/550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 37. Listar Empleados por Servicio (Requiere ROLE_PROVEEDOR)

**Nombre:** Get Employees by Service - Success
**URL:** `http://localhost:8083/api/schedule/employee-services/service/[UUID-SERVICIO]`
**Método:** GET
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
```
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "idServicio": "[UUID-SERVICIO]",
    "idProveedor": "[UUID-PROVEEDOR]",
    "nombreServicio": "Corte de Cabello",
    "duracionMinutos": 60,
    "precio": 30.00,
    "descripcion": "Corte de cabello para hombre",
    "activo": true,
    "capacidadMaxima": 1,
    "employees": [
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
    ],
    "createdAt": "2026-04-28T12:00:00"
}
```
**Nota:** Retorna el servicio con todos los empleados asociados. Solo el proveedor dueño del servicio puede acceder.

---

### 38. Listar Servicios por Empleado (Requiere ROLE_PROVEEDOR)

**Nombre:** Get Services by Employee - Success
**URL:** `http://localhost:8083/api/schedule/employee-services/employee/[UUID-EMPLEADO]`
**Método:** GET
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN]
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
    "notes": "Senior stylist",
    "services": [
        {
            "idServicio": "[UUID-SERVICIO-1]",
            "idProveedor": "[UUID-PROVEEDOR]",
            "nombreServicio": "Corte de Cabello",
            "duracionMinutos": 60,
            "precio": 30.00,
            "descripcion": "Corte de cabello para hombre",
            "activo": true,
            "capacidadMaxima": 1,
            "createdAt": "2026-04-28T12:00:00"
        },
        {
            "idServicio": "[UUID-SERVICIO-2]",
            "idProveedor": "[UUID-PROVEEDOR]",
            "nombreServicio": "Afeitado de Barba",
            "duracionMinutos": 30,
            "precio": 15.00,
            "descripcion": "Afeitado tradicional con navaja",
            "activo": true,
            "capacidadMaxima": 1,
            "createdAt": "2026-04-28T12:00:00"
        }
    ],
    "createdAt": "2026-04-28T12:00:00",
    "updatedAt": "2026-04-28T12:00:00"
}
```
**Nota:** Retorna el empleado con todos los servicios activos asociados. Solo el proveedor dueño del empleado puede acceder.

---

### 39. Listar Empleados Activos por Servicio (Público)

**Nombre:** Get Active Employees by Service - Public
**URL:** `http://localhost:8083/api/schedule/employee-services/service/[UUID-SERVICIO]/active`
**Método:** GET
**Headers:**
```
Content-Type: application/json
```
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "idServicio": "[UUID-SERVICIO]",
    "idProveedor": "[UUID-PROVEEDOR]",
    "nombreServicio": "Corte de Cabello",
    "duracionMinutos": 60,
    "precio": 30.00,
    "descripcion": "Corte de cabello para hombre",
    "activo": true,
    "capacidadMaxima": 1,
    "employees": [
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
    ],
    "createdAt": "2026-04-28T12:00:00"
}
```
**Nota:** Retorna solo los empleados activos asociados al servicio. Endpoint público para que los clientes puedan ver disponibilidad.

---

### 40. Listar Empleados Activos por Servicio - Servicio No Encontrado (Error)

**Nombre:** Get Active Employees by Service - Service Not Found
**URL:** `http://localhost:8083/api/schedule/employee-services/service/550e8400-e29b-41d4-a716-446655440000/active`
**Método:** GET
**Headers:**
```
Content-Type: application/json
```
**Código esperado:** 404 Not Found
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Servicio no encontrado en el catálogo con ID: 550e8400-e29b-41d4-a716-446655440000",
    "path": "/api/schedule/employee-services/service/550e8400-e29b-41d4-a716-446655440000/active"
}
```

---

### 41. Crear Asociación - Sin Permisos (Error)

**Nombre:** Create Employee-Service Association - Access Denied
**URL:** `http://localhost:8083/api/schedule/employee-services`
**Método:** POST
**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN-PROVEEDOR-2]  // Token de otro proveedor
```
**Body:**
```json
{
    "employeeId": "[UUID-EMPLEADO-PROVEEDOR-1]",
    "serviceId": "[UUID-SERVICIO-PROVEEDOR-1]"
}
```
**Código esperado:** 403 Forbidden
**Response esperado:**
```json
{
    "timestamp": "2026-04-28T12:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "No tienes permisos para gestionar esta asociación empleado-servicio",
    "path": "/api/schedule/employee-services"
}
```
**Nota:** Un proveedor no puede crear asociaciones con empleados o servicios de otro proveedor.

---

## Health Check

### 42. Health Check

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

### 43. Version Check

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

### Códigos de Error

- **400 Bad Request**: Datos inválidos, fuera de horario laboral, fecha pasada
- **401 Unauthorized**: No autenticado
- **403 Forbidden**: No tiene rol PROVEEDOR o no es dueño del empleado/servicio
- **404 Not Found**: Recurso no encontrado
- **409 Conflict**: Superposición de horarios, bloqueos, o asociaciones duplicadas
- **500 Internal Server Error**: Error interno del servidor

### Variables de URL

En las pruebas, reemplaza las siguientes variables:
- `[UUID-EMPLEADO]` - ID del empleado (UUID)
- `[UUID-EMPLEADO-2]` - ID de otro empleado
- `[UUID-EMPLEADO-INACTIVO]` - ID de empleado inactivo
- `[UUID-PROVEEDOR]` - ID del proveedor (del JWT)
- `[UUID-HORARIO]` - ID del horario laboral
- `[UUID-BLOQUEO]` - ID del bloqueo de horario
- `[UUID-RESERVA]` - ID de la reserva
- `[UUID-SERVICIO]` - ID del servicio del catálogo
- `[UUID-ASOCIACION]` - ID de la asociación empleado-servicio
- `[JWT_TOKEN_PROVEEDOR]` - Token JWT del proveedor

