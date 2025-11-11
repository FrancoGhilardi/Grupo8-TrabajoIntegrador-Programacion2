package dao;

import entities.Empleado;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Interfaz específica para operaciones de acceso a datos (DAO)
 * relacionadas con la entidad {@link Empleado}.
 *
 * <p>
 * Extiende de {@link GenericDao} para heredar las operaciones CRUD básicas
 * y agrega consultas particulares del dominio del empleado, como la búsqueda
 * por DNI.
 * </p>
 *
 * <p>
 * Las implementaciones concretas (por ejemplo,
 * {@code EmpleadoDaoJdbcImpl}) deben utilizar JDBC con
 * {@link java.sql.PreparedStatement} para ejecutar todas las operaciones,
 * garantizando seguridad frente a inyección SQL.
 * </p>
 *
 * @author Grupo 8
 * @version 1.0
 */
public interface EmpleadoDao extends GenericDao<Empleado> {

    /**
     * Busca un empleado en la base de datos según su número de documento (DNI),
     * utilizando una conexión interna gestionada por el método.
     *
     * @param dni número de documento único del empleado.
     * @return un {@link Optional} que contiene al empleado si existe,
     * o vacío si no se encontró.
     * @throws SQLException si ocurre un error durante la consulta SQL.
     */
    Optional<Empleado> buscarPorDni(String dni) throws SQLException;

    /**
     * Busca un empleado por su DNI utilizando una conexión externa.
     * <p>
     * Este método es útil para ejecutar la búsqueda dentro de una transacción
     * compartida con otras operaciones (por ejemplo, validaciones antes de crear
     * un nuevo registro).
     * </p>
     *
     * @param dni  número de documento único del empleado.
     * @param conn conexión JDBC activa compartida.
     * @return un {@link Optional} con el empleado si se encuentra.
     * @throws SQLException si ocurre un error durante la operación SQL.
     */
    Optional<Empleado> buscarPorDni(String dni, Connection conn) throws SQLException;
}
