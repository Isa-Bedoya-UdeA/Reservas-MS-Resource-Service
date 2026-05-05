# Variables de Entorno - Reservas-MS-Schedule-Service

Este documento describe todas las variables de entorno necesarias para el despliegue del microservicio de Horarios y Disponibilidad.

## Archivo de Configuración

Copia el archivo `.env.example` a `.env` y configura los valores:

```bash
cp .env.example .env
```

## Variables Requeridas

### 1. Perfil de Spring

| Variable | Descripción | Valor por Defecto | Opciones |
|----------|-------------|-------------------|----------|
| `SPRING_PROFILE` | Perfil activo de Spring Boot | `dev` | `dev`, `test`, `prod` |

### 2. Configuración de Base de Datos (Supabase)

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_URL` | URL JDBC de PostgreSQL (Transaction Pooler IPv4) | `jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0` |
| `DB_USER` | Usuario de la base de datos | `postgres.[PROJECT-REF]` |
| `DB_PASSWORD` | Contraseña de Supabase | `[TU-CONTRASEÑA]` |

**Nota:** Se recomienda usar el Transaction Pooler de Supabase (puerto 6543) para compatibilidad con IPv4.

### 3. Configuración JWT

| Variable | Descripción | Recomendación |
|----------|-------------|---------------|
| `JWT_SECRET` | Secreto para firmar/validar tokens JWT | Generar con: `openssl rand -base64 64` |
| `JWT_EXPIRATION` | Tiempo de expiración del token en milisegundos | `86400000` (24 horas) |

⚠️ **IMPORTANTE:** El `JWT_SECRET` debe ser el **MISMO VALOR** en todos los microservicios para que la validación de tokens funcione correctamente.

### 4. URL del Frontend

| Variable | Descripción | Valor Local | Valor Producción |
|----------|-------------|-------------|------------------|
| `FRONTEND_URL` | URL base para enlaces | `http://localhost:3000` | `https://tu-dominio.com` |

### 5. URLs de Servicios Externos (Dependencias)

| Variable | Descripción | Puerto Local | Producción |
|----------|-------------|--------------|------------|
| `SERVICES_AUTH_URL` | URL del Auth Service para validación JWT | `http://localhost:8081` | `https://ms-auth-service.onrender.com` |
| `SERVICES_CATALOG_URL` | URL del Catalog Service para validación de servicios | `http://localhost:8082` | `https://ms-catalog-service.onrender.com` |
| `SERVICES_SCHEDULE_URL` | URL de este mismo servicio (para referencias) | `http://localhost:8083` | `https://ms-schedule-service.onrender.com` |

## Ejemplo Completo (.env)

```bash
# ======================
# SPRING PROFILE
# ======================
SPRING_PROFILE=dev

# ======================
# DATABASE CONFIG
# ======================
DB_URL=jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
DB_USER=postgres.[TU-PROJECT-REF]
DB_PASSWORD=[TU-CONTRASEÑA-DE-SUPABASE]

# ======================
# JWT CONFIG (MISMO EN TODOS LOS MS)
# ======================
JWT_SECRET=[TU-JWT-SECRET-SEGURA]
JWT_EXPIRATION=86400000

# ======================
# EXTERNAL SERVICES URLs
# ======================
SERVICES_AUTH_URL=http://localhost:8081
SERVICES_CATALOG_URL=http://localhost:8082
SERVICES_SCHEDULE_URL=http://localhost:8083

# ======================
# FRONTEND URL
# ======================
FRONTEND_URL=http://localhost:3000
```

## Despliegue en Producción (Render)

Cuando despliegues en Render:

1. Configura todas las variables en el dashboard de Render
2. Actualiza las URLs de servicios externos a las URLs de producción
3. Asegúrate de que el `JWT_SECRET` sea idéntico en todos los microservicios

## Verificación

Para verificar la configuración:

```bash
# Ver el perfil activo
cat .env | grep SPRING_PROFILE

# Iniciar el servicio
mvn spring-boot:run
```

En los logs verás la validación de las variables configuradas.

## Notas Específicas del Schedule Service

- Este microservicio **depende de dos servicios externos**:
  - **Auth Service**: Para validar tokens JWT
  - **Catalog Service**: Para validar que los servicios existen y pertenecen al proveedor
- Sin estas dependencias funcionando, las operaciones de asociación empleado-servicio fallarán
- El microservicio usa **OpenFeign** para comunicarse con el Catalog Service

## Arquitectura de Comunicación

```
┌─────────────────┐         ┌──────────────────┐
│  Schedule MS    │ ──────> │   Auth MS        │
│  (este)         │  JWT    │   (validación)   │
└─────────────────┘         └──────────────────┘
         │
         │ OpenFeign
         ▼
┌──────────────────┐
│   Catalog MS     │
│ (validación de  │
│  servicios)      │
└──────────────────┘
```
