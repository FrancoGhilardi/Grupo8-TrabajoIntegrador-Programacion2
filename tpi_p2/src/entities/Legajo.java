package entities;

import java.time.LocalDate;

/**
 * Entidad que representa el Legajo de un empleado.
 * 
 * Implementa el lado "B" de una relación 1→1 unidireccional con Empleado.
 * El Legajo no conoce al Empleado que lo referencia, cumpliendo el patrón
 * de navegabilidad unidireccional.
 * 
 * Esta clase modela la información administrativa del empleado, incluyendo
 * su número de legajo, categoría laboral, estado y observaciones.
 * 
 * @author Grupo 8
 * @version 1.0
 */
public class Legajo extends BaseEntity {
    
    /** Nombre de la tabla en la base de datos */
    public static final String TABLE = "Legajo";
    
    /**
     * Clase interna que define los nombres de las columnas en la BD.
     * Facilita el mapeo objeto-relacional y evita errores por strings literales.
     */
    public static final class Cols {
        public static final String ID = "id";
        public static final String ELIMINADO = "eliminado";
        public static final String NRO_LEGAJO = "nroLegajo";
        public static final String CATEGORIA = "categoria";
        public static final String ESTADO = "estado";
        public static final String FECHA_ALTA = "fechaAlta";
        public static final String OBSERVACIONES = "observaciones";
    }

    /** Número de legajo único que identifica administrativamente al empleado */
    private String nroLegajo;
    
    /** Categoría o nivel laboral del empleado (ej: Junior, Senior, Manager) */
    private String categoria;
    
    /** Estado actual del legajo (ACTIVO/INACTIVO) */
    private EstadoLegajo estado;
    
    /** Fecha en la que se dio de alta el legajo en el sistema */
    private LocalDate fechaAlta;
    
    /** Observaciones o notas adicionales sobre el legajo */
    private String observaciones;

    /**
     * Constructor por defecto.
     * Necesario para la creación de instancias mediante reflexión (ej: frameworks ORM).
     */
    public Legajo() { }

    /**
     * Constructor completo con todos los atributos.
     * 
     * @param id identificador único del legajo
     * @param eliminado flag de eliminación lógica
     * @param nroLegajo número de legajo
     * @param categoria categoría laboral
     * @param estado estado del legajo (ACTIVO/INACTIVO)
     * @param fechaAlta fecha de alta del legajo
     * @param observaciones observaciones adicionales
     */
    public Legajo(Long id, Boolean eliminado, String nroLegajo, String categoria, EstadoLegajo estado, LocalDate fechaAlta, String observaciones) {
        setId(id);
        setEliminado(eliminado);
        this.nroLegajo = nroLegajo;
        this.categoria = categoria;
        this.estado = estado;
        this.fechaAlta = fechaAlta;
        this.observaciones = observaciones;
    }

    /**
     * Obtiene el número de legajo.
     * @return el número de legajo
     */
    public String getNroLegajo() { return nroLegajo; }
    
    /**
     * Establece el número de legajo.
     * @param nroLegajo el número de legajo a asignar
     */
    public void setNroLegajo(String nroLegajo) { this.nroLegajo = nroLegajo; }

    /**
     * Obtiene la categoría laboral.
     * @return la categoría
     */
    public String getCategoria() { return categoria; }
    
    /**
     * Establece la categoría laboral.
     * @param categoria la categoría a asignar
     */
    public void setCategoria(String categoria) { this.categoria = categoria; }

    /**
     * Obtiene el estado del legajo.
     * @return el estado (ACTIVO/INACTIVO)
     */
    public EstadoLegajo getEstado() { return estado; }
    
    /**
     * Establece el estado del legajo.
     * @param estado el estado a asignar
     */
    public void setEstado(EstadoLegajo estado) { this.estado = estado; }

    /**
     * Obtiene la fecha de alta del legajo.
     * @return la fecha de alta
     */
    public LocalDate getFechaAlta() { return fechaAlta; }
    
    /**
     * Establece la fecha de alta del legajo.
     * @param fechaAlta la fecha a asignar
     */
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    /**
     * Obtiene las observaciones del legajo.
     * @return las observaciones
     */
    public String getObservaciones() { return observaciones; }
    
    /**
     * Establece las observaciones del legajo.
     * @param observaciones las observaciones a asignar
     */
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    /**
     * Genera una representación en String del legajo.
     * Incluye todos los atributos para facilitar la depuración.
     * 
     * @return representación textual del legajo
     */
    @Override
    public String toString() {
        return "Legajo{" +
                "id=" + getId() +
                ", eliminado=" + getEliminado() +
                ", nroLegajo='" + nroLegajo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", estado=" + estado +
                ", fechaAlta=" + fechaAlta +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}