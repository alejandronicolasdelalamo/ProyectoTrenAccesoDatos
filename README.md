# Tren — CRUD con Spring Boot y MySQL

Aplicación web para gestionar una flota de trenes (crear, listar, editar y eliminar), hecha con
**Java 21, Spring Boot, Thymeleaf y MySQL**. Incluye un generador de datos aleatorios y un
**registro (log) de transacciones** en archivo de texto.

Este README explica cómo está hecho cada apartado y qué hemos aprendido por el camino, para poder
contárselo a la clase.

---

## Resumen del proyecto

- **Qué es:** una aplicación web CRUD para gestionar una flota de trenes, hecha en grupo.
- **Cómo está hecha:** arquitectura por capas (controller → service → repository) con Spring Boot,
  Thymeleaf y MySQL.
- **Extras:** generador de 20.000 trenes aleatorios con Datafaker y log de transacciones en
  archivo de texto, consultable en JSON desde `/api/logs`.
- **Dónde verla:** `http://localhost:8081/trenes` una vez arrancada.

---

## 1. Qué hace la aplicación

| Acción | Ruta | Método |
|---|---|---|
| Listar trenes (paginado, 10 por página) | `/trenes` | GET |
| Formulario de alta | `/trenes/nuevo` | GET |
| Formulario de edición | `/trenes/editar/{id}` | GET |
| Guardar (crear o editar) | `/trenes/guardar` | POST |
| Eliminar | `/trenes/eliminar/{id}` | GET |
| Ver el log de transacciones (JSON) | `/api/logs` | GET |

La app corre en **http://localhost:8081/trenes**.

## 2. Tecnologías

- Java 21 y Spring Boot (Web MVC, Data JPA, Validation, Thymeleaf)
- MySQL 8
- Maven con wrapper (`mvnw`), no hace falta instalar Maven
- Datafaker para generar datos aleatorios

## 3. Estructura y arquitectura por capas

```
src/main/java/com/example/tren/
├── model/        Tren (entidad JPA), Propulsion, TipoServicio, OperacionLog, LogTransaccion
├── repository/   TrenRepository (Spring Data JPA)
├── service/      TrenService, LogService (interfaces) + service/impl con la lógica
├── controller/   TrenController (vistas HTML), LogRestController (API JSON), HomeController
├── exception/    Excepción "tren no encontrado" + manejador global
└── GeneradorTrenesSql.java   Generador de datos aleatorios (se ejecuta aparte)

src/main/resources/
├── application.properties            Configuración general
├── application-local.properties      Credenciales locales (NO se sube a git)
├── schema.sql / data.sql             Tabla y datos iniciales
└── templates/                        Vistas Thymeleaf (listar, formulario, layout)
```

Idea clave: cada capa tiene **una sola responsabilidad**.
El controlador recibe la petición, el servicio contiene la lógica, el repositorio habla con la
base de datos.

## 4. Cómo arrancarlo

### 4.1 Tener MySQL funcionando

Instala MySQL 8 y comprueba que el servicio está en marcha (en Windows aparece como `MySQL80`).

> **Problema que tuvimos:** al escribir `mysql` en la terminal salía *"no se reconoce como un
> comando"*. MySQL sí estaba instalado y el servicio corriendo. Lo que pasaba es que la carpeta
> `C:\Program Files\MySQL\MySQL Server 8.0\bin` **no estaba en el PATH** de Windows, así que la
> terminal no sabía dónde buscar `mysql.exe`.
>
> **Soluciones:** usar la ruta completa
> (`"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p`)
> o añadir esa carpeta `bin` al PATH (Variables de entorno → Path → Nuevo) y abrir una terminal
> nueva.

### 4.2 Crear la base de datos y un usuario propio

No conviene que la app use `root`. Entra como root y crea un usuario con permisos solo sobre
la base de datos del proyecto:

```sql
CREATE DATABASE IF NOT EXISTS tren_db;
CREATE USER 'tren_app'@'localhost' IDENTIFIED BY 'TU_PASSWORD';
GRANT ALL PRIVILEGES ON tren_db.* TO 'tren_app'@'localhost';
FLUSH PRIVILEGES;
```

> **Problema que tuvimos:** la app no arrancaba con
> `Access denied for user 'tren_app'@'localhost'`. MySQL funcionaba y la app llegaba a
> conectar, pero ese usuario no existía (o su contraseña no coincidía con la del archivo de
> configuración). Se arregla creando el usuario como arriba, con la misma contraseña que pongas
> en `application-local.properties`.

### 4.3 Configurar las credenciales sin subirlas a git

Copia la plantilla y pon tu usuario y contraseña:

```
copy src\main\resources\application-local.properties.example src\main\resources\application-local.properties
```

```properties
spring.datasource.username=tren_app
spring.datasource.password=TU_PASSWORD
```

`application-local.properties` está en `.gitignore`, así que **nunca se sube al repositorio**.
`application.properties` activa el perfil `local` (`spring.profiles.active=local`), y sus valores
pisan a los de por defecto (`root`/`root`).

### 4.4 Ejecutar

```
mvnw.cmd spring-boot:run        (Windows)
./mvnw spring-boot:run          (macOS / Linux)
```

El primer arranque tarda unos 40 segundos porque carga 20.000 trenes (ver siguiente apartado).

## 5. ¿Quién carga los datos: el servidor o el cliente?

**El servidor.** El navegador no carga nada en la base de datos. Hay dos momentos:

**Al arrancar la app:**
- `application.properties` tiene `spring.sql.init.mode=always`.
- Eso hace que Spring Boot ejecute `data.sql` en **cada arranque**.
- `data.sql` empieza con `DELETE FROM trenes;` y sigue con unos 20.000 `INSERT`. Por eso los
  cambios que hagas a mano se pierden al reiniciar. Para evitarlo, pon
  `spring.sql.init.mode=never` una vez cargados los datos.

**Al abrir la página:**
- `TrenController.listar(...)` pide una página de trenes al servicio y la mete en el `Model`
  (`model.addAttribute("trenes", ...)`).
- La plantilla `listar.html` la recorre con `th:each="tren : ${trenes}"`.
- Thymeleaf genera el HTML **ya completo en el servidor**; el navegador solo lo pinta.

**Cómo comprobarlo:**
1. Abre `/trenes` y pulsa **Ctrl+U** (ver código fuente): verás las filas `<tr>` con los datos ya
   escritos y ningún `th:each`.
2. En **F12 → Network** solo hay una petición HTML, sin `fetch` ni XHR.
3. En la consola de la app, `spring.jpa.show-sql=true` imprime el `select ... from trenes` cada
   vez que recargas la página.

La única parte pensada para que la consuma un cliente es `/api/logs`, que devuelve JSON.

## 6. Generador de datos aleatorios

Clase: `src/main/java/com/example/tren/GeneradorTrenesSql.java`

- Tiene su propio `main`: se ejecuta aparte de la app (en VS Code, botón *Run* sobre el `main`).
- Usa la librería **Datafaker** para generar **20.000 trenes** (`cantidadAGenerar = 20000`).
- Escribe el resultado en `src/main/resources/data.sql`.
- Los datos tienen cierta lógica: los trenes anteriores a 1900 son más lentos (30–85 km/h) que
  los modernos (80–330 km/h), y las matrículas son correlativas (`TR-00001`, `TR-00002`...).
- Cuidado: **sobrescribe `data.sql`** con datos nuevos cada vez que se ejecuta.

## 7. Log de transacciones

Cada vez que se crea, actualiza o elimina un tren se guarda una línea en
`logs/transacciones.log` (la ruta se configura con `app.log.ruta`).

Formato de cada línea:

```
fecha-hora | OPERACION | Entidad | id | detalle
```

Cómo está hecho (`LogServiceImpl`):
- `TrenServiceImpl` llama a `logService.registrar(...)` en cada operación.
- La línea se escribe **solo después de que la transacción de base de datos se confirme**
  (`afterCommit`). Así, si la operación falla y se deshace, no queda un log falso.
- `/api/logs` lee el archivo y devuelve los registros en JSON, del más reciente al más antiguo.
- Primero se guardaba en una tabla de la base de datos; después lo cambiamos a un archivo de texto.
  La carpeta `logs/` está en `.gitignore`.

## 8. Qué hemos aprendido

- **Capas en Spring Boot:** controller → service → repository, y por qué separar
  responsabilidades.
- **Renderizado en servidor:** Thymeleaf genera el HTML en el servidor; el navegador no consulta
  la base de datos.
- **Seguridad básica de credenciales:** perfil `local`, archivo fuera de git y usuario de base de
  datos con permisos mínimos en vez de `root`.
- **Inicialización de datos:** `schema.sql` y `data.sql` se ejecutan al arrancar, y por eso
  los datos se reinician en cada arranque.
- **Transacciones:** registrar el log en `afterCommit` evita guardar operaciones que luego se
  revierten.
- **Diagnosticar errores leyendo la causa real:** `mysql` "no funcionaba" por el PATH, y la app
  "no arrancaba" por un usuario inexistente. Eran dos problemas distintos y se resuelven por
  separado.
- **Generar datos de prueba** de forma reproducible con una clase Java y Datafaker.
