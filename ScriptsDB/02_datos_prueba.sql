-- 02_datos_prueba.sql
USE tpi_p2;

-- Legajos
INSERT INTO Legajo (eliminado, nroLegajo, categoria, estado, fechaAlta, observaciones) VALUES
(0, 'L-1001', 'Administrativo', 'ACTIVO', '2023-05-10', NULL),
(0, 'L-1002', 'IT',            'ACTIVO', '2023-06-15', 'Ingreso con notebook'),
(0, 'L-1003', 'Finanzas',      'INACTIVO','2022-03-01', 'Baja temporal'),
(0, 'L-1004', 'RRHH',          'ACTIVO', '2021-11-20', NULL),
(0, 'L-1005', 'Ventas',        'ACTIVO', '2020-01-15', 'Cuenta comisiones');

-- Empleados (relación 1:1 con Legajo)
INSERT INTO Empleado (eliminado, nombre, apellido, dni, email, fechaIngreso, area, legajo) VALUES
(0, 'María',  'Gómez',   '40123456', 'maria.gomez@empresa.com', '2023-05-12', 'RRHH',     1),
(0, 'Juan',   'Pérez',   '38111222', 'juan.perez@empresa.com',  '2023-06-20', 'IT',       2),
(0, 'Lucía',  'López',   '37123456', 'lucia.lopez@empresa.com', '2022-03-05', 'Finanzas', 3),
(0, 'Diego',  'Suárez',  '35123456', 'diego.suarez@empresa.com','2021-11-22', 'RRHH',     4),
(0, 'Ana',    'Martínez','33123456', 'ana.martinez@empresa.com','2020-01-20', 'Ventas',   5);

-- Consultas de verificación
-- 1) Cardinalidad 1:1
SELECT COUNT(*) AS total_legajos FROM Legajo;
SELECT COUNT(*) AS total_empleados FROM Empleado;
SELECT e.id, e.apellido, l.nroLegajo FROM Empleado e INNER JOIN Legajo l ON e.legajo = l.id LIMIT 5;
-- Revisar los counts y el JOIN (deberían ser 5 y 5).

-- 2) Reglas de unicidad (probar y esperar error)
-- INSERT INTO Empleado (eliminado, nombre, apellido, dni, email, fechaIngreso, area, legajo)
-- VALUES (0, 'Carlos','Pérez','40123456','carlos@empresa.com','2023-07-01','IT', 2); -- dni duplicadoS