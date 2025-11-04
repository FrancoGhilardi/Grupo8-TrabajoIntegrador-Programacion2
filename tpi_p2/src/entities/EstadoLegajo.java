package entities;

/**
 * Enumeración que define los estados posibles de un Legajo.
 * 
 * El uso de un enum garantiza type-safety y evita valores inválidos,
 * restringiendo los estados únicamente a los valores permitidos por el sistema.
 * 
 * Estados disponibles:
 * - ACTIVO: El legajo está en estado activo y operativo
 * - INACTIVO: El legajo está temporalmente inactivo (ej: licencia, suspensión)
 * 
 * @author Grupo 8
 * @version 1.0
 */
public enum EstadoLegajo {
    /** 
     * Estado activo: el legajo está operativo y el empleado puede realizar
     * sus actividades normalmente
     */
    ACTIVO,
    
    /** 
     * Estado inactivo: el legajo está temporalmente deshabilitado
     * (ej: licencia médica, suspensión temporal)
     */
    INACTIVO
}
