package dao;

import entities.Legajo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Interfaz específica para el acceso a datos de la entidad {@link Legajo}.
 *
 * <p>
 * Extiende la interfaz {@link GenericDao} para heredar las operaciones CRUD
 * comunes y define métodos de búsqueda personalizados propios del dominio,
 * como la búsqueda por número de legajo.
 * </p>
 *
 * <p>
 * Las implementaciones concretas deben usar JDBC con
 * {@link java.sql.PreparedStatement} y manejar correctamente la conexión
 * (cerrarla solo cuando no se use una conexión externa).
 * </p>
 *
 * @author Grupo 8
 * @version 1.0
 */
public interface LegajoDao extends GenericDao<Legajo> {

    /**
     * Busca un legajo por su número único, utilizando una conexión interna.
     *
     * @param nroLegajo número identificatorio único del legajo.
     * @return un {@link Optional} con el legajo encontrado, o vacío si no existe.
     * @throws SQLException si ocurre un error de acceso a la base de datos.
     */
    Optional<Legajo> buscarPorNroLegajo(String nroLegajo) throws SQLException;

    /**
     * Busca un legajo por su número utilizando una conexión externa,
     * permitiendo integrarlo en una transacción compuesta.
     *
     * @param nroLegajo número identificatorio único del legajo.
     * @param conn      conexión JDBC activa compartida con otras operaciones.
     * @return un {@link Optional} con el legajo si existe.
     * @throws SQLException si ocurre un error durante la consulta SQL.
     */
    Optional<Legajo> buscarPorNroLegajo(String nroLegajo, Connection conn) throws SQLException;
}
