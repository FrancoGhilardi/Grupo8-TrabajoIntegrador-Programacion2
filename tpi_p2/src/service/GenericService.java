package service;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de la capa de servicios.
 *
 * <p>Expone operaciones CRUD de alto nivel desacopladas de JDBC/SQL.
 * La implementación concreta es responsable de:
 * <ul>
 *   <li>Orquestar transacciones (begin/commit/rollback)</li>
 *   <li>Invocar a los DAO correspondientes</li>
 *   <li>Aplicar validaciones de negocio y de integridad</li>
 *   <li>Mapear y envolver excepciones de infraestructura</li>
 * </ul>
 * </p>
 *
 * @param <T> tipo de la entidad de dominio
 */
public interface GenericService<T> {

    /**
     * Inserta una nueva entidad aplicando validaciones de negocio.
     *
     * @param entidad entidad a persistir
     * @return entidad persistida
     * @throws ServiceException cuando la validación falla o ocurre un error de infraestructura
     */
    T insertar(T entidad);

    /**
     * Actualiza una entidad existente.
     *
     * @param entidad entidad con cambios a persistir
     * @return entidad actualizada
     * @throws ServiceException cuando la validación falla o ocurre un error de infraestructura
     */
    T actualizar(T entidad);

    /**
     * Marca lógicamente como eliminada una entidad por su identificador.
     *
     * @param id identificador de la entidad
     * @throws ServiceException cuando no existe, ya está eliminada o falla la operación
     */
    void eliminar(long id);

    /**
     * Obtiene una entidad por su identificador.
     *
     * @param id identificador de la entidad
     * @return entidad en un {@link Optional}, vacío si no existe o está eliminada
     * @throws ServiceException cuando ocurre un error de infraestructura
     */
    Optional<T> getById(long id);

    /**
     * Lista todas las entidades activas.
     *
     * @return listado de entidades
     * @throws ServiceException cuando ocurre un error de infraestructura
     */
    List<T> getAll();
}
