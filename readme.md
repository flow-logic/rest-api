# REST API con Spring Boot: proyecto de referencia

Proyecto de referencia para el curso de **programación backend con Java**. Es una API REST pequeña pero completa: expone un recurso (`Student`), lo guarda en una base de datos MySQL, protege los endpoints con autenticación y roles, se documenta con Swagger y se consume desde un frontend HTML sencillo.

Úsalo como modelo para estructurar tus propios proyectos y como guía de cómo se conectan las piezas de una aplicación Spring Boot.

## Qué vas a encontrar en este proyecto

| Concepto | Dónde verlo |
|---|---|
| Controlador REST (`@RestController`, `@GetMapping`, `@PostMapping`, `@RequestBody`) | `controller/RestStudentController.java` |
| Inyección de dependencias por constructor | `RestStudentController`, `StudentDAOImpl` |
| Patrón DAO (interfaz + implementación) | `dao/StudentDAO.java`, `dao/StudentDAOImpl.java` |
| Persistencia con JPA (`@Entity`, `EntityManager`, JPQL) | `entity/Student.java`, `StudentDAOImpl` |
| Autenticación HTTP Basic y autorización por roles | `security/SecurityConfig.java` |
| Usuarios y roles almacenados en base de datos (`JdbcUserDetailsManager`) | `security/SecurityConfig.java` |
| CORS para permitir un frontend en otro origen | `security/SecurityConfig.java` |
| Documentación automática de la API (OpenAPI / Swagger UI) | `security/OpenApiConfig.java` |
| Tests unitarios con Mockito | `src/test/.../StudentDAOImplIT.java` |
| Cliente web que consume la API con `fetch` | `frontend/index.html` |

## Tecnologías

- Java 21
- Spring Boot 4.1.1 (Spring Web MVC, Spring Data JPA, Spring Security)
- MySQL
- springdoc-openapi 3.1.1 (Swagger UI)
- Maven (incluido mediante Maven Wrapper, no necesitas instalarlo)

## Estructura del repositorio

```
rest-api/
├── backend/                         # API REST (proyecto Maven)
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd              # Maven Wrapper (Linux/macOS y Windows)
│   └── src/
│       ├── main/
│       │   ├── java/com/example/rest_api/
│       │   │   ├── RestApiApplication.java      # Punto de entrada
│       │   │   ├── controller/                  # Capa web (endpoints)
│       │   │   ├── dao/                         # Capa de acceso a datos
│       │   │   ├── entity/                      # Entidades JPA
│       │   │   └── security/                    # Seguridad, CORS y OpenAPI
│       │   └── resources/application.properties # Configuración y conexión a BD
│       └── test/                                # Tests
└── frontend/
    └── index.html                   # Cliente web de ejemplo
```

El flujo de una petición es: **Cliente → Controller → DAO → Base de datos**, y la respuesta vuelve por el mismo camino. Antes de llegar al controlador, la petición pasa por el filtro de seguridad de Spring Security.

## Requisitos previos

Comprueba que tienes instalado:

- **JDK 21** o superior. Verifica con `java -version`.
- **MySQL 8** o superior, en ejecución en el puerto `3306`. Puedes usar una instalación local, XAMPP, MySQL Workbench o Docker.
- **Git**.
- Un IDE con soporte para Java y Maven (IntelliJ IDEA, Eclipse, NetBeans, VS Code con el *Extension Pack for Java*).
- Conexión a internet la primera vez: el Maven Wrapper descarga Maven y las dependencias.

## Cómo ejecutar la aplicación en local

### 1. Clonar el repositorio

```bash
git clone https://github.com/flow-logic/rest-api.git
cd rest-api
```

### 2. Crear la base de datos

El proyecto **no crea las tablas automáticamente**, así que hay que prepararlas antes de arrancar. Conéctate a MySQL (por ejemplo, con MySQL Workbench o `mysql -u root -p`) y ejecuta este script:

```sql
CREATE DATABASE IF NOT EXISTS universidad
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE universidad;

-- Recurso de negocio: estudiantes
CREATE TABLE student (
  id         INT NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(45) DEFAULT NULL,
  last_name  VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- Tablas que Spring Security espera por defecto (JdbcUserDetailsManager)
CREATE TABLE users (
  username VARCHAR(50)  NOT NULL,
  password VARCHAR(500) NOT NULL,
  enabled  TINYINT(1)   NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE authorities (
  username  VARCHAR(50) NOT NULL,
  authority VARCHAR(50) NOT NULL,
  UNIQUE KEY ix_auth_username (username, authority),
  CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username)
);

-- Usuarios de ejemplo ({noop} = contraseña en texto plano, solo para aprendizaje)
INSERT INTO users (username, password, enabled) VALUES
  ('profesor', '{noop}profesor123', 1),
  ('director', '{noop}director123', 1);

-- Roles: en Spring Security los roles se guardan con el prefijo ROLE_
INSERT INTO authorities (username, authority) VALUES
  ('profesor', 'ROLE_PROFESOR'),
  ('director', 'ROLE_PROFESOR'),
  ('director', 'ROLE_DIRECTOR');

-- Datos de ejemplo
INSERT INTO student (first_name, last_name) VALUES
  ('Ana', 'García'),
  ('Luis', 'Martínez');
```

> **Por qué `director` tiene dos roles:** en este proyecto los roles no se heredan entre sí. Las reglas de `SecurityConfig` exigen `PROFESOR` para consultar y crear estudiantes, y `DIRECTOR` para modificar y borrar. Para que el director también pueda consultar, necesita ambos roles.

### 3. Configurar la conexión a la base de datos

Abre `backend/src/main/resources/application.properties` y ajusta los valores si tu MySQL usa otro usuario, contraseña o puerto:

```properties
spring.application.name=rest-api
spring.datasource.url=jdbc:mysql://localhost:3306/universidad
spring.datasource.username=root
spring.datasource.password=root
```

> Estas credenciales son solo para desarrollo local. En un proyecto real nunca se suben contraseñas al repositorio: se usan variables de entorno o perfiles de Spring.

### 4. Arrancar la API

Desde la carpeta `backend`:

**Linux / macOS**

```bash
cd backend
./mvnw spring-boot:run
```

**Windows (PowerShell o CMD)**

```bash
cd backend
mvnw.cmd spring-boot:run
```

Si prefieres tu IDE, abre la carpeta `backend` como proyecto Maven y ejecuta la clase `RestApiApplication`.

Sabrás que ha arrancado correctamente cuando veas en la consola una línea parecida a:

```
Started RestApiApplication in X.XXX seconds
```

La API queda disponible en **http://localhost:8080**.

### 5. Probar la API

#### Opción A: Swagger UI (recomendada para empezar)

Abre en el navegador:

**http://localhost:8080/swagger-ui/index.html**

Pulsa el botón **Authorize**, introduce `profesor` / `profesor123` y prueba los endpoints con **Try it out**.

#### Opción B: curl

Listar estudiantes:

```bash
curl -u profesor:profesor123 http://localhost:8080/api/students
```

Crear un estudiante:

```bash
curl -u profesor:profesor123 \
  -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"firstName": "Marta", "lastName": "Soler"}'
```

> En Windows PowerShell, usa `curl.exe` en lugar de `curl` y escribe el comando en una sola línea.

Prueba también qué ocurre sin credenciales (`401 Unauthorized`) o con un usuario que no tiene el rol necesario (`403 Forbidden`). Entender la diferencia entre ambos códigos es parte de lo que se practica en este proyecto.

#### Opción C: el frontend de ejemplo

Con la API en marcha, sirve la carpeta `frontend` con cualquier servidor estático. Por ejemplo, con Python:

```bash
cd frontend
python3 -m http.server 5500
```

Abre **http://localhost:5500**, introduce las credenciales en el diálogo de inicio de sesión y verás el listado de estudiantes. Si usas VS Code, la extensión *Live Server* también sirve (usa el puerto 5500 por defecto).

## Endpoints

Todos los endpoints cuelgan de `/api`.

| Método | Ruta | Descripción | Rol requerido |
|---|---|---|---|
| `GET` | `/api/students` | Devuelve la lista de estudiantes | `PROFESOR` |
| `POST` | `/api/students` | Crea un estudiante y lo devuelve con su `id` generado | `PROFESOR` |

Modelo de datos (`Student`):

```json
{
  "id": 1,
  "firstName": "Ana",
  "lastName": "García"
}
```

Al crear un estudiante no envíes `id`: lo genera la base de datos.

**Nota para practicar:** `SecurityConfig` ya declara reglas para `PUT /api/students` y `DELETE /api/students/**` (rol `DIRECTOR`), pero el controlador todavía no implementa esos endpoints. Son un buen ejercicio de ampliación (ver más abajo).

## Ejecutar los tests

```bash
cd backend

# Test unitario del DAO con Mockito (no necesita base de datos)
./mvnw test -Dtest=StudentDAOImplIT

# Todos los tests por defecto (RestApiApplicationTests levanta el contexto
# de Spring completo, así que MySQL debe estar en marcha y la BD creada)
./mvnw test
```

## Problemas frecuentes

| Síntoma | Causa probable y solución |
|---|---|
| `Communications link failure` al arrancar | MySQL no está en ejecución o no escucha en el puerto 3306. Arráncalo y revisa `spring.datasource.url`. |
| `Access denied for user 'root'` | Usuario o contraseña incorrectos en `application.properties`. |
| `Table 'universidad.student' doesn't exist` (o `users`) | No se ha ejecutado el script SQL del paso 2. |
| `release version 21 not supported` o `Unsupported class file major version` | Tu JDK es anterior a la 21. Instálala y comprueba con `java -version` (y con `echo $JAVA_HOME`). |
| `Permission denied: ./mvnw` (Linux/macOS) | Ejecuta `chmod +x mvnw` y vuelve a intentarlo. |
| `Port 8080 was already in use` | Otro programa usa el puerto. Ciérralo o añade `server.port=8081` a `application.properties` (y actualiza la URL en `frontend/index.html`). |
| `401 Unauthorized` | Faltan credenciales o son incorrectas. |
| `403 Forbidden` | El usuario existe pero su rol no permite esa operación. Revisa la tabla `authorities`. |
| Error de CORS en el navegador | Sirve el frontend desde `http://localhost` (no abriendo el archivo con `file://`) y comprueba que la API está levantada. |

## Ejercicios propuestos

Una vez que lo tengas funcionando, intenta ampliar el proyecto:

1. Añadir `GET /api/students/{id}` que devuelva un estudiante o un `404` si no existe.
2. Implementar `PUT /api/students` y `DELETE /api/students/{id}` (protegidos por el rol `DIRECTOR`, como ya indica `SecurityConfig`).
3. Añadir validaciones (`@Valid`, `@NotBlank`) a `Student` y devolver errores claros con un `@ControllerAdvice`.
4. Introducir una capa de servicio (`StudentService`) entre el controlador y el DAO.
5. Sustituir las contraseñas `{noop}` por `{bcrypt}` y comprobar que el login sigue funcionando.
6. Mover la configuración de la base de datos a variables de entorno.
7. Reescribir el DAO con Spring Data JPA (`JpaRepository`) y comparar cuánto código desaparece.

## Licencia y uso

Proyecto con fines educativos. Úsalo, modifícalo y rómpelo libremente para aprender.