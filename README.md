# Grupo8-TrabajoIntegrador-Programacion2

Trabajo Integrador de Programación 2 - Relación 1:1 Unidireccional (Empleado → Legajo)

## Descripción del Proyecto

Este proyecto implementa una aplicación Java que gestiona empleados y sus legajos, utilizando una base de datos MySQL con una relación 1:1 unidireccional.

---

## 📋 Requisitos Previos

- **JDK 8 o superior**
- **MySQL 8.0+**
- **NetBeans IDE** (o cualquier IDE compatible con proyectos Java/Ant)
- **MySQL Connector/J** (driver JDBC para MySQL)

---

## 🗄️ Configuración de la Base de Datos

### 1. Iniciar MySQL
Asegúrate de que el servidor MySQL esté corriendo en el puerto **3306** (puerto estándar).

### 2. Ejecutar los Scripts SQL

Ejecuta los scripts en el siguiente orden desde tu cliente MySQL (MySQL Workbench, línea de comandos, etc.):

#### a) Crear la Base de Datos

```bash
mysql -u root -p < ScriptsDB/01_creacion_base.sql
```

Este script:

- Elimina la base de datos `tpi_p2` si existe
- Crea la base de datos `tpi_p2`
- Crea las tablas `Legajo` y `Empleado` con sus restricciones
- Configura índices para optimizar consultas

#### b) Insertar Datos de Prueba

```bash
mysql -u root -p < ScriptsDB/02_datos_prueba.sql
```

Este script inserta:

- 5 registros en la tabla `Legajo`
- 5 registros en la tabla `Empleado` (relación 1:1 con Legajo)

### 3. Verificar la Creación

Conéctate a MySQL y verifica:

```sql
USE tpi_p2;
SHOW TABLES;
SELECT COUNT(*) FROM Empleado;
SELECT COUNT(*) FROM Legajo;
```

Deberías ver 5 empleados y 5 legajos.

---

## ⚙️ Configuración del Proyecto Java

### 1. Credenciales de Base de Datos

Las credenciales están configuradas en `tpi_p2/src/config/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/tpi_p2";
private static final String USER = "root";
private static final String PASSWORD = "admin123";
```

**⚠️ IMPORTANTE:** Si tus credenciales de MySQL son diferentes, modifica estos valores:

- **Puerto:** Si tu MySQL corre en un puerto diferente, cambia `3306` por tu puerto
- **Usuario:** Cambia `"root"` por tu usuario de MySQL
- **Contraseña:** Cambia `"admin123"` por tu contraseña

### 2. Agregar el Driver MySQL (MySQL Connector/J)

El driver JDBC `mysql-connector-j-8.4.0.jar` ya está incluido en la raíz del proyecto.

Para agregarlo a tu proyecto en NetBeans:

1. Clic derecho en el proyecto → **Properties**
2. **Libraries** → **Add JAR/Folder**
3. Selecciona el archivo `mysql-connector-j-8.4.0.jar` de la raíz del proyecto

**Nota:** Si ya está agregado en las librerías del proyecto, puedes omitir este paso.

---

## 🚀 Ejecutar el Proyecto

### Opción 1: Desde NetBeans

1. Abre el proyecto `tpi_p2` en NetBeans
2. Clic derecho en el proyecto → **Clean and Build**
3. Ejecuta la clase `TestConexion.java` o `tpi_p2.java`:
    - Clic derecho en el archivo → **Run File**

### Opción 2: Desde la Línea de Comandos

#### a) Compilar el proyecto

```powershell
cd tpi_p2
ant clean
ant compile
```

#### b) Ejecutar la aplicación

```powershell
ant run
```

### Opción 3: Ejecutar el JAR generado

```powershell
cd tpi_p2/dist
java -jar tpi_p2.jar
```

---

## 🧪 Probar la Conexión

El proyecto incluye `TestConexion.java` que verifica la conexión a la base de datos:

```java
// Ejecutar TestConexion.java para validar la conexión
```

Si la conexión es exitosa, deberías ver un mensaje confirmando la conexión a la base de datos.

---

## 📂 Estructura del Proyecto

```
Grupo8-TrabajoIntegrador-Programacion2/
├── README.md
├── ScriptsDB/
│   ├── 01_creacion_base.sql      # Script de creación de BD
│   └── 02_datos_prueba.sql       # Script de datos de prueba
└── tpi_p2/
    ├── src/
    │   ├── config/
    │   │   └── DatabaseConnection.java      # Configuración de conexión JDBC
    │   ├── entities/
    │   │   ├── BaseEntity.java              # Clase base con id y eliminado
    │   │   ├── Empleado.java                # Entidad principal (A)
    │   │   ├── EstadoLegajo.java            # Enum ACTIVO / INACTIVO
    │   │   └── Legajo.java                  # Entidad relacionada (B)
    │   ├── dao/
    │   │   ├── GenericDao.java              # Interfaz genérica CRUD
    │   │   ├── EmpleadoDao.java             # DAO específico de Empleado
    │   │   └── LegajoDao.java               # DAO específico de Legajo
    │   ├── dao/impl/
    │   │   ├── EmpleadoDaoJdbcImpl.java     # Implementación JDBC de EmpleadoDao
    │   │   └── LegajoDaoJdbcImpl.java       # Implementación JDBC de LegajoDao
    │   └── tpi_p2/
    │       ├── TestConexion.java            # Test de conexión
    │       └── tpi_p2.java                  # Clase principal / menú (en desarrollo)
    └── build.xml                            # Configuración Ant
```

---

## 🧱 Capa DAO (Data Access Object)

La capa DAO encapsula todo el acceso a la base de datos utilizando JDBC y `PreparedStatement`.

- `GenericDao<T>`: define operaciones CRUD genéricas (`crear`, `leer`, `leerTodos`, `actualizar`, `eliminar`)
  y versiones con `Connection` externa para participar en transacciones.

- `EmpleadoDao` / `EmpleadoDaoJdbcImpl`:
  Implementa la persistencia de la entidad `Empleado`, con búsqueda por DNI
  y recuperación del `Legajo` asociado mediante LEFT JOIN.

- `LegajoDao` / `LegajoDaoJdbcImpl`:
  Implementa la persistencia de la entidad `Legajo`, con búsqueda por número de legajo.

Todos los métodos están implementados con manejo de excepciones `SQLException`,
baja lógica (`eliminado = 1`), y conexión gestionada por la clase `DatabaseConnection`.

---

## 🔧 Solución de Problemas

### Error: "No se encontró el driver JDBC"

- Verifica que el archivo `mysql-connector-j-8.4.0.jar` esté agregado a las librerías del proyecto en NetBeans

### Error: "Access denied for user"

- Verifica las credenciales en `DatabaseConnection.java`
- Asegúrate de que el usuario tenga permisos en la base de datos `tpi_p2`

### Error: "Can't connect to MySQL server on localhost:3306"

- Verifica que MySQL esté corriendo: `mysqladmin -u root -p ping`
- Verifica el puerto correcto en `DatabaseConnection.java`

### Error: "Unknown database 'tpi_p2'"

- Ejecuta primero el script `01_creacion_base.sql`

---

## 👥 Autores

**Alumnos - Grupo 8**: HERNAN JAVIER CASALDERREY (Comisión 13) · FRANCO JOEL GHILARDI ARMIJO (Comisión 14) · LAUTARO LANER (Comisión 3) · ZAMPIERI PAMELA (Comisión 18)

Tecnicatura Universitaria en Programación - Programación 2 - Universidad Tecnológica Nacional

---

## 📝 Licencia

Proyecto académico - UTN