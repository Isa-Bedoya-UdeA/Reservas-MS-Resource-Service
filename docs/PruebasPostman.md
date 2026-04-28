# Pruebas de API - MS-Schedule-Service

Este documento contiene las pruebas de API para el microservicio de Horarios y Disponibilidad.

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

---

## Health Check

### 12. Health Check

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

---

### 13. Version Check

**Nombre:** Version Check - Success
**URL:** `http://localhost:8083/api/version`
**Método:** GET
**Código esperado:** 200 OK
**Response esperado:**
```json
{
    "version": "1.0.0-SNAPSHOT",
    "service": "Reservas-MS-Schedule-Service"
}
```

---

## Notas Importantes

### Obtención de Tokens JWT

Para obtener los tokens JWT necesarios para las pruebas que requieren autenticación:

1. **Token de Proveedor:**
   - Registra un usuario con rol PROVEEDOR en el Auth Service
   - Inicia sesión: `POST http://localhost:8081/api/auth/login`
   - Usa el `accessToken` retornado

### IDs de Prueba

Para las pruebas que requieren IDs específicos, puedes obtenerlos de:
- **Empleados:** Ejecuta primero la prueba #10 (Get Employees by Provider) y usa los IDs retornados
- **Proveedores:** Registra un proveedor en el Auth Service y usa el ID retornado

### Orden de Ejecución Recomendado

Para una ejecución ordenada de las pruebas:
1. Primero ejecuta las pruebas de Health Check (12-13)
2. Luego ejecuta las pruebas de creación de empleados (1-3)
3. Luego ejecuta las pruebas de actualización/desactivación (4-7)
4. Finalmente ejecuta las pruebas de listado (8-11)

### Precondiciones

Antes de ejecutar estas pruebas, asegúrate de:
1. Tener el Auth Service corriendo en `http://localhost:8081`
2. Tener el Schedule Service corriendo en `http://localhost:8083`
3. Tener usuarios registrados en el Auth Service con el rol PROVEEDOR
4. Tener la base de datos configurada y accesible

### Documentación Swagger

La documentación interactiva de la API está disponible en:
- **Swagger UI:** `http://localhost:8083/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8083/v3/api-docs`
