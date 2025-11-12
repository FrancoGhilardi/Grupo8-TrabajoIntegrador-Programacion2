package service;

import entities.Legajo;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de alto nivel para {@link Legajo}.
 *
 * <p>Expone operaciones CRUD y consultas de negocio típicas
 * (búsqueda por número de legajo, existencia, filtros simples).</p>
 *
 * <h3>Responsabilidades de la implementación</h3>
 * <ul>
 *   <li>Orquestar transacciones con begin/commit/rollback.</li>
 *   <li>Invocar los DAOs correspondientes y encapsular errores técnicos en {@link ServiceException}.</li>
 *   <li>Validar reglas de negocio mínimas:
 *     <ul>
 *       <li><b>Número de legajo</b>: requerido, no vacío, patrón válido y <b>único</b>.</li>
 *       <li><b>Fecha de alta</b>: no futura.</li>
 *       <li><b>Estados</b> (si aplica): dentro de dominio permitido.</li>
 *       <li><b>Borrado</b>: preferir borrado lógico si el modelo lo contempla.</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public interface LegajoService extends GenericService<Legajo> {

    /**
     * Obtiene un legajo por su número único.
     *
     * @param numero valor de número de legajo
     * @return {@link Optional} con la entidad si existe y está activa
     * @throws ServiceException VALIDATION si el formato de número es inválido
     */
    Optional<Legajo> getByNumero(String numero);

    /**
     * Indica si existe un legajo activo con ese número.
     *
     * @param numero valor de número de legajo
     * @return true si existe, false en caso contrario
     * @throws ServiceException VALIDATION si el formato de número es inválido
     */
    boolean existsByNumero(String numero);

    /**
     * Búsqueda sencilla por estado
     * Se permite patrón flexible y sanitizado (trim/ignore-case).
     *
     * @param estadoLike patrón a buscar
     * @return lista de coincidencias (posiblemente vacía)
     */
    List<Legajo> searchByEstado(String estadoLike);
}
