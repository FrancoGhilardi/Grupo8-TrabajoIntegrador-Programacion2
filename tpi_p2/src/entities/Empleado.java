package entities;

import java.time.LocalDate;

/**
 * Entidad que representa a un Empleado en el sistema.
 * 
 * Implementa el lado "A" de una relación 1→1 unidireccional con Legajo.
 * El Empleado contiene la referencia al Legajo asociado.
 * 
 * Esta clase modela la información personal y laboral básica de un empleado,
 * incluyendo datos de contacto, área de trabajo y fecha de ingreso.
 * 
 * @author Grupo 8
 * @version 1.0
 */
public class Empleado extends BaseEntity {
    
    /** Nombre de la tabla en la base de datos */
    public static final String TABLE = "Empleado";
    
    /**
     * Clase interna que define los nombres de las columnas en la BD.
     * Facilita el mapeo objeto-relacional y evita errores por strings literales.
     */
    public static final class Cols {
        public static final String ID = "id";
        public static final String ELIMINADO = "eliminado";
        public static final String NOMBRE = "nombre";
        public static final String APELLIDO = "apellido";
        public static final String DNI = "dni";
        public static final String EMAIL = "email";
        public static final String FECHA_INGRESO = "fechaIngreso";
        public static final String AREA = "area";
        /** Foreign Key hacia la tabla Legajo */
        public static final String LEGAJO_ID = "legajo";
    }

    /** Nombre del empleado */
    private String nombre;
    
    /** Apellido del empleado */
    private String apellido;
    
    /** Documento Nacional de Identidad (único por empleado) */
    private String dni;
    
    /** Correo electrónico corporativo del empleado */
    private String email;
    
    /** Fecha en la que el empleado ingresó a la empresa */
    private LocalDate fechaIngreso;
    
    /** Área o departamento al que pertenece el empleado */
    private String area;
    
    /** 
     * Referencia al Legajo del empleado.
     * Relación 1→1 unidireccional: un Empleado tiene un Legajo.
     */
    private Legajo legajo;

    /**
     * Constructor por defecto.
     * Necesario para la creación de instancias mediante reflexión (ej: frameworks ORM).
     */
    public Empleado() { }

    /**
     * Constructor completo con todos los atributos.
     * 
     * @param id identificador único del empleado
     * @param eliminado flag de eliminación lógica
     * @param nombre nombre del empleado
     * @param apellido apellido del empleado
     * @param dni documento de identidad
     * @param email correo electrónico
     * @param fechaIngreso fecha de ingreso a la empresa
     * @param area área o departamento
     * @param legajo legajo asociado al empleado
     */
    public Empleado(Long id, Boolean eliminado, String nombre, String apellido, String dni, String email, LocalDate fechaIngreso, String area, Legajo legajo) {
        setId(id);
        setEliminado(eliminado);
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.fechaIngreso = fechaIngreso;
        this.area = area;
        this.legajo = legajo;
    }

    /**
     * Obtiene el nombre del empleado.
     * @return el nombre
     */
    public String getNombre() { return nombre; }
    
    /**
     * Establece el nombre del empleado.
     * @param nombre el nombre a asignar
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene el apellido del empleado.
     * @return el apellido
     */
    public String getApellido() { return apellido; }
    
    /**
     * Establece el apellido del empleado.
     * @param apellido el apellido a asignar
     */
    public void setApellido(String apellido) { this.apellido = apellido; }

    /**
     * Obtiene el DNI del empleado.
     * @return el documento de identidad
     */
    public String getDni() { return dni; }
    
    /**
     * Establece el DNI del empleado.
     * @param dni el documento a asignar
     */
    public void setDni(String dni) { this.dni = dni; }

    /**
     * Obtiene el email del empleado.
     * @return el correo electrónico
     */
    public String getEmail() { return email; }
    
    /**
     * Establece el email del empleado.
     * @param email el correo electrónico a asignar
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Obtiene la fecha de ingreso del empleado.
     * @return la fecha de ingreso
     */
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    
    /**
     * Establece la fecha de ingreso del empleado.
     * @param fechaIngreso la fecha a asignar
     */
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    /**
     * Obtiene el área del empleado.
     * @return el área o departamento
     */
    public String getArea() { return area; }
    
    /**
     * Establece el área del empleado.
     * @param area el área o departamento a asignar
     */
    public void setArea(String area) { this.area = area; }

    /**
     * Obtiene el legajo asociado al empleado.
     * @return el legajo del empleado
     */
    public Legajo getLegajo() { return legajo; }
    
    /**
     * Establece el legajo del empleado.
     * @param legajo el legajo a asignar
     */
    public void setLegajo(Legajo legajo) { this.legajo = legajo; }

    /**
     * Genera una representación en String del empleado.
     * Incluye todos los atributos para facilitar la depuración.
     * 
     * @return representación textual del empleado
     */
    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + getId() +
                ", eliminado=" + getEliminado() +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                ", area='" + area + '\'' +
                ", legajo=" + (legajo != null ? legajo.getId() : null) +
                '}';
    }
}