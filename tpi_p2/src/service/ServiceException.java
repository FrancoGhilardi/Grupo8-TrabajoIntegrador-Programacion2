package service;

/**
 * Excepción unchecked para la capa de servicios.
 *
 * <p>Unifica el manejo de errores de negocio y de infraestructura, evitando
 * propagar detalles de JDBC/SQL hacia capas superiores. Incluye una {@link Type}
 * para clasificar rápidamente el origen del problema (validación, no encontrado,
 * conflicto, infraestructura).</p>
 *
 * <p>Uso recomendado:
 * <ul>
 *   <li>Lanzar {@code ServiceException.validation(...)} ante reglas de negocio incumplidas.</li>
 *   <li>Lanzar {@code ServiceException.notFound(...)} cuando la entidad no exista.</li>
 *   <li>Lanzar {@code ServiceException.conflict(...)} ante estados inconsistentes o duplicados.</li>
 *   <li>Envolver excepciones técnicas con {@code ServiceException.infrastructure(..., cause)}.</li>
 * </ul>
 * </p>
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Tipo/clasificación de la excepción para un manejo consistente.
     */
    public enum Type {
        /** Violación de reglas de negocio o datos inválidos provistos por el usuario. */
        VALIDATION,
        /** Recurso/entidad no encontrado. */
        NOT_FOUND,
        /** Conflicto de estado (duplicados, referencias no permitidas, etc.). */
        CONFLICT,
        /** Error técnico/infraestructura (JDBC, I/O, config, etc.). */
        INFRASTRUCTURE
    }

    private final Type type;

    /**
     * Crea una excepción con tipo y mensaje.
     *
     * @param type    clasificación del error
     * @param message detalle legible para el operador/usuario técnico
     */
    public ServiceException(Type type, String message) {
        super(message);
        this.type = type;
    }

    /**
     * Crea una excepción con tipo, mensaje y causa.
     *
     * @param type    clasificación del error
     * @param message detalle legible para el operador/usuario técnico
     * @param cause   causa original
     */
    public ServiceException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    /**
     * Retorna la clasificación del error para tomar decisiones de manejo.
     *
     * @return tipo de la excepción
     */
    public Type getType() {
        return type;
    }

    // -----------------------
    // Fábricas estáticas
    // -----------------------

    /**
     * Construye una excepción de validación.
     *
     * @param message mensaje descriptivo
     * @return excepción de tipo VALIDATION
     */
    public static ServiceException validation(String message) {
        return new ServiceException(Type.VALIDATION, message);
    }

    /**
     * Construye una excepción de "no encontrado".
     *
     * @param message mensaje descriptivo
     * @return excepción de tipo NOT_FOUND
     */
    public static ServiceException notFound(String message) {
        return new ServiceException(Type.NOT_FOUND, message);
    }

    /**
     * Construye una excepción de conflicto.
     *
     * @param message mensaje descriptivo del conflicto detectado
     * @return excepción de tipo CONFLICT
     */
    public static ServiceException conflict(String message) {
        return new ServiceException(Type.CONFLICT, message);
    }

    /**
     * Construye una excepción de infraestructura envolviendo la causa.
     *
     * @param message mensaje descriptivo
     * @param cause   causa original
     * @return excepción de tipo INFRASTRUCTURE
     */
    public static ServiceException infrastructure(String message, Throwable cause) {
        return new ServiceException(Type.INFRASTRUCTURE, message, cause);
    }
}
