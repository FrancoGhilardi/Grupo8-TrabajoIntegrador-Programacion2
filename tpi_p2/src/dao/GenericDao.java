package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica que define las operaciones básicas de acceso a datos (DAO).
 *
 * <p>
 * Implementa el patrón de diseño <b>Data Access Object (DAO)</b>, separando la
 * lógica de persistencia (JDBC, SQL) de la lógica de negocio.
 * </p>
 *
 * <p>
 * Esta interfaz proporciona un conjunto estándar de operaciones CRUD
 * (Crear, Leer, Actualizar y Eliminar) que pueden reutilizarse para cualquier
 * entidad del sistema (por ejemplo, {@code Empleado}, {@code Legajo}, etc.).
 * </p>
 *
 * @param <T> el tipo de entidad que maneja el DAO (por ejemplo, Empleado o Legajo)
 * @author Grupo 8
 * @version 1.0
 */
public interface GenericDao<T> {

    // ─────────────────────────────────────────────────────────────
    // Métodos con conexión interna (crean y cierran la Connection)
    // ─────────────────────────────────────────────────────────────

    /**
     * Inserta una nueva entidad en la base de datos.
     *
     * @param entidad objeto a persistir.
     * @return la entidad insertada con su ID generado (si aplica).
     * @throws SQLException si ocurre un error durante la operación SQL.
     */
    T crear(T entidad) throws SQLException;

    /**
     * Recupera una entidad por su identificador único.
     *
     * @param id identificador de la entidad.
     * @return un {@link Optional} que contiene la entidad si existe, o vacío si no se encuentra.
     * @throws SQLException si ocurre un error de acceso a la base de datos.
     */
    Optional<T> leer(long id) throws SQLException;

    /**
     * Recupera todas las entidades no eliminadas lógicamente.
     *
     * @return una lista de entidades activas.
     * @throws SQLException si ocurre un error de acceso a la base de datos.
     */
    List<T> leerTodos() throws SQLException;

    /**
     * Actualiza los campos de una entidad existente en la base de datos.
     *
     * @param entidad objeto con los nuevos valores.
     * @return la entidad actualizada.
     * @throws SQLException si ocurre un error de acceso a la base de datos.
     */
    T actualizar(T entidad) throws SQLException;

    /**
     * Realiza una eliminación lógica (marcar como eliminado) de la entidad especificada.
     *
     * @param id identificador de la entidad a eliminar.
     * @throws SQLException si ocurre un error durante la operación SQL.
     */
    void eliminar(long id) throws SQLException;


    // ─────────────────────────────────────────────────────────────
    // Métodos con Connection externa (para uso en transacciones)
    // ─────────────────────────────────────────────────────────────

    /**
     * Inserta una nueva entidad usando una conexión externa.
     * <p>
     * Permite ejecutar varias operaciones dentro de una misma transacción.
     * La conexión no debe cerrarse dentro de este método.
     * </p>
     *
     * @param entidad objeto a persistir.
     * @param conn    conexión JDBC compartida por otras operaciones.
     * @return la entidad insertada con su ID generado.
     * @throws SQLException si ocurre un error durante la operación SQL.
     */
    T crear(T entidad, Connection conn) throws SQLException;

    /**
     * Recupera una entidad por su ID usando una conexión externa.
     *
     * @param id   identificador de la entidad.
     * @param conn conexión JDBC activa.
     * @return un {@link Optional} con la entidad si existe.
     * @throws SQLException si ocurre un error durante la consulta.
     */
    Optional<T> leer(long id, Connection conn) throws SQLException;

    /**
     * Recupera todas las entidades activas usando una conexión externa.
     *
     * @param conn conexión JDBC activa.
     * @return lista de entidades no eliminadas.
     * @throws SQLException si ocurre un error durante la consulta.
     */
    List<T> leerTodos(Connection conn) throws SQLException;

    /**
     * Actualiza una entidad existente usando una conexión externa.
     *
     * @param entidad objeto con los nuevos valores.
     * @param conn    conexión JDBC activa.
     * @return la entidad actualizada.
     * @throws SQLException si ocurre un error durante la actualización.
     */
    T actualizar(T entidad, Connection conn) throws SQLException;

    /**
     * Realiza una eliminación lógica de una entidad usando una conexión externa.
     *
     * @param id   identificador de la entidad a marcar como eliminada.
     * @param conn conexión JDBC activa.
     * @throws SQLException si ocurre un error durante la eliminación lógica.
     */
    void eliminar(long id, Connection conn) throws SQLException;
}
