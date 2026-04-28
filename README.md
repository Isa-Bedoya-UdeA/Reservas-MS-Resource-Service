# Plataforma de Reservas de Servicios - MS-Schedule-Service

[![CI/CD Pipeline](https://github.com/Isa-Bedoya-UdeA/Reservas-MS-Schedule-Service/actions/workflows/build.yml/badge.svg)](https://github.com/Isa-Bedoya-UdeA/Reservas-MS-Schedule-Service/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Isa-Bedoya-UdeA_Reservas-MS-Schedule-Service)

## Descripción

CodeF@ctory - Caso 15 - Plataforma de Reservas de Servicios - Microservicio de Horarios y Empleados.

## Responsabilidad

Gestión de empleados, asignación de servicios a empleados, y disponibilidad de horarios para reservas.

## Tecnologías

### Backend

* **Java 17**
* **Spring Boot 3.5.13**
* **Spring Security** (Autenticación y autorización)
* **Spring Data JPA** (Persistencia)
* **JWT** (JSON Web Tokens para autenticación)
* **MapStruct** (Mapeo entre entidades y DTOs)
* **Lombok** (Reducción de código boilerplate)
* **Maven** (Gestión de dependencias)

### Herramientas de Desarrollo

* **Git** (Control de versiones)
* **GitHub** (Repositorio remoto)
* **Postman** (Pruebas de APIs)
* **SonarCloud** (Análisis de calidad de código)

## Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

* **JDK 17** o superior
* **Maven 3.8+**
* **Oracle Database** o **PostgreSQL**
* **Git**

## Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Isa-Bedoya-UdeA/Reservas-MS-Schedule-Service
cd Reservas-MS-Schedule-Service
```

### 2. Configurar la Base de Datos y Propiedades

Copia el archivo `.env.example` a `.env`:

```bash
cp .env.example .env
```

Edita el archivo `.env` con tus credenciales de Supabase:

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

### 3. Configurar JWT

Genera un JWT_SECRET seguro:

```bash
openssl rand -base64 64
```

Agrega el JWT_SECRET generado a tu archivo `.env`:

```bash
JWT_SECRET=[TU-JWT-SECRET-SEGURA]
JWT_EXPIRATION=86400000
```

> **IMPORTANTE:** El JWT_SECRET debe ser el mismo en todos los microservicios.

### 4. Compilar el Proyecto

```bash
mvn clean install -U -DskipTests 
```

### 5. Ejecutar la Aplicación

```bash
# Opción 1:
mvn spring-boot:run

# Opción 2:
mvn clean spring-boot:run

# Si falla al ejecutar, lanzar este comando, y luego nuevamente run
taskkill /F /IM java.exe 2>$null
```

La aplicación estará disponible en: `http://localhost:8083`

## Estructura del Proyecto

```
Reservas-MS-Schedule-Service/
├── src/
│   ├── main/
│   │   ├── java/com/codefactory/reservasmsresourceservice/
│   │   │   ├── client/              # Feign Clients para comunicación entre microservicios
│   │   │   ├── config/              # Configuración de Spring (Security, JWT, CORS, etc.)
│   │   │   ├── controller/          # Controladores REST (Employee, Health)
│   │   │   ├── dto/                 # Data Transfer Objects (Request y Response)
│   │   │   ├── entity/              # Entidades JPA (Employee, EmployeeServiceOffering, Availability)
│   │   │   ├── exception/           # Excepciones personalizadas y manejo global
│   │   │   ├── mapper/              # Mapeadores (MapStruct) entre entidades y DTOs
│   │   │   ├── repository/          # Repositorios Spring Data JPA
│   │   │   ├── security/            # Seguridad (JWT filter, JWT service)
│   │   │   ├── service/             # Interfaces de servicios
│   │   │   └── service/impl/        # Implementaciones de servicios
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── application-test.properties
│   └── test/
│       ├── java/                    # Tests unitarios y de integración
│       └── resources/
├── docs/                            # Documentación y pruebas
├── .env.example                     # Plantilla de variables de entorno
├── .env                             # Variables de entorno (no versionado)
├── pom.xml                          # Configuración de Maven
└── README.md
```

## Endpoints Principales

### Health Check
- `GET /api/`: Health Check - Retorna estado del servicio
- `GET /api/version`: Version Check - Retorna versión del servicio

### Empleados
- `POST /api/schedule/employees`: Crear empleado (requiere ROLE_PROVEEDOR) - El providerId se obtiene del JWT
- `PUT /api/schedule/employees/{id}`: Actualizar empleado existente (requiere ROLE_PROVEEDOR) - Solo el proveedor creador puede modificar
- `DELETE /api/schedule/employees/{id}`: Desactivar empleado (soft delete) (requiere ROLE_PROVEEDOR) - Solo el proveedor creador puede desactivar
- `GET /api/schedule/employees/{id}`: Obtener empleado por ID (requiere ROLE_PROVEEDOR) - Solo el proveedor dueño puede ver
- `GET /api/schedule/employees`: Listar todos los empleados del proveedor autenticado (requiere ROLE_PROVEEDOR)
- `GET /api/schedule/employees/active`: Listar solo empleados activos del proveedor autenticado (requiere ROLE_PROVEEDOR)

## Relaciones entre Entidades

- **Employee**: Entidad que representa empleados (recurso humano) asociados a proveedores
- **EmployeeServiceOffering**: Entidad que representa la relación N:N entre empleados y servicios (un empleado puede ofrecer múltiples servicios)
- **Availability**: Entidad que representa los horarios de disponibilidad de los empleados para reservas

## Documentación de API (Swagger/OpenAPI)

**Ruta de acceso:** http://localhost:8083/swagger-ui/index.html#/

## Pruebas en Postman

Para ver las pruebas detalladas de la API, consulta el archivo [docs/PruebasPostman.md](docs/PruebasPostman.md).