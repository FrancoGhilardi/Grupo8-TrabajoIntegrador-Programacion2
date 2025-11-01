-- 01_creacion_base.sql
-- TPI Programación II – Empleado → Legajo (1→1 unidireccional)
-- Motor: MySQL 8+

DROP DATABASE IF EXISTS tpi_p2;
CREATE DATABASE tpi_p2 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE tpi_p2;

-- Tabla Legajo (B)
CREATE TABLE Legajo (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  eliminado TINYINT(1) NOT NULL DEFAULT 0,
  nroLegajo VARCHAR(20) NOT NULL,
  categoria VARCHAR(30) NULL,
  estado ENUM('ACTIVO','INACTIVO') NOT NULL,
  fechaAlta DATE NOT NULL,
  observaciones VARCHAR(255) NULL,
  CONSTRAINT uq_legajo_nro UNIQUE (nroLegajo),
  CONSTRAINT ck_legajo_eliminado CHECK (eliminado IN (0,1))
);

-- Tabla Empleado (A)
CREATE TABLE Empleado (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  eliminado TINYINT(1) NOT NULL DEFAULT 0,
  nombre VARCHAR(80) NOT NULL,
  apellido VARCHAR(80) NOT NULL,
  dni VARCHAR(15) NOT NULL,
  email VARCHAR(120) NULL,
  fechaIngreso DATE NOT NULL,
  area VARCHAR(50) NULL,
  legajo BIGINT NOT NULL,
  CONSTRAINT uq_empleado_dni UNIQUE (dni),
  CONSTRAINT uq_empleado_legajo UNIQUE (legajo),
  CONSTRAINT fk_empleado_legajo FOREIGN KEY (legajo)
    REFERENCES Legajo(id)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT,
  CONSTRAINT ck_empleado_eliminado CHECK (eliminado IN (0,1))
);

-- Índices auxiliares
CREATE INDEX idx_empleado_apellido ON Empleado(apellido);
CREATE INDEX idx_empleado_area ON Empleado(area);
CREATE INDEX idx_legajo_estado ON Legajo(estado);
CREATE INDEX idx_legajo_categoria ON Legajo(categoria);