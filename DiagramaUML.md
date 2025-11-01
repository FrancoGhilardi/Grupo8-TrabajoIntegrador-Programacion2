---
title: TPI Programación II – Empleado → Legajo (1→1 unidireccional)
---
classDiagram
    direction LR

    class Empleado {
        - Long id
        - Boolean eliminado
        - String nombre
        - String apellido
        - String dni
        - String email
        - LocalDate fechaIngreso
        - String area
        - Legajo legajo
        + Empleado()
        + Empleado(Long id, Boolean eliminado, String nombre, String apellido, String dni, String email, LocalDate fechaIngreso, String area, Legajo legajo)
        + Long getId()
        + void setId(Long id)
        + Boolean getEliminado()
        + void setEliminado(Boolean eliminado)
        + String getNombre()
        + void setNombre(String nombre)
        + String getApellido()
        + void setApellido(String apellido)
        + String getDni()
        + void setDni(String dni)
        + String getEmail()
        + void setEmail(String email)
        + LocalDate getFechaIngreso()
        + void setFechaIngreso(LocalDate fecha)
        + String getArea()
        + void setArea(String area)
        + Legajo getLegajo()
        + void setLegajo(Legajo legajo)
        + String toString()
    }

    class Legajo {
        - Long id
        - Boolean eliminado
        - String nroLegajo
        - String categoria
        - EstadoLegajo estado
        - LocalDate fechaAlta
        - String observaciones
        + Legajo()
        + Legajo(Long id, Boolean eliminado, String nroLegajo, String categoria, EstadoLegajo estado, LocalDate fechaAlta, String observaciones)
        + Long getId()
        + void setId(Long id)
        + Boolean getEliminado()
        + void setEliminado(Boolean eliminado)
        + String getNroLegajo()
        + void setNroLegajo(String nro)
        + String getCategoria()
        + void setCategoria(String cat)
        + EstadoLegajo getEstado()
        + void setEstado(EstadoLegajo e)
        + LocalDate getFechaAlta()
        + void setFechaAlta(LocalDate f)
        + String getObservaciones()
        + void setObservaciones(String o)
        + String toString()
    }

    class EstadoLegajo {
        <<enumeration>>
        ACTIVO
        INACTIVO
    }

    Empleado --> "1" Legajo : «1→1 unidireccional»

    %% Paquetes (agrupación visual)
    class Config "Config (DB)" {
    }
    class DAO "DAO" {
    }
    class Service "Service" {
    }
    class Main "Main / AppMenu" {
    }

    %% Dependencias por capas (representativas)
    Empleado ..> DAO : usa
    Legajo ..> DAO : usa
    DAO ..> Config : Connection
    Service ..> DAO : orquesta transacciones
    Main ..> Service : invoca casos de uso