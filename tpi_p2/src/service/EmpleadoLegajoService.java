package service;

import entities.Empleado;
import entities.Legajo;

/**
 * Caso de uso compuesto para Alta de {@link Empleado} junto a su {@link Legajo} en una sola transacción.
 *
 * <p>Objetivo: exponer una operación de negocio de alto nivel que evite
 * que la UI/menú tenga que coordinar múltiples llamadas y rollback manuales.</p>
 *
 * <h3>Garantías</h3>
 * <ul>
 *   <li>La operación es <b>atómica</b>: si falla cualquier paso (validación, unicidad, I/O),
 *       la transacción se revierte y no queda nada a medio crear.</li>
 *   <li>Se validan reglas de negocio mínimas:
 *     <ul>
 *       <li><b>DNI</b> del empleado: requerido, numérico (7–11), único.</li>
 *       <li><b>Número de legajo</b>: requerido, patrón válido y único.</li>
 *       <li><b>Relación 1↔1</b>: el empleado queda asociado al legajo recién creado.</li>
 *     </ul>
 *   </li>
 *   <li>Se normalizan strings (trim) y se chequean fechas no futuras cuando aplique.</li>
 * </ul>
 *
 * <h3>Errores</h3>
 * <ul>
 *   <li>Lanza {@link ServiceException} con tipo:
 *     <ul>
 *       <li>{@link ServiceException.Type#VALIDATION} si fallan las reglas.</li>
 *       <li>{@link ServiceException.Type#CONFLICT} si hay duplicados (DNI / nroLegajo).</li>
 *       <li>{@link ServiceException.Type#INFRASTRUCTURE} si hay fallos técnicos (JDBC, I/O).</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public interface EmpleadoLegajoService {

    /**
     * Crea un {@link Legajo} y un {@link Empleado} asociado en una única transacción.
     *
     * <p>Si ocurre un error, se realiza <b>rollback</b> completo.</p>
     *
     * @param empleado datos del empleado a crear (nombre, apellido, dni, email opcional, etc.)
     * @param legajo   datos del legajo a crear (nroLegajo, estado/categoría/fechaAlta…)
     * @return el {@link Empleado} persistido con su {@link Legajo} asociado (incluye IDs asignados)
     * @throws ServiceException ante validaciones incumplidas o errores de infraestructura
     */
    Empleado altaEmpleadoConLegajo(Empleado empleado, Legajo legajo);
}
