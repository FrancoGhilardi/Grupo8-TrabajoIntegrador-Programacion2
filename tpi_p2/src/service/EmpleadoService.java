package service;

import entities.Empleado;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de alto nivel para {@link Empleado}.
 *
 * <p>Extiende el CRUD genérico y agrega consultas/validaciones
 * de negocio habituales del dominio (unicidad por DNI, búsquedas, etc.).</p>
 *
 * <h3>Responsabilidades de la implementación</h3>
 * <ul>
 *   <li>Orquestar transacciones (begin/commit/rollback) usando la clase base.</li>
 *   <li>Invocar DAOs correspondientes y mapear excepciones técnicas a {@link ServiceException}.</li>
 *   <li>Aplicar validaciones previas:
 *     <ul>
 *       <li><b>DNI</b>: no nulo/ vacío; formato válido (solo dígitos); unicidad.</li>
 *       <li><b>Nombre/Apellido</b>: no nulos/ vacíos; longitud razonable.</li>
 *       <li><b>Email</b> (si existe): formato sintáctico válido.</li>
 *       <li><b>Fechas</b> (si existen): no futuras para nacimiento/ingreso.</li>
 *     </ul>
 *   </li>
 *   <li>Eliminar: preferir borrado lógico si el modelo lo contempla; impedir si viola integridad referencial.</li>
 * </ul>
 */
public interface EmpleadoService extends GenericService<Empleado> {

    /**
     * Obtiene un empleado por su DNI.
     *
     * @param dni documento (solo dígitos)
     * @return {@link Optional} con el empleado si existe y está activo
     * @throws ServiceException VALIDATION si el DNI es inválido
     */
    Optional<Empleado> getByDni(String dni);

    /**
     * Indica si existe un empleado activo con el DNI dado.
     *
     * @param dni documento (solo dígitos)
     * @return true si existe, false en caso contrario
     * @throws ServiceException VALIDATION si el DNI es inválido
     */
    boolean existsByDni(String dni);

    /**
     * Búsqueda simple por apellido (LIKE).
     *
     * @param apellidoLike patrón a buscar (se aplicará sanitización/trim)
     * @return lista de coincidencias (puede ser vacía)
     */
    List<Empleado> searchByApellido(String apellidoLike);
}
